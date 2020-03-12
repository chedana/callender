package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;





public class InterceptCall extends BroadcastReceiver {
    String pathSave ="";
    MediaRecorder mediaRecorder;
    @Override
    public void onReceive(Context context, Intent intent) {

        try{
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                Toast.makeText(context,"Ringing!",Toast.LENGTH_SHORT).show();

            }

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Toast.makeText(context,"Reveived!",Toast.LENGTH_SHORT).show();

                pathSave = context.getExternalFilesDir(null)+ "/"
                            +  "test_audio_record.3gp";
                    setupMediaRecorder(context);
                    try{
                        Toast.makeText(context,"Recording..."+pathSave, Toast.LENGTH_SHORT).show();

                        mediaRecorder.prepare();
                        mediaRecorder.start();

                    }catch(IOException e){
//                        Toast.makeText(context,"Fail...", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }

                    Toast.makeText(context,"Recording...", Toast.LENGTH_SHORT).show();
            }
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(context,"Idle!",Toast.LENGTH_SHORT).show();
                Toast.makeText(context,"Stopped...", Toast.LENGTH_SHORT).show();
                mediaRecorder.stop();

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setupMediaRecorder(Context context){
        Toast.makeText(context,"Setting up...", Toast.LENGTH_SHORT).show();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

}
