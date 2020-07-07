package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class TimetableInfo extends AppCompatActivity {
    public ListView modulesListView;
    public TextView criteriaTextView;
    public TextView specificLessonTextView;
    public TextView lessonTextView;
    public String lesson;
    public Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_info);

        //adding the modules to the list
        ArrayList<String> modulesList = new ArrayList<>();
        for (int i = 0; i <EditModules.listOfUserInput.size(); i++) {
            modulesList.add(EditModules.listOfUserInput.get(i));
        }

        //creates array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modulesList);
        modulesListView = (ListView) findViewById(R.id.modulesListView);
        modulesListView.setAdapter(adapter);

        //criteria textView
        criteriaTextView = (TextView) findViewById(R.id.criteriaTextView);

        //specific lesson textview
        specificLessonTextView = (TextView) findViewById(R.id.specificLessonTextView);
        lessonTextView = (TextView) findViewById(R.id.conditionTextView);
        String conditionLessonModuleCode = SetRequirement.modEditText.getText().toString();
        String conditionLessonNum = SetRequirement.lessonCodeEditText.getText().toString();
        String conditionLessonType = SetRequirement.typeSpinner.getSelectedItem().toString();
        lesson = conditionLessonModuleCode + " " + conditionLessonType + " " + conditionLessonNum;
        lessonTextView.setText(lesson);

        //close Button
        closeButton = (Button) findViewById(R.id.closeInfoBtn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), ViewTimetable.class);
                startActivity(startIntent);

            }
        });
    }
}

