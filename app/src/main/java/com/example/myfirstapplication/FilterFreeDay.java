package com.example.myfirstapplication;

import android.os.Build;

import androidx.annotation.RequiresApi;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<AllLesson> filter() {
        if (this.freeDay == null) {
            return this.unfilteredLesson;
        } else if (this.freeDay.isEmpty()) {
            System.out.println("ffd error 1");
            return null;
        } else {
            List<AllLesson> filteredLesson = new ArrayList<>();
            for (AllLesson allLesson : this.unfilteredLesson) {
                filteredLesson.add(AllLesson.deepCopy(allLesson));
            }

            for (AllLesson allLesson : filteredLesson) {
                //remove cmi lessons

                if (this.freeDay.isEmpty()) {
                    System.out.println("ffd error 2 " + allLesson);
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


                    } else {
                        List<Lesson> lessons = new ArrayList<>();
                        List<Lesson> bestLessons = new ArrayList<>();

                        if (allLesson.getIsGrouped()) {
                            boolean added = false;
                            boolean isPossible = false;
                            int freeDays = 0;
                            int days = 6;

                            for (Lesson lesson : allLesson.getAllTimings()) {
                                List<Integer> listOfDays = new ArrayList<>();
                                lessons = allLesson.findLesson(lesson.getNum());
                                for (Lesson checkLesson : lessons) {
                                    if (!this.timetable.check(checkLesson)) {
                                        isPossible = false;
                                        break;
                                    } else {
                                        if (!listOfDays.contains(checkLesson.getDay())) {
                                            listOfDays.add(checkLesson.getDay());
                                        }
                                        isPossible = true;
                                    }
                                }
                                if (isPossible) {
                                    for (int day : listOfDays) {
                                        if (this.freeDay.contains(day)) {
                                            freeDays++;
                                        }
                                    }
                                    if (freeDays < days) {
                                        days = freeDays;
                                        bestLessons = lessons;
                                    }
                                    break;
                                }
                            }
                            if (isPossible) {
                                added = true;
                                for (Lesson checkedLesson : bestLessons) {
                                    if (this.freeDay.contains(checkedLesson.getDay())) {
                                        this.freeDay.remove((Integer) checkedLesson.getDay());
                                    }
                                }
                            }
                            if (!added) {
                                System.out.println("ffd error 3 " + allLesson);
                                return null;
                            } else {
                                allLesson.setLesson(lessons);
                            }

                        } else {
                            boolean added = false;
                            for (Lesson lesson : allLesson.getAllTimings()) {
                                if (this.timetable.check(lesson)) {
                                    lessons.add(lesson);
                                    allLesson.setLesson(lessons);
                                    added = true;
                                    if (!this.freeDay.contains(lesson.getDay())) {
                                        this.freeDay.remove((Integer) lesson.getDay());
                                    }
                                    break;
                                }
                            }
                            if (!added) {
                                return null;
                            }
                        }
                    }
                }
            }

            if (this.freeDay.isEmpty()) {
                System.out.println("ffd error 4 ");
                return null;
            } else {
                //filteredLesson.sort(comp);
                return filteredLesson;
            }
        }
    }
}

                        /*//everything in the removeLesson list lie on the free day
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
                    }
                }
            }*/
/*            if (this.freeDay.isEmpty()) {
                return null;
            } else {
                filteredLesson.sort(comp);
                return filteredLesson;
            }
        }
    }
}*/




