package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class GeneratedTimetable extends AppCompatActivity {
    public static TextView timetableTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated_timetable);
        timetableTextView = (TextView) findViewById(R.id.timetableTV);

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPreferences.edit();

        String timetable = mPreferences.getString(getString(R.string.timetable), "");
        timetableTextView.setText(timetable);
        editor.commit();
    }
}
