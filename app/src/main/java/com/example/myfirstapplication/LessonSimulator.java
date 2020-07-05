package com.example.myfirstapplication;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class LessonSimulator {
    //remove all the same day/same time lessons
    private List<AllLesson> listOfLectures = new ArrayList<>();
    private List<AllLesson> listOfLabs = new ArrayList<>();
    private List<AllLesson> listOfTutorials = new ArrayList<>();
    private List<AllLesson> listOfRecitations = new ArrayList<>();
    private List<AllLesson> listOfSectionals = new ArrayList<>();
    private Timetable timetable;
    private final static Comparator<AllLesson> comp = new LeastAlternativeComparator();

    //sort the list by no of alternative days then module code
    public LessonSimulator (List<AllLesson> allLect, List<AllLesson> allLabs, List<AllLesson> allTut,
                            List<AllLesson> allRec, List<AllLesson> allSec, Timetable timetable) {
        //possibleTime() returns AllLesson
        for (AllLesson lect : allLect) {	//if there are 5 mods, there will be 5 list inside
            this.listOfLectures.add(lect.possibleTime());
        }
        this.listOfLectures.sort(comp);

        for (AllLesson lab : allLabs) {
            this.listOfLabs.add(lab.possibleTime());
        }
        this.listOfLabs.sort(comp);

        for (AllLesson tut : allTut) {
            this.listOfTutorials.add(tut.possibleTime());
        }
        this.listOfTutorials.sort(comp);

        for (AllLesson rec : allRec) {
            this.listOfRecitations.add(rec.possibleTime());
        }
        this.listOfRecitations.sort(comp);

        for (AllLesson sec : allSec) {
            this.listOfSectionals.add(sec.possibleTime());
        }
        this.listOfSectionals.sort(comp);

        this.timetable = timetable;
    }

    public Timetable generate(List<Integer> free) {
        boolean lectStage = false;
        boolean labStage = false;
        boolean secStage = false;
        boolean recStage = false;
        boolean tutStage = false;
        List<Integer> freeDay = free;

        //all the list are sorted alr
        if (this.listOfLectures.isEmpty()) {
            lectStage = true;
        } else {
            Schedule lectureSchedule = new Schedule(this.listOfLectures, this.timetable);
            lectStage = lectureSchedule.scheduling(freeDay);
            freeDay = this.timetable.getPossibleFreeDay();
        }
        if (lectStage) {	//passed lectStage aka has at least one freeDay
            if (this.listOfLabs.isEmpty()) {
                labStage = true;
            } else {
                Schedule labSchedule = new Schedule(this.listOfLabs, this.timetable);
                labStage = labSchedule.scheduling(freeDay);
                freeDay = this.timetable.getPossibleFreeDay();
            }
        }
        if (labStage) {
            if (this.listOfSectionals.isEmpty()) {
                secStage = true;
            } else {
                Schedule secSchedule = new Schedule(this.listOfSectionals, this.timetable);
                secStage = secSchedule.scheduling(freeDay);
                freeDay = this.timetable.getPossibleFreeDay();
            }
        }

        if (secStage) {
            if (this.listOfRecitations.isEmpty()) {
                recStage = true;
            } else {
                Schedule recSchedule = new Schedule(this.listOfRecitations, this.timetable);
                recStage = recSchedule.scheduling(freeDay);
                freeDay = this.timetable.getPossibleFreeDay();
            }
        }

        if (recStage) {
            if (this.listOfTutorials.isEmpty()) {
                tutStage = true;
            } else {
                Schedule tutSchedule = new Schedule(this.listOfTutorials, this.timetable);
                tutStage = tutSchedule.scheduling(freeDay);
            }
        }

        return this.timetable;
    }

}








