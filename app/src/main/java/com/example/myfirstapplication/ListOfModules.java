package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ListOfModules extends AppCompatActivity {
    public ListView modulesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_modules);
        // edit button to edit the list of modules
        Button editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent editModules = new Intent(getApplicationContext(), EditModules.class);
                startActivity(editModules);
            }
        });
        //creates the list of modules to be displayed in listview and adds modules
        ArrayList<String> modulesList = new ArrayList<>();
        for (int i = 0; i <EditModules.listOfUserInput.size(); i++) {
            modulesList.add(EditModules.listOfUserInput.get(i));
        }

        //creates arrayadapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modulesList);
        modulesListView = (ListView) findViewById(R.id.modulesListView);
        modulesListView.setAdapter(adapter);
        /*
        modulesList = (TextView) findViewById(R.id.modulesListTextView);
        modulesList.setText(EditModules.listOfModules.toString());

*/
        // view timetable button to view current timetable
        Button viewTimetableButton = (Button) findViewById(R.id.viewTimetableButton);
        viewTimetableButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (GeneratedTimetable.timetableTextView == null) {
                    Intent noTimetable = new Intent(getApplicationContext(), NoTimetable.class);
                    startActivity(noTimetable);
                } else {
                    Intent viewTimetable = new Intent(getApplicationContext(), GeneratedTimetable.class);
                    startActivity(viewTimetable);
                }
            }});

        //generate button to set requirement for new timetable
        Button generateTimetableButton = (Button) findViewById(R.id.generateTimetableButton);
        generateTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent generateNewTimetable = new Intent(getApplicationContext(), SetRequirement.class);
                startActivity(generateNewTimetable);
            }
        });
    }
}

/*
        //ListView listOfModules = (ListView) findViewById(R.id.listView);
        //capture the textview and set the string as its text
        TextView modulesListTextView = (TextView) findViewById(R.id.modulesListTextView);
        //get the intent that started this activity and extract the string
        Intent intentToGetMod = getIntent();
        String module = intentToGetMod.getStringExtra("message");
        modulesListTextView.setText(module);
    }
}
 */
