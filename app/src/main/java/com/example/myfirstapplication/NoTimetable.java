package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoTimetable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_timetable);

        //generate timetable button to set criteria and conditions for new timetable
        Button generateTimetableButton = (Button) findViewById(R.id.generateTimetableButton);
        generateTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent generateNewTimetable = new Intent(getApplicationContext(), EditModules.class);
                startActivity(generateNewTimetable);
            }
        });
    }
}
