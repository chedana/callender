package com.example.callendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ScheduleLists extends AppCompatActivity {


    private ListView entryListView;
    List<String> entries = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_lists);

        entries.addAll(((Appointments)this.getApplication()).get_appointments());

        entryListView = findViewById(R.id.entry_list);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, entries);
        entryListView.setAdapter(adapter);

        entryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //entries.add(Integer.toString(position));
                //adapter.notifyDataSetChanged();

                Intent intent = new Intent(ScheduleLists.this, SingleSchedule.class);
                intent.putExtra("position",position);
                startActivity(intent);

            }
        });

    }


}

