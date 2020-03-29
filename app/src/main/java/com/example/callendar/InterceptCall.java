package com.example.callendar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;


import android.app.Notification ;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.content.BroadcastReceiver ;
import android.content.Context ;
import android.content.Intent ;


public class InterceptCall extends BroadcastReceiver {
    String pathSave ="";
    MediaRecorder mediaRecorder;
    boolean receive_call = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (context instanceof com.example.callendar.MainActivity) {
            //modify the map
//            ((com.example.myapplication.MainActivity)context).;
        }

        try{
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                Toast.makeText(context,"Ringing!",Toast.LENGTH_SHORT).show();

            }

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Toast.makeText(context,"Reveived!",Toast.LENGTH_SHORT).show();
                receive_call = true;
                pathSave = context.getExternalFilesDir(null)+ "/"
                        +  "test_audio_record.3gp";
                setupMediaRecorder(context);
                try{
                    mediaRecorder.prepare();
                    mediaRecorder.start();

                }catch(IOException e){
                    e.printStackTrace();
                    Toast.makeText(context,"Failed...",Toast.LENGTH_SHORT).show();

                }
            }
            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(context,"Idle!",Toast.LENGTH_SHORT).show();
                if (receive_call){
                    displayNotification(context);
                }


            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setupMediaRecorder(Context context){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
        Toast.makeText(context,"Recording!",Toast.LENGTH_SHORT).show();

    }
    public  void displayNotification(Context context){
        String message = "This phone call is recorded.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications","MyNotifications",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MyNotifications");
        builder.setSmallIcon(R.drawable.ic_message);
        builder.setContentTitle("Call ended");
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(context, com.example.callendar.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 999, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE
        );
        notificationManager.notify(999,builder.build());
    }


}
