
package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Spinner;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Runnable;


public class ViewTimetable extends AppCompatActivity {
    public static Timetable confirmedTT;
    //store the timetable
    public TextView timetableTextView;
    public Button viewButton;
    public Button infoButton;
    public Button editConditionButton;
    public Button editModsBtn;
    private Handler mainHandler = new Handler();

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private static CheckBox mCheckbox;
    private static Button saveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_timetable);
        viewButton = (Button) findViewById(R.id.viewButton);
        timetableTextView = (TextView) findViewById(R.id.timetableTextView);

        //info about the tt
        infoButton = (Button) findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent getInfoIntent = new Intent(getApplicationContext(), TimetableInfo.class);
                startActivity(getInfoIntent);
            }
        });

        //edit condition
        editConditionButton = (Button) findViewById(R.id.editCondBtn);
        editConditionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent editCondIntent = new Intent(getApplicationContext(), SetRequirement.class);
                startActivity(editCondIntent);
            }
        });

        //edit modules
        editModsBtn = (Button) findViewById(R.id.editModsBtn);
        editModsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent editCondIntent = new Intent(getApplicationContext(), EditModules.class);
                startActivity(editCondIntent);
            }
        });

        viewButton = (Button) findViewById(R.id.viewButton);

        //saving the timetable
        timetableTextView = (TextView) findViewById(R.id.timetableTextView);
        mCheckbox = (CheckBox) findViewById(R.id.checkBox);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        checkSharedPreferences();

        //after you click the save button and the checkbox is checked, the generated tt will be saved
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save the checkbox preference
                if (mCheckbox.isChecked()) {
                    //set a checkbox when the app starts
                    mEditor.putString(getString(R.string.checkbox), "True");
                    mEditor.commit();

                    //save the timetable generated
                    String timetable = timetableTextView.getText().toString();
                    mEditor.putString(getString(R.string.timetable), timetable);
                    mEditor.commit();

                    Intent displayTT = new Intent(ViewTimetable.this, GeneratedTimetable.class);
                    startActivity(displayTT);
                } else {
                    //set a checkbox when the app starts
                    mEditor.putString(getString(R.string.checkbox), "False");
                    mEditor.commit();

                    //save the timetable generated
                    mEditor.putString(getString(R.string.timetable), "");
                    mEditor.commit();
                }
            }
        });
    }

    private void checkSharedPreferences() {
        String checkbox = mPreferences.getString(getString(R.string.checkbox), "False");
        String timetableCreated = mPreferences.getString(getString(R.string.timetable), "");
        //on default, checkbox is not checked and timetable generated is empty

        timetableTextView.setText(timetableCreated);

        if (checkbox.equals("True")) {
            mCheckbox.setChecked(true);
        } else {
            mCheckbox.setChecked(false);
        }
    }


    //background thread of generating the timetable
    public void startThread(View view) {
        generateTimetableRunnable runnable = new generateTimetableRunnable();
        new Thread(runnable).start();
    }

    //background runnable of generating the timetable
    class generateTimetableRunnable implements Runnable {

        @Override
        public void run() {
            int numberOfModules = EditModules.listOfUserInput.size();

            //get information from the prev activity
            String conditionLessonModuleCode = SetRequirement.modEditText.getText().toString();
            String conditionLessonNum = SetRequirement.lessonCodeEditText.getText().toString();
            String conditionLessonType = SetRequirement.typeSpinner.getSelectedItem().toString();

            Module[] modulesTaking = new Module[numberOfModules];
            List<AllLesson> listOfLectures = new ArrayList<>();
            List<AllLesson> listOfLabs = new ArrayList<>();
            List<AllLesson> listOfSectionals = new ArrayList<>();
            List<AllLesson> listOfRecitations = new ArrayList<>();
            List<AllLesson> listOfTutorials = new ArrayList<>();
            List<ExamDateTime> examTimings = new ArrayList<>();
            boolean hasError = false;

            try {
                Timetable tt = new Timetable(1);

                for (int i = 0; i < numberOfModules; ++i) {
                    String newModuleCode = EditModules.listOfUserInput.get(i);
                    List<Lesson> lessonsList = NUSModsAPI.fetchLessonTimings(newModuleCode);

                    /*new - check for exam time/date*/
                    ExamDateTime examDate = NUSModsAPI.fetchExamDate(newModuleCode);
                    if (examDate == null) {
                        hasError = true;
                        Intent errorTT = new Intent(getApplicationContext(), NullModuleError.class);
                        startActivity(errorTT);
                        break;
                    } else if (examDate.isEmpty()) {
                        continue;
                    } else if (examTimings.isEmpty()) {
                        examTimings.add(examDate);
                    } else {
                        for (ExamDateTime curr : examTimings) {
                            if (curr.coincide(examDate)) {
                                hasError = true;
                                Intent errorTT = new Intent(getApplicationContext(), ExamClashError.class);
                                startActivity(errorTT);
                                break;
                            }

                        }
                        examTimings.add(examDate);
                    }
                    /*new*/

                    Module newModule = DataManagement.makeModule(newModuleCode, lessonsList);
                    if (newModule == null) {
                        hasError = true;
                        Intent errorTT = new Intent(getApplicationContext(), NullModuleError.class);
                        startActivity(errorTT);
                        break;
                    } else {
                        modulesTaking[i] = newModule;


                        if (newModule.getCode().equals(conditionLessonModuleCode)) {
                            List<Lesson> conditionLesson = newModule.getLesson(conditionLessonNum, conditionLessonType);
                            if (conditionLesson.isEmpty()) {
                                hasError = true;
                                //System.out.println("Error");
                                //cant find lesson
                                Intent errorTT = new Intent(getApplicationContext(), LessonNotFoundError.class);
                                startActivity(errorTT);
                                break;
                            } else {
                                for (Lesson lesson : conditionLesson) {
                                    if (tt.check(lesson)) {
                                        tt.add(lesson);
                                    } else {
                                        hasError = true;
                                        //System.out.println("Error");
                                        //if the lesson cant be added to the tt
                                        Intent errorTT = new Intent(getApplicationContext(), ErrorTimetable.class);
                                        startActivity(errorTT);
                                        break;
                                    }
                                }
                                //Empty the All" " list in the module
                                boolean updateBoolean = newModule.updateAllLesson(conditionLessonType);
                                if (!updateBoolean) {
                                    hasError = true;
                                    System.out.println("Update Error");
                                }
                            }
                        }


                        if (!newModule.getLect().isEmpty()) {
                            AllLesson lecture = newModule.getLect();
                            listOfLectures.add(lecture);
                            if (lecture.getUniqueDays() == 1 && tt.getFreeDay().contains(lecture.getAllTimings().get(0).getDay())) {
                                tt.removeFreeDay(lecture.getAllTimings().get(0).getDay());
                            }
                        }

                        if (!newModule.getLab().isEmpty()) {
                            AllLesson lab = newModule.getLab();
                            listOfLabs.add(lab);
                            if (lab.getUniqueDays() == 1 && tt.getFreeDay().contains(lab.getAllTimings().get(0).getDay())) {
                                tt.removeFreeDay(lab.getAllTimings().get(0).getDay());
                            }
                        }

                        if (!newModule.getSec().isEmpty()) {
                            AllLesson sec = newModule.getSec();
                            listOfSectionals.add(sec);
                            if (sec.getUniqueDays() == 1 && tt.getFreeDay().contains(sec.getAllTimings().get(0).getDay())) {
                                tt.removeFreeDay(sec.getAllTimings().get(0).getDay());
                            }
                        }

                        if (!newModule.getRec().isEmpty()) {
                            AllLesson rec = newModule.getRec();
                            listOfRecitations.add(rec);
                            if (rec.getUniqueDays() == 1 && tt.getFreeDay().contains(rec.getAllTimings().get(0).getDay())) {
                                tt.removeFreeDay(rec.getAllTimings().get(0).getDay());
                            }
                        }

                        if (!newModule.getTut().isEmpty()) {
                            AllLesson tut = newModule.getTut();
                            listOfTutorials.add(tut);
                            if (tut.getUniqueDays() == 1 && tt.getFreeDay().contains(tut.getAllTimings().get(0).getDay())) {
                                tt.removeFreeDay(tut.getAllTimings().get(0).getDay());
                            }
                        }
                    }
                }

                if (!hasError) {

                    LessonSimulator simulator = new LessonSimulator(listOfLectures, listOfLabs, listOfTutorials, listOfRecitations, listOfSectionals, tt);
                    ViewTimetable.confirmedTT = simulator.generate(tt.getPossibleFreeDay());
                    if (confirmedTT.getPossible()) {
                        System.out.println(confirmedTT);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timetableTextView.setText(confirmedTT.toString());
                            }
                        });
                    } else {
                        //error: cannot print timetable
                        System.out.println("Error");
                        ViewTimetable.confirmedTT = null;
                        Intent errorTT = new Intent(getApplicationContext(), ErrorTimetable.class);
                        startActivity(errorTT);
                    }
                }
            } catch (
                    IOException e) {
                e.printStackTrace();
            } catch (
                    JSONException e) {
                e.printStackTrace();
            }
        }
    }
}














