package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class GeneratedTimetable extends AppCompatActivity {
    public TextView timetableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_timetable);
        timetableTextView = (TextView) findViewById(R.id.timetableTV);
        timetableTextView.setText(ViewTimetable.confirmedTT.toString());
    }
}
