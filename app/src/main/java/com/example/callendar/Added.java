package com.example.callendar;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import android.os.Bundle;

public class Added extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added);

        final Button btn = (Button) findViewById(R.id.return_to_main);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Added.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button btn_reedit = (Button) findViewById(R.id.return_to_main2);
        btn_reedit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Added.super.onBackPressed();
            }
        });

        TextView time_t = (TextView) findViewById(R.id.textView15);
        TextView date_t = (TextView) findViewById(R.id.textView17);
        TextView event_t = (TextView) findViewById(R.id.textView18);
        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        String date = intent.getStringExtra("date");
        String event = intent.getStringExtra("event");
        time_t.setText(time);
        date_t.setText(date);
        event_t.setText(event);
    }
}
