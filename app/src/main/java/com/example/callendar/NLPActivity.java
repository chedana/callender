package com.example.callendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.callendar.NER_Entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.lang.Character;


public class NLPActivity extends AppCompatActivity {

    private static final String TAG = "YUSUN";
    String conversation;
    int conversation_index = 12;

    String[] tokenized_string = new String[0];
    Span[] date_span = new Span[0];
    Span[] time_span = new Span[0];
    Span[] location_span = new Span[0];
    Span[] person_span = new Span[0];

    String[] sentences = new String[0];

    List<NER_Entity> date_list = new ArrayList<>();
    List<NER_Entity> time_list = new ArrayList<>();
    List<NER_Entity> location_list = new ArrayList<>();
    List<NER_Entity> person_list = new ArrayList<>();

    List<String> positive_words_pool = new ArrayList<>();
    List<String> negative_words_pool = new ArrayList<>();

    double positive_sentiment_weight = 1.0;
    double negative_sentiment_weight = -1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //test area
        /*
        String test_string = "3 a.m. 9 PM 11:25 a.m ";
        String test_result = changeTimeFormat(test_string);
        */
        //test area end

        // Request user permissions
        getPermissions();

        //get positive and negative words pool
        try {
            positive_words_pool = readFileToStringList("./positive_words.txt");
            negative_words_pool = readFileToStringList("./negative_words.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        Log.v(TAG, "start training sentiment model");
        trainSentimentModel();
        Log.v(TAG, "finish training sentiment model");
        */

        //load conversation from txt file
        String conversation_path = getConversationPath(conversation_index);
        conversation = readStringFromFile(conversation_path);

        //Sentence detection
        try {
            sentences = sentenceDetect(conversation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Remove punctuations at end of each sentence
        for(int i=0; i<sentences.length; i++){
            if(!Character.isLetterOrDigit(sentences[i].charAt(sentences[i].length()-1))){
                sentences[i] = sentences[i].substring(0, sentences[i].length()-1);
            }
        }

        conversation = stringArrayToString(sentences);
        conversation = changeTimeFormat(conversation);

        //Tokenize the conversation string
        try {
            tokenized_string = tokenize_string(conversation);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Find date
        try {
            date_span = findDate(tokenized_string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Find time
        try {
            time_span = findTime(tokenized_string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Find location
        try {
            location_span = findLocation(tokenized_string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Find Person
        try {
            person_span = findPerson(tokenized_string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //print span information
        //printSpanInfo();


        spanToEntitylist(date_span, "date");
        spanToEntitylist(time_span, "time");
        spanToEntitylist(location_span, "location");
        spanToEntitylist(person_span, "person");

        //deal with the situation that time recognized as date
        Iterator<NER_Entity> i = date_list.iterator();
        while (i.hasNext()) {
            NER_Entity e = i.next();
            if(e.content.matches("^([0-1][0-9]|[2][0-3]):([0-5][0-9])$")) {
                i.remove();
                time_list.add(e);
            }
        }

        sortListByWeight(date_list);
        sortListByWeight(time_list);
        sortListByWeight(location_list);
        sortListByWeight(person_list);

        //print list information
        printListInfo();



        int a = 1;

    }

    public double getSetimentWeightOfWord(String word){
        word = word.toLowerCase();
        for(String w : positive_words_pool){
            if(word.equals(w))
                return positive_sentiment_weight;
        }

        for(String w : negative_words_pool){
            if(word.equals(w))
                return negative_sentiment_weight;
        }
        return 0.0;
    }

    public List<String> readFileToStringList(String file_name) throws IOException {
        String appPath = getApplicationContext().getFilesDir().getAbsolutePath();
        String file_path = appPath + file_name;
        BufferedReader br = new BufferedReader(new FileReader(file_path));
        List<String> lines = new ArrayList<String>();

        String line = br.readLine();

        while(br != null) {
            lines.add(line);
            line = br.readLine();
        }
        br.close();

        return lines;
    }

    public String stringArrayToString(String[] sentences){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < sentences.length; i++) {
            sb.append(sentences[i]);
            if(i!=sentences.length-1)
                sb.append(" ");
        }
        return sb.toString();
    }

    public String changeTimeFormat(String s){
        String words[] = s.replaceAll("[^a-zA-Z0-9: ]", "").split("\\s+");
        List<String> myStringList = new ArrayList<String>();
        for(int i=0; i<words.length-1; i++) {
            boolean flag = false;

            //this word is a time number
            //check keyword "a.m.", should exclude case like "I am"
            if(words[i+1].toLowerCase().equals("am") && !words[i].toLowerCase().equals("i")){
                String[] time_array = words[i].split(":");
                if(time_array.length==1){
                    if(Integer.parseInt(time_array[0]) < 10){
                        words[i] = "0" + words[i];
                    }
                    words[i] += ":00";
                }
                flag = true;
            }

            if(words[i+1].toLowerCase().equals("pm")){
                String[] time_array = words[i].split(":");
                if(time_array.length==1){
                    words[i] = Integer.toString(Integer.parseInt(words[i]) + 12);
                    words[i] += ":00";
                }
                else if(time_array.length==2){
                    time_array[0] = Integer.toString(Integer.parseInt(time_array[0]) + 12);
                    words[i] = time_array[0] + ":" + time_array[1];
                }
                flag = true;
            }

            // deal with the case like "2:30" without a.m.
            if(words[i].matches("^([0-9]):([0-5][0-9])$")){
                words[i] = "0"+words[i];
                flag = true;
            }

            // deal with the case like "at 4"
            if(words[i].toLowerCase().matches("^(1[0-9]|[0-9]|2[0-4])$")){
                if(words[i].length() == 1)
                    words[i] = "0"+words[i]+":00";
                else if(words[i].length() == 2)
                    words[i] = words[i] + ":00";
                flag = true;
            }

            myStringList.add(words[i]);
            if(flag)
                i+=1;
        }
        myStringList.add(words[words.length-1]);

        String[] result = new String[myStringList.size()];
        result = myStringList.toArray(result);
        return stringArrayToString(result);
    }

    public void getPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        1);

            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        1);

            }
        }
    }

    public void sortListByWeight(List<NER_Entity> my_list){

        Collections.sort(my_list, new Comparator<NER_Entity>() {
            @Override
            public int compare(NER_Entity entity_1, NER_Entity entity_2) {
                if (entity_1.weight < entity_2.weight)
                    return 1;
                return -1;
            }
        });
    }

    public double[] getSentimentOfEachWordInSentence(String sentence){
        String[] sentence_split = sentence.split("\\s+");
        double[] result = new double[sentence_split.length];
        for(int i=0; i<sentence_split.length; i++) {
            result[i] = getSetimentWeightOfWord(sentence_split[i]);
        }

        for(int i=0; i<result.length-1; i++){
            if((result[i] < 0.0) && (result[i+1] < 0.0)){
                result[i] = positive_sentiment_weight;
                result[i+1] = 0.0;
            }
        }
        return result;
    }

    public double nearestSentimentWordTimesDistance(int key_index_start, int key_index_end, double[] d_array){
        boolean reach_left = false;
        boolean reach_right = false;
        int nearest_distance = 1;
        while(!(reach_left && reach_right)){
            if(key_index_start - nearest_distance < 0)
                reach_left = true;
            if(key_index_end + nearest_distance > d_array.length-1)
                reach_right = true;
            if(!reach_left){
                if(d_array[key_index_start-nearest_distance] != 0.0)
                    return d_array[key_index_start-nearest_distance]/nearest_distance*d_array.length;
            }
            if(!reach_right){
                if(d_array[key_index_end+nearest_distance] != 0.0)
                    return d_array[key_index_end+nearest_distance]/nearest_distance*d_array.length;
            }
            nearest_distance++;
        }
        return 0.0;
    }

    public double sumOfDoubleArray(double[] d_array){
        double sum = 0.0;
        for(double d : d_array){
            sum += d;
        }
        return sum;
    }

    public void printListInfo(){
        Log.v(TAG, " ");
        Log.v(TAG, "DATE:");
        for (NER_Entity entity: date_list) {
            Log.v(TAG, "position " + entity.start_index + ":" + entity.end_index + " " + entity.content + " weight=" + entity.weight);
        }
        Log.v(TAG, " ");
        Log.v(TAG, "TIME:");
        for (NER_Entity entity: time_list) {
            Log.v(TAG, "position " + entity.start_index + ":" + entity.end_index + " " + entity.content + " weight=" + entity.weight);
        }
        Log.v(TAG, " ");
        Log.v(TAG, "LOCATION:");
        for (NER_Entity entity: location_list) {
            Log.v(TAG, "position " + entity.start_index + ":" + entity.end_index + " " + entity.content + " weight=" + entity.weight);
        }
        Log.v(TAG, " ");
        Log.v(TAG, "PERSON:");
        for (NER_Entity entity: person_list) {
            Log.v(TAG, "position " + entity.start_index + ":" + entity.end_index + " " + entity.content + " weight=" + entity.weight);
        }
    }

    public void printSpanInfo(){
        Log.v(TAG, "DATE:");
        for (Span s : date_span) {
            Log.v(TAG, s.getStart() + ":" + s.getEnd() + " " + getStringFromSpan(tokenized_string, s));
        }
        Log.v(TAG, "TIME:");
        for (Span s : time_span) {
            Log.v(TAG, s.getStart() + ":" + s.getEnd() + " " + getStringFromSpan(tokenized_string, s));
        }
        Log.v(TAG, "LOCATION:");
        for (Span s : location_span) {
            Log.v(TAG, s.getStart() + ":" + s.getEnd() + " " + getStringFromSpan(tokenized_string, s));
        }
        Log.v(TAG, "PERSON:");
        for (Span s : person_span) {
            Log.v(TAG, s.getStart() + ":" + s.getEnd() + " " + getStringFromSpan(tokenized_string, s));
        }
    }

    public void spanToEntitylist(Span[] spans, String entity_type){
        for (Span s : spans) {
            NER_Entity entity = new NER_Entity();
            entity.content = getStringFromSpan(tokenized_string, s);

            //special case: "next xxx"
            entity.start_index = s.getStart();
            entity.end_index = s.getEnd();
            entity.prob = s.getProb();
            List<Integer> sentence_info = getSentenceFromIndex(entity.start_index);
            String my_sentence = sentences[sentence_info.get(0)];
            double[] senti_array = getSentimentOfEachWordInSentence(my_sentence);
            entity.weight = 50 * entity.prob +
                    150.0 * entity.start_index/tokenized_string.length +
                    50 * sumOfDoubleArray(senti_array) +
                    100 * nearestSentimentWordTimesDistance(entity.start_index - sentence_info.get(1),
                                                        entity.end_index - sentence_info.get(1),
                            senti_array);

            if(entity_type.equalsIgnoreCase("date"))
                date_list.add(entity);
            else if(entity_type.equalsIgnoreCase("time"))
                time_list.add(entity);
            else if(entity_type.equalsIgnoreCase("location"))
                location_list.add(entity);
            else if(entity_type.equalsIgnoreCase("person"))
                person_list.add(entity);
        }
    }

    //not used
    public int getWordIndexFromSentence(String word, String sentence){
        String[] words = sentence.split("\\s+");
        for(int i=0; i<words.length; i++){
            if(words[i].equals(word))
                return i;
        }
        //should not reach here
        return 99999;
    }

    public String getStringFromSpan(String[] tokenized_string, Span s) {
        int start_index = s.getStart();
        int end_index = s.getEnd();
        String result = new String("");
        for (int i = start_index; i < end_index; i++) {
            result += tokenized_string[i];
            if (i != end_index - 1) {
                result += " ";
            }
        }
        return result;
    }

    public String[] tokenize_string(String s) throws IOException {
        String bin_name = "/en-token.bin";
        InputStream is = getInputStream(bin_name);
        TokenizerModel model = new TokenizerModel(is);
        is.close();
        WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
        String[] result = tokenizer.tokenize(s);
        return result;
    }

    public InputStream getInputStream(String bin_name) throws FileNotFoundException {
        String appPath = getApplicationContext().getFilesDir().getAbsolutePath();
        String bin_path = appPath + bin_name;
        return new FileInputStream(bin_path);
    }

    public Span[] findDate(String[] tokenized_s) throws IOException {
        String bin_name = "/en-ner-date.bin";
        InputStream is = getInputStream(bin_name);
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
        NameFinderME nameFinder = new NameFinderME(model);
        Span[] spans = nameFinder.find(tokenized_s);
        return spans;
    }

    public Span[] findTime(String[] tokenized_s) throws IOException {
        String bin_name = "/en-ner-time.bin";
        InputStream is = getInputStream(bin_name);
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
        NameFinderME nameFinder = new NameFinderME(model);
        Span[] spans = nameFinder.find(tokenized_s);
        return spans;
    }

    public Span[] findLocation(String[] tokenized_s) throws IOException {
        String bin_name = "/en-ner-location.bin";
        InputStream is = getInputStream(bin_name);
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
        NameFinderME nameFinder = new NameFinderME(model);
        Span[] spans = nameFinder.find(tokenized_s);
        return spans;
    }


    public Span[] findPerson(String[] tokenized_s) throws IOException {
        String bin_name = "/en-ner-person.bin";
        InputStream is = getInputStream(bin_name);
        TokenNameFinderModel model = new TokenNameFinderModel(is);
        is.close();
        NameFinderME nameFinder = new NameFinderME(model);

        Span[] spans = nameFinder.find(tokenized_s);
        return spans;
    }

    // not used
    /*
    public void trainSentimentModel() {
        MarkableFileInputStreamFactory dataIn = null;
        try {
            String appPath = getApplicationContext().getFilesDir().getAbsolutePath();
            String file_path = appPath + "/tweets.txt";
            dataIn = new MarkableFileInputStreamFactory(
                    new File(file_path));

            ObjectStream<String> lineStream = null;
            lineStream = new PlainTextByLineStream(dataIn, StandardCharsets.UTF_8);
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

            TrainingParameters tp = new TrainingParameters();
            tp.put(TrainingParameters.CUTOFF_PARAM, "2");
            tp.put(TrainingParameters.ITERATIONS_PARAM, "30");

            DoccatFactory df = new DoccatFactory();
            sentiment_model = DocumentCategorizerME.train("en", sampleStream, tp, df);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    */

    public String[] sentenceDetect(String conversation) throws InvalidFormatException, IOException {
        String bin_name = "/en-sent.bin";
        InputStream is = getInputStream(bin_name);
        SentenceModel model = new SentenceModel(is);

        // feed the sentiment_model to SentenceDetectorME class
        SentenceDetectorME sdetector = new SentenceDetectorME(model);

        // detect sentences in the paragraph
        String[] sentences = sdetector.sentDetect(conversation);

        // print the sentences detected, to console
        for(int i=0;i<sentences.length;i++){
            System.out.println(sentences[i]);
        }
        is.close();
        return sentences;
    }


    // the higher the result, the positive the text
    // text is a sentence string
    /*
    public double oldSentimentAnalysis(String text){
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(sentiment_model);

        // double[] outcomes = myCategorizer.categorize(new String[]{text});
        double[] outcomes = myCategorizer.categorize(text.split(" "));
        return 1.0*outcomes[0];
    }

    */

    public String readStringFromFile(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public String getConversationPath(int i){
        String appPath = getApplicationContext().getFilesDir().getAbsolutePath();
        return appPath + "/" + Integer.toString(i) + ".wav.txt";
    }

    // Which sentence does a word belong to?
    // input: the word's index in the conversation
    // output: [sentence's index in sentences, start index of the sentence in conversation]
    public List<Integer> getSentenceFromIndex(int i){

        int word_count = 0;
        for(int j=0; j<sentences.length; j++){
            int this_length = sentences[j].split("\\s+").length;
            if (word_count + this_length >= i) {
                List<Integer> result = new ArrayList<>();
                result.add(j);
                result.add(word_count);
                return result;
            }
            word_count += this_length;
        }
        return null;
    }

}
