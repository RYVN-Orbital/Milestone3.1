package com.example.myfirstapplication;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LectureSchedule {
    //sorted based on num of alternative lessons, then module code
    private final List<AllLesson> listOfLessons;
    private final Timetable timetable;
    private final static List<Integer> noFreeDay = new ArrayList<>();
    private final static Comparator<AllLesson> comp = new LeastAlternativeComparator();

    public LectureSchedule(List<AllLesson> listOfLessons, Timetable timetable) {
        this.listOfLessons = listOfLessons;
        this.timetable = timetable;
    }

    //return type is boolean
    //true if the scheduling is successful and vice versa
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean scheduling(List<Integer> freeDay) {

        //usually fixed lesson is only for lectures
        ArrayList<AllLesson> fixedLessons = new ArrayList<>();
        ArrayList<AllLesson> notFixedLessons = new ArrayList<>();
        for (AllLesson allLesson : this.listOfLessons) {
            if (allLesson.size() == 1) {
                fixedLessons.add(allLesson);
            } else if (allLesson.size() == 0) { //shldnt need this condition; put here in case
                continue;
            } else {
                //lect may consists of Lectures with the same lesson code
                //check for similar lesson code
                String lessonCode = allLesson.getAllTimings().get(0).getNum();

                //included = true suggests that the lesson list is added to either fixedLectures or notFixedLectures
                boolean included = false;
                for (int i = 1; i < allLesson.size(); ++i) {
                    if (!allLesson.getAllTimings().get(i).getNum().equals(lessonCode)) {
                        notFixedLessons.add(allLesson);
                        included = true;
                        break;
                    }
                }
                //all the lectures in the list have the same lesson code
                if (!included) {
                    fixedLessons.add(allLesson);
                }
            }
        }

        //after separating them into fixed and not fixed, sort again
        fixedLessons.sort(comp);
        notFixedLessons.sort(comp);

        //adding fixed lectures into tt
        //every lessons inside must be added in (for same lesson code too)
        for (AllLesson newLessonList : fixedLessons) {
            for (Lesson newLesson : newLessonList.getAllTimings()) {
                if (this.timetable.check(newLesson)) {
                    //day of fixedLesson = fixed day
                    //remove fixed day from possible free day
                    this.timetable.removeFreeDay(newLesson.getDay());
                    freeDay.remove((Integer) newLesson.getDay());
                    this.timetable.add(newLesson);
                } else {
                    //if newlecture cant be added
                    //an it is a fixed lect
                    //return error tt
                    System.out.println("error 2");
                    this.timetable.setPossibleFreeDay(noFreeDay);
                    return false;
                }
            }
        }

        //after adding all fixed lectures, no free days at all = impossible
        if (this.timetable.getFreeDay().isEmpty()) {
            System.out.println("error 3");
            return false;
        }

        //there are free days
        FilterFreeDay newFilteredLesson = new FilterFreeDay(notFixedLessons, freeDay, this.timetable);
        //filter(): sort alr
        List<AllLesson> newFilteredList = newFilteredLesson.filter();

        //when return null, no free day
        //not sure
        if (newFilteredList == null) {
            System.out.println("error 4");
            this.timetable.setPossibleFreeDay(noFreeDay);
            return false;
        }

        for (AllLesson listLessons : newFilteredList) {
            //hasAdded indicates whether a lecture has been added into the tt
            /*boolean hasAdded = false;
            String lessonNum = "";
            Lesson addedLesson = null;*/
            //getAlltimings == possibleTime() cuz alr filtered in lesson Sim

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
                    //}

                    if (isPossible) {
                        hasAdded = true;
                        for (Lesson lesson : newLessonList) {
                            this.timetable.add(lesson);
                        }
                        break;
                    } else {
                        hasAdded = false;
                    }
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
                                //fixed days
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

            /*for (Lesson newLesson : listLessons.getAllTimings()) {
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

        }*/

     /*   ArrayList<Integer> removeDays = new ArrayList<>();
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
