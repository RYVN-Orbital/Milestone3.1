package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class helpActivity extends AppCompatActivity {
    public Button closeBtn;
    public TextView helpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        closeBtn = (Button) findViewById(R.id.closeButton);
        helpTextView = (TextView) findViewById(R.id.helpTextView);

        //close Button
        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent editCondIntent = new Intent(getApplicationContext(), ViewTimetable.class);
                startActivity(editCondIntent);
            }
        });
    }
}
