package com.example.myfirstapplication;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NUSModsAPI {

    //create connection and fetch the data
    //ioexception happens when either the stream itself is corrupted
    //or some error occurred during reading the data
    public static String request(String uri) throws IOException {
        StringBuilder sb = new StringBuilder();
        URL url = new URL(uri);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bin = new BufferedReader(new InputStreamReader(in));

            //temporary string to hold each line read from the reader
            String inputLine = "";
            while ((inputLine = bin.readLine()) != null) {
                sb.append(inputLine);
            }
        } catch (FileNotFoundException e) {
            sb.append("no module found");
        } finally {
            urlConnection.disconnect();
        }
        return sb.toString();
    }

    //for now, since each link is only info for one mod
    //sort all the json object into lesson objects and initialise the list
    public static List<Lesson> fetchLessonTimings(String modCode) throws IOException, JSONException {

        //create variable to hold return data
        ArrayList<Lesson> allLessonTimings = new ArrayList<>();
        String moduleCode = modCode;

        //form api website
        String website = "https://api.nusmods.com/v2/2019-2020/modules/";
        String jsonWeb = ".json";

        //fetch json data and parse into string
        String moduleInfo = website + moduleCode + jsonWeb;
        String result = NUSModsAPI.request(moduleInfo);

        if (result.equals("no module found")) {
            return null;
        } else {

            //parse the JSON string
            JSONObject jsonLessons = new JSONObject(result);

            Iterator<String> listOfKeys = jsonLessons.keys();
            //while (listOfKeys.hasNext()) {
            // get timetable of all lessons
            //if (listOfKeys.next() == "semesterData") {
            //get the lessons info for the module
            JSONArray lessonsInfo = jsonLessons.getJSONArray("semesterData");
            //lessonsinfo from semester data is a list of 3 semesters and their timetables
            //get the timing and venue for all the lessons of the module

            JSONObject semData;
            JSONObject sem2 = null;

            for (int i = 0; i < lessonsInfo.length(); i++ ) {
                semData = (JSONObject) lessonsInfo.get(i);
                if (semData.getInt("semester") == 2) {
                    sem2 = semData;
                    break;
                }
            }

            if (sem2 == null) {
                return null;
            }
            /*
            JSONObject sem2;
            if (lessonsInfo.length() == 1) {
                sem2 = (JSONObject) lessonsInfo.get(0);
                if (sem2.getInt("semester") != 2) {
                    return null;
                }

            } else {
                sem2 = (JSONObject) lessonsInfo.get(1);
            }*/

            JSONArray lessonsTimingsForSem2 = sem2.getJSONArray("timetable");
            for (int j = 0; j < lessonsTimingsForSem2.length(); j++) {
                //get the necessary lesson details for each class
                JSONObject eachClass = lessonsTimingsForSem2.getJSONObject(j);
                String classNum = eachClass.getString("classNo");
                int startTime = Integer.parseInt(eachClass.getString("startTime"));
                int endTime = Integer.parseInt(eachClass.getString("endTime"));
                String lessonVenue = eachClass.getString("venue");
                String dayOfTheWeek = eachClass.getString("day");
                String lessonType = eachClass.getString("lessonType");
                ArrayList<Integer> listOfWeeks = new ArrayList<>();
                JSONArray weeksArray = eachClass.getJSONArray("weeks");
                if (weeksArray != null) {
                    for (int k = 0; k < weeksArray.length(); k++) {
                        listOfWeeks.add(weeksArray.getInt(k));
                    }
                }
                //create a new lesson and add it to the arraylist of lessons
                Lesson newLesson = new Lesson
                        (moduleCode, classNum, startTime, endTime, dayOfTheWeek, lessonVenue, listOfWeeks, lessonType);
                allLessonTimings.add(newLesson);
            }
            //break;
            //break out of the loop since we only need semester data
            //} else {
            //continue;
            //}
            //}
            return allLessonTimings;
        }
    }
}
