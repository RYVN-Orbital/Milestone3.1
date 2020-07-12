package com.example.myfirstapplication;

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
            //hasAdded indicates whether a lecture has been added into the tt
            boolean hasAdded = false;
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
                                        break;
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
}

