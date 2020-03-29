package com.example.callendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

public class SingleSchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_schedule);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        String app_name = ((Appointments)this.getApplication()).get_appointment_at(position);

        final TextView time_t_m = (TextView) findViewById(R.id.textView7);
        final TextView date_t_m = (TextView) findViewById(R.id.textView16);
        final TextView event_t_m = (TextView) findViewById(R.id.textView9);


        final Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SingleSchedule.this, Added.class);
                intent.putExtra("date",date_t_m.getText().toString());
                intent.putExtra("time",time_t_m.getText().toString());
                intent.putExtra("event",event_t_m.getText().toString());
                startActivity(intent);
            }
        });


        final Button a_btn = (Button) findViewById(R.id.a_button);
        a_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SingleSchedule.super.onBackPressed();
            }
        });
    }
}
