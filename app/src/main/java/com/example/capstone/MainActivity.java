package com.example.myapplication;
//package com.example.myapplication.snackbar;
//
//package android.support.design.widget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.provider.CalendarContract;
import android.view.View;

import android.net.Uri;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.util.Log;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;

import android.Manifest;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }else{
            // do nothing
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granded!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "No Permission granded!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void AddCalendarEvent(View view) {

        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setData(CalendarContract.Events.CONTENT_URI);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, "Dinner");
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "");
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "");
//        calIntent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=15;BYDAY=TH");

        GregorianCalendar calBegin = new GregorianCalendar(2020, 1, 05, 19, 00);
        GregorianCalendar calEnd = new GregorianCalendar(2020, 1, 05, 20, 00);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);


        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calBegin.getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calEnd.getTimeInMillis());

        startActivity(calIntent);
    }
//        long eventID = 202;
//        ContentResolver cr = getContentResolver();
//        ContentValues values = new ContentValues();
//
//        values.put(CalendarContract.Attendees.ATTENDEE_NAME,"Tony");
//        values.put(CalendarContract.Attendees.ATTENDEE_EMAIL,"chedanapro@gmail.com");
//        values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP,CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
//        values.put(CalendarContract.Attendees.ATTENDEE_TYPE,CalendarContract.Attendees.TYPE_OPTIONAL);
//        values.put(CalendarContract.Attendees.ATTENDEE_STATUS,CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);
//        values.put(CalendarContract.Attendees.EVENT_ID, eventID);

//        Uri uri = cr.insert(CalendarContract.Attendees.CONTENT_URI, values);

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
//            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
//            long eventID = Long.parseLong(uri.getLastPathSegment());
//            Log.i("Calendar", "Event Created, the event id is: " + eventID);
//            Snackbar.make(view, "Jazzercise event added!", Snackbar.LENGTH_SHORT).show();
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
//        }
//    }
}