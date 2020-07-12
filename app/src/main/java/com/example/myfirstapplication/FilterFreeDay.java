package com.example.myfirstapplication;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

class FilterFreeDay {
    private final List<AllLesson> unfilteredLesson;
    private List<Integer> freeDay;
    private final Timetable timetable;
    private final static Comparator<AllLesson> comp = new LeastAlternativeComparator();

    public FilterFreeDay(List<AllLesson> unfilteredLesson, List<Integer> freeDay, Timetable timetable) {
        this.unfilteredLesson = unfilteredLesson;
        this.freeDay = freeDay;
        this.timetable = timetable;
    }

    public List<AllLesson> filter() {
        if (this.freeDay == null) {
            return this.unfilteredLesson;
        } else if (this.freeDay.isEmpty()) {
            return null;
        } else {
            List<AllLesson> filteredLesson = new ArrayList<>();
            for (AllLesson allLesson : this.unfilteredLesson) {
                filteredLesson.add(AllLesson.deepCopy(allLesson));
            }
            boolean hasError = false;
            for (AllLesson allLesson : filteredLesson) {
                //remove cmi lessons
                if (this.freeDay.isEmpty()) {
                    return null;
                } else {
                    List<Lesson> removeLesson = new ArrayList<>();
                    List<String> removedLessonCode = new ArrayList<>();

                    for (Lesson lesson : allLesson.getAllTimings()) {
                        if (!this.timetable.check(lesson)) {
                            removeLesson.add(lesson);
                            if (!removedLessonCode.contains(lesson.getNum())) {
                                removedLessonCode.add(lesson.getNum());
                            }
                        } else if (this.freeDay.contains(lesson.getDay())) {
                            removeLesson.add(lesson);
                            //lesson code not inside the list
                            if (!removedLessonCode.contains(lesson.getNum())) {
                                removedLessonCode.add(lesson.getNum());
                            }
                        }
                    }

                    if (allLesson.getIsGrouped()) {
                        //double check, remove lessons w the same lesson code when one of them falls on free day
                        for (Lesson lesson : allLesson.getAllTimings()) {
                            //remove same code
                            if (removedLessonCode.contains(lesson.getNum()) && !removeLesson.contains(lesson)) {
                                removeLesson.add(lesson);
                            }
                        }
                    }

                    //not all lessons are removed
                    if (removeLesson.size() != allLesson.getAllTimings().size()) {
                        for (Lesson lesson : removeLesson) {
                            allLesson.remove(lesson);
                        }
                        continue;
                    } else {
                        //everything in the removeLesson list lie on the free day
                        //get the first possible set out first (shld hv at least one free day)
                        removeLesson = new ArrayList<>();
                        boolean included = false;

                        String currLessonCode = "";

                        for (Lesson lesson : allLesson.getAllTimings()) {
                            if (included) {
                                //in a set
                                if (currLessonCode.equals(lesson.getNum())) {
                                    //if take this lesson, impossible tt cuz no free day
                                    if (this.freeDay.contains(lesson.getDay()) && this.freeDay.size() == 1) {
                                        //remove the timing cuz it doesnt work
                                        for (Lesson wrongLesson : removeLesson) {
                                            if (!this.freeDay.contains(wrongLesson.getDay())) {
                                                this.freeDay.add(wrongLesson.getDay());
                                            }
                                        }
                                        removeLesson = new ArrayList<>();
                                        included = false;
                                    } else {    //check wht can put in tt
                                        if (this.timetable.check(lesson)) {
                                            removeLesson.add(lesson);
                                            if (this.freeDay.contains(lesson.getDay())) {
                                                this.freeDay.remove((Integer) lesson.getDay());
                                            }
                                        } else { //cannot put in tt, need restart
                                            for (Lesson wrongLesson : removeLesson) {
                                                if (!this.freeDay.contains(wrongLesson.getDay())) {
                                                    this.freeDay.add(wrongLesson.getDay());
                                                }
                                            }
                                            removeLesson = new ArrayList<>();
                                            included = false;
                                        }
                                    }
                                } else {
                                    break;
                                } //different lesson number
                            } else { //not included
                                if (this.freeDay.size() > 0 && (!currLessonCode.equals(lesson.getNum()))) {
                                    if (this.timetable.check(lesson)) {
                                        removeLesson.add(lesson);
                                        if (this.freeDay.contains(lesson.getDay())) {
                                            this.freeDay.remove((Integer) lesson.getDay());
                                        }
                                        currLessonCode = lesson.getNum();
                                        included = true;
                                    } else {
                                        currLessonCode = lesson.getNum();
                                    }
                                } else if (currLessonCode.equals(lesson.getNum())) {
                                    continue;
                                } else {
                                    return null;
                                }
                            }
                        }
                        //hasError = true;
                        //break;
                    }
                }
            }
                if (this.freeDay.isEmpty()) {
                    return null;
                } else {
                    filteredLesson.sort(comp);
                    return filteredLesson;
                }
        }
    }
}




