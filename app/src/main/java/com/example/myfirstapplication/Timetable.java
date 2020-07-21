package com.example.myfirstapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Arrays;

public class Timetable {
    private final int ttNum;
    private List<Lesson> monday = new ArrayList<>();
    private List<Lesson> tuesday = new ArrayList<>();
    private List<Lesson> wednesday = new ArrayList<>();
    private List<Lesson> thursday = new ArrayList<>();
    private List<Lesson> friday = new ArrayList<>();
    private List<Lesson> saturday = new ArrayList<>();
    private List<Integer> possibleFreeDay = new ArrayList<>();

    //timetable number is just a unique code for the tt
    public Timetable (int ttNum) {
        this.ttNum = ttNum;
        List<Integer> days = Arrays.asList(1,2,3,4,5,6);
        this.possibleFreeDay.addAll(days);
    }

    public int getNum() {
        return this.ttNum;
    }

    @Override
    public String toString() {
        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        List<List<Lesson>> allLessons = Arrays.asList(monday, tuesday, wednesday, thursday, friday, saturday);
        String str = "";
        for (int i = 0; i < allLessons.size(); i++ ) {
            str += "\n"+ "\n" + days.get(i);
            List<Lesson> lessons = allLessons.get(i);
            for (Lesson lesson : lessons) {
                str += "\n" + lesson;
            }
        }
        return str;
    }

    //used when theres fixed lesson
    public void removeFreeDay(int day) {
        if (possibleFreeDay.contains(day)) {
            possibleFreeDay.remove((Integer) day);
        }
    }

    public List<Integer> getPossibleFreeDay() {
        return this.possibleFreeDay;
    }

    //method to add the first lesson on the day
    public void add(Lesson lesson) {
        SlotsComparator comp = new SlotsComparator();
        int dayOfLesson = lesson.getDay();
        if (dayOfLesson == 1) {
            this.monday.add(lesson);
            this.monday.sort(comp);
        } else if (dayOfLesson == 2) {
            this.tuesday.add(lesson);
            this.tuesday.sort(comp);
        } else if (dayOfLesson == 3) {
            this.wednesday.add(lesson);
            this.wednesday.sort(comp);
        } else if (dayOfLesson == 4) {
            this.thursday.add(lesson);
            this.thursday.sort(comp);
        } else if (dayOfLesson == 5) {
            this.friday.add(lesson);
            this.friday.sort(comp);
        } else {
            this.saturday.add(lesson);
            this.saturday.sort(comp);
        }
    }

    public void removeLesson(Lesson lesson) {
        int day = lesson.getDay();
        if (day == 1) {
            this.monday.remove(lesson);
        } else if (day == 2) {
            this.tuesday.remove(lesson);
        } else if (day == 3) {
            this.wednesday.remove(lesson);
        } else if (day == 4) {
            this.thursday.remove(lesson);
        } else if (day == 5) {
            this.friday.remove(lesson);
        } else {
            this.saturday.remove(lesson);
        }
    }

    //method to get free day
    public List<Integer> getFreeDay() {
        List<Integer> freeDays = new ArrayList<>();
        if (this.monday.isEmpty()) {
            freeDays.add(1);
        }
        if (this.tuesday.isEmpty()) {
            freeDays.add(2);
        }
        if (this.wednesday.isEmpty()) {
            freeDays.add(3);
        }
        if (this.thursday.isEmpty()) {
            freeDays.add(4);
        }
        if (this.friday.isEmpty()) {
            freeDays.add(5);
        }
        if (this.saturday.isEmpty()) {
            freeDays.add(6);
        }
        return freeDays;
    }

    public boolean getPossible() {
        return possibleFreeDay.size() != 0;
    }


    public void setPossibleFreeDay(List<Integer> lst) {
        possibleFreeDay = lst;
    }


    //Check whether a lesson can be added into the tt
    public boolean check(Lesson newLesson) {
        List<Lesson> selectedList;
        int dayOfNewLesson = newLesson.getDay();
        int startOfNewLesson = newLesson.getStartTime();
        int endOfNewLesson = newLesson.getEndTime();
        SlotsComparator comp = new SlotsComparator();

        if (dayOfNewLesson == 1) {
            selectedList = monday;
        } else if (dayOfNewLesson == 2) {
            selectedList = tuesday;
        } else if (dayOfNewLesson == 3) {
            selectedList = wednesday;
        } else if (dayOfNewLesson == 4) {
            selectedList = thursday;
        } else if (dayOfNewLesson == 5) {
            selectedList = friday;
        } else {
            selectedList = saturday;
        }
        selectedList.sort(comp);

        if (selectedList.size() == 0) {
            return true;
        } else if (selectedList.size() == 1) {
            //new lesson becomes the very first lesson of the day
            if (selectedList.get(0).getStartTime() >= endOfNewLesson) {
                return true;
                //new lesson becomes the last lesson of the day
            } else if (selectedList.get(0).getEndTime() <= startOfNewLesson) {
                return true;
            } else {
                return false;
            }
        } else {

            for (int i = 0; i <= selectedList.size() - 1; i++) {
                Lesson confirmedLesson = selectedList.get(i);

                //first confirmedlesson
                if (i == 0 && confirmedLesson.getStartTime() >= endOfNewLesson) {
                        return true;
                    }

                if (i == selectedList.size() - 1) {
                    //NewLesson happens after the end of the confirmedLesson
                    if (confirmedLesson.getEndTime() <= startOfNewLesson) {
                        return true;
                    } else {
                        return false;
                    }

                    //others confirmedLesson
                } else {
                    Lesson nextConfirmedLesson = selectedList.get(i + 1);

                    //start time of newLesson is during the confirmedLesson
                    if (confirmedLesson.getStartTime() <= startOfNewLesson
                            && startOfNewLesson <= confirmedLesson.getEndTime()) {
                        return false;
                        //end time of newLesson is during confirmedLesson
                    } else if (confirmedLesson.getStartTime() <= endOfNewLesson
                            && endOfNewLesson <= confirmedLesson.getEndTime()) {
                        return false;
                        //newLesson is in between 2 confirmed lesson
                    } else if (confirmedLesson.getEndTime() <= startOfNewLesson && endOfNewLesson <= nextConfirmedLesson.getStartTime()) {
                        return true;
                    } else {
                        continue;
                    }
                }
            }
            return false;
        }
    }
}
