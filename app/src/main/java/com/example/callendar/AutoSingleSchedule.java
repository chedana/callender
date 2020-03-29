package com.example.callendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

import java.util.List; // import just the List interface
import java.util.ArrayList; // import just the ArrayList class

import java.util.Arrays;


public class AutoSingleSchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_auto_schedule);
        String[]time_list = new String[]{"time1", "time2", "time3"};
        String[]date_list = new String[]{"date1", "date2", "date3"};
        String[]event_list = new String[]{"event1", "event2", "event3"};

        final android.widget.Spinner time_s = findViewById(R.id.spinner_time);
        final android.widget.Spinner date_s = findViewById(R.id.spinner_date);
        final android.widget.Spinner event_s = findViewById(R.id.spinner_event);

        ArrayAdapter<String> adapter_time = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, time_list);
        time_s.setAdapter(adapter_time);
        ArrayAdapter<String> adapter_date = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, date_list);
        date_s.setAdapter(adapter_date);
        ArrayAdapter<String> adapter_event = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, event_list);
        event_s.setAdapter(adapter_event);


        final Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AutoSingleSchedule.this, Added.class);
                intent.putExtra("date",date_s.getSelectedItem().toString());
                intent.putExtra("time",time_s.getSelectedItem().toString());
                intent.putExtra("event",event_s.getSelectedItem().toString());
                startActivity(intent);
            }
        });

        final Button m_b = (Button) findViewById(R.id.m_button);
        m_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AutoSingleSchedule.this, SingleSchedule.class);
                startActivity(intent);
            }
        });

    }


}