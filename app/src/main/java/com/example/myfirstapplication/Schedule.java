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
        //filteredlist will be sorted based on num of alt lessons
        List<AllLesson> filteredList = filterLesson.filter();

        if (filteredList == null || this.timetable.getFreeDay().isEmpty()) {
            //impossible tt, hence 0 possible free day
            System.out.println("error 1");
            this.timetable.setPossibleFreeDay(noFreeDay);
            return false;
        }

        //usually fixed lesson is only for lectures
        ArrayList<AllLesson> fixedLessons = new ArrayList<>();
        ArrayList<AllLesson> notFixedLessons = new ArrayList<>();
        for (AllLesson allLesson : filteredList) {
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
                for (int i = 1; i < allLesson.size(); ++ i) {
                    if (!allLesson.getAllTimings().get(i).getNum().equals(lessonCode)) {
                        notFixedLessons.add(allLesson);
                        included = true;
                        break;
                    }
                }
                //all the lectures in the list have the same lesson code
                if (! included) {
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
        FilterFreeDay newFilteredLesson = new FilterFreeDay(notFixedLessons, this.timetable.getPossibleFreeDay(), this.timetable);
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
                                    this.timetable.removeFreeDay(lesson.getDay());
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
                            List<Integer> listOfDays = new ArrayList<>();
                            for (Lesson lesson : ogLesson.getAllTimings()) {
                                if (added) {
                                    if (lesson.getNum().equals(addLesson.getNum())) {
                                        if (this.timetable.check(lesson)) {
                                            this.timetable.add(lesson);
                                            listOfDays.add(lesson.getDay());
                                        } else {
                                            this.timetable.removeLesson(addLesson);
                                            added = false;
                                            listOfDays = new ArrayList<>();
                                        }
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (this.timetable.check(lesson)) {
                                        this.timetable.add(lesson);
                                        addLesson = lesson;
                                        added = true;
                                        listOfDays.add(lesson.getDay());
                                    }
                                }
                            }
                            if (!added) {
                                System.out.println("error 7");
                                this.timetable.setPossibleFreeDay(noFreeDay);
                                return false;
                            } else {
                                for (int days : listOfDays) {
                                    this.timetable.removeFreeDay(days);
                                }
                            }

                        }

                        break;
                    }

                }
            }

        }
        return true;
    }
}

