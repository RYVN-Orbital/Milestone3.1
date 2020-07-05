package com.example.myfirstapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Runnable;
import java.util.Set;

public class ViewTimetable extends AppCompatActivity {
    public static Timetable confirmedTT;
    public TextView timetableTextView;
    public Button viewButton;
    private Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_timetable);
        viewButton = (Button) findViewById(R.id.viewButton);
        timetableTextView = (TextView) findViewById(R.id.timetableTextView);
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
            boolean hasError = false;

            try {
                Timetable tt = new Timetable(1);

                for (int i = 0; i < numberOfModules; ++i) {
                    String newModuleCode = EditModules.listOfUserInput.get(i);
                    List<Lesson> lessonsList = NUSModsAPI.fetchLessonTimings(newModuleCode);
                    Module newModule = DataManagement.makeModule(newModuleCode, lessonsList);

                    if (newModule == null) {
                        hasError = true;
                        //System.out.println("Null Module Error");
                        //cant find module
                        Intent errorTT = new Intent(getApplicationContext(), NullModuleError.class);
                        startActivity(errorTT);
                        break;
                    } else {
                        modulesTaking[i] = newModule;

                        /*New 1*/
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
                        /*End of New 1*/

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
                    //EDIT: Shifted up
                    //Timetable tt = new Timetable(1);

                    LessonSimulator simulator = new LessonSimulator(listOfLectures, listOfLabs, listOfTutorials, listOfRecitations, listOfSectionals, tt);
                    confirmedTT = simulator.generate(tt.getPossibleFreeDay());
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





