package com.example.myfirstapplication;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

class Schedule {
    //sorted based on num of alternative lessons, then module code
    private final List<AllLesson> listOfLessons;
    private final Timetable timetable;
    private final static List<Integer> noFreeDay = new ArrayList<>();
    private final static Comparator<AllLesson> comp = new LeastAlternativeComparator();

    public Schedule(List<AllLesson> listOfLessons, Timetable timetable) {
        this.listOfLessons = listOfLessons;
        this.timetable = timetable;
    }

    //return type is boolean
    //true if the scheduling is successful and vice versa
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean scheduling(List<Integer> freeDay) {
        FilterFreeDay filterLesson = new FilterFreeDay(this.listOfLessons, freeDay, this.timetable);
        List<AllLesson> filteredList = filterLesson.filter();

        if (filteredList == null || this.timetable.getFreeDay().isEmpty()) {
            //impossible tt, hence 0 possible free day
            System.out.println("error 1");
            this.timetable.setPossibleFreeDay(noFreeDay);
            return false;
        }

        for (AllLesson listLessons : filteredList) {
            List<Lesson> newLessonList;
            boolean isPossible = false;
            boolean hasAdded = false;

            if (listLessons.getIsGrouped()) {
                for (Lesson newLesson : listLessons.getAllTimings()) {
                    newLessonList = listLessons.findLesson(newLesson.getNum());
                    for (Lesson lesson : newLessonList) {
                        if (!this.timetable.check(lesson)) {
                            isPossible = false;
                            break;
                        } else {
                            isPossible = true;
                        }
                    }
                }

                if (isPossible) {
                    hasAdded = true;
                    for (Lesson newLesson : listLessons.getAllTimings()) {
                        this.timetable.add(newLesson);
                    }
                } else {
                    hasAdded = false;
                }
            } else {

                for (Lesson newLesson : listLessons.getAllTimings()) {
                    if (!this.timetable.check(newLesson)) {
                        hasAdded = false;
                    } else {
                        this.timetable.add(newLesson);
                        hasAdded = true;
                        break;
                    }
                }
            }

            if (!hasAdded) {
                for (AllLesson ogLesson : this.listOfLessons) {
                    boolean added = false;
                    if (ogLesson.getCode().equals(listLessons.getCode())) {
                        //single lesson
                        if (!ogLesson.getIsGrouped()) {
                            for (Lesson lesson : ogLesson.getAllTimings()) {
                                if (this.timetable.check(lesson)) {
                                    added = true;
                                    this.timetable.add(lesson);
                                    break;
                                }
                            }
                            if (!added) {
                                System.out.println("error 6");
                                this.timetable.setPossibleFreeDay(noFreeDay);
                                return false;

                            } else { //added
                                ArrayList<Integer> removeDays = new ArrayList<>();
                                for (int day : this.timetable.getPossibleFreeDay()) {
                                    if (!this.timetable.getFreeDay().contains(day)) {
                                        removeDays.add(day);
                                    }
                                }

                                for (int day : removeDays) {
                                    this.timetable.removeFreeDay(day);
                                }
                                continue;
                                //return true;
                            }

                            //multiple lessons
                        } else {
                            List<Lesson> bestLessonList = new ArrayList<>();
                            int leastDays = 7;
                            int currDays = 0;
                            isPossible = false;
                            List<Lesson> currentLessonList;

                            for (Lesson lesson : ogLesson.getAllTimings()) {
                                currentLessonList = ogLesson.findLesson(lesson.getNum());
                                for (Lesson checkLesson : currentLessonList) {
                                    if (!this.timetable.check(checkLesson)) {
                                        isPossible = false;
                                        break;
                                    } else {
                                        isPossible = true;
                                    }
                                }

                                if (isPossible) {
                                    List<Integer> days = this.timetable.getFreeDay();
                                    for (Lesson checkedLesson : currentLessonList) {
                                        if (days.contains(checkedLesson.getDay())) {
                                            currDays++; //least the better, means more freeDay
                                        }
                                    }
                                    if (currDays < leastDays) {
                                        bestLessonList = currentLessonList;
                                    }
                                } // else continue to look for other possible lessons
                            }

                            if (bestLessonList.isEmpty()) {
                                added = false;
                            } else {
                                for (Lesson lesson : bestLessonList) {
                                    this.timetable.add(lesson);
                                }
                                added = true;
                            }
                        }

                        if (!added) {
                            System.out.println("error 7");
                            this.timetable.setPossibleFreeDay(noFreeDay);
                            return false;

                        } else { //added
                            ArrayList<Integer> removeDays = new ArrayList<>();
                            for (int day : this.timetable.getPossibleFreeDay()) {
                                if (!this.timetable.getFreeDay().contains(day)) {
                                    removeDays.add(day);
                                }
                            }

                            for (int day : removeDays) {
                                this.timetable.removeFreeDay(day);
                            }
                            //return true;
                        }


                    }
                }
            } else { //hasadded
                ArrayList<Integer> removeDays = new ArrayList<>();
                for (int day : this.timetable.getPossibleFreeDay()) {
                    if (!this.timetable.getFreeDay().contains(day)) {
                        removeDays.add(day);
                    }
                }

                for (int day : removeDays) {
                    this.timetable.removeFreeDay(day);
                }
                //return true;
            }
        }
        return true;
    }

}
            //hasAdded indicates whether a lecture has been added into the tt
 /*           boolean hasAdded = false;
            String lessonNum = "";
            Lesson addedLesson = null;
            //getAlltimings == possibleTime() cuz alr filtered in lesson Sim
            for (Lesson newLesson : listLessons.getAllTimings()) {
                if (hasAdded) { //ensure that we have included all the lectures w the same lesson code
                    if (lessonNum.equals(newLesson.getNum())) {
                        if (this.timetable.check(newLesson)) {
                            this.timetable.add(newLesson);
                        } else { //timing coincides
                            //this set of the same lessonCode cannot be added
                            this.timetable.removeLesson(addedLesson);
                            hasAdded = false;
                            continue;
                        }
                    } else {
                        break;
                    }
                } else { 	//lecture for the mod is not added yet
                    if (this.timetable.check(newLesson)) {
                        this.timetable.add(newLesson);
                        lessonNum = newLesson.getNum();
                        addedLesson = newLesson;
                        hasAdded = true;
                    }
                }
            }


            //lesson for that mod is not added at all (means coincide w other lessons)
            //not sure
            if (! hasAdded) {

                for (AllLesson ogLesson : this.listOfLessons) {
                    boolean added = false;
                    if (ogLesson.getCode().equals(listLessons.getCode())) {
                        if (!ogLesson.getIsGrouped()) {
                            for (Lesson lesson : ogLesson.getAllTimings()) {
                                if (this.timetable.check(lesson)) {
                                    added = true;
                                    this.timetable.add(lesson);
                                    break;
                                }
                            }
                            if (!added) {
                                System.out.println("error 6");
                                this.timetable.setPossibleFreeDay(noFreeDay);
                                return false;
                            }
                        } else {
                            Lesson addLesson = null;
                            for (Lesson lesson : ogLesson.getAllTimings()) {
                                if (added) {
                                    if (lesson.getNum().equals(addLesson.getNum())) {
                                        if (this.timetable.check(lesson)) {
                                            this.timetable.add(lesson);
                                        } else {
                                            this.timetable.removeLesson(addLesson);
                                            added = false;
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    if (this.timetable.check(lesson)) {
                                        this.timetable.add(lesson);
                                        addLesson = lesson;
                                        added = true;
                                    }
                                }
                            }
                            if (!added) {
                                System.out.println("error 7");
                                this.timetable.setPossibleFreeDay(noFreeDay);
                                return false;
                            }

                        }

                        break;
                    }

                }
            }

        }

        ArrayList<Integer> removeDays = new ArrayList<>();
        for (int day : this.timetable.getPossibleFreeDay()) {
            if (!this.timetable.getFreeDay().contains(day)) {
                removeDays.add(day);
            }
        }

        for (int day : removeDays) {
            this.timetable.removeFreeDay(day);
        }

        return true;
    }
}*/

