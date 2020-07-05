package com.example.myfirstapplication;

import java.util.List;

public class DataManagement {

    public static Module makeModule(String modCode, List<Lesson> lessons) {
        if (lessons == null) {
            return null;
        } else {
            Lesson[] arrayLesson = lessons.toArray(new Lesson[0]);
            Module newModule = new Module(modCode, arrayLesson);
            return newModule;
        }
    }
}
