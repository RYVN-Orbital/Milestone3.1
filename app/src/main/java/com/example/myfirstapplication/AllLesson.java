package com.example.myfirstapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

class AllLesson {
    private final String moduleCode;
    private List<Lesson> allTimings;
    private final static Comparator<Lesson> comp = new LessonComparator();
    private boolean isGrouped;

    public AllLesson(String moduleCode, List<Lesson> allTimings) {
        this.moduleCode = moduleCode;
        allTimings.sort(comp);
        this.allTimings = allTimings;

        if (this.allTimings.size() > 1) {
            //remove 1 condition
            boolean checked = false;
            String lessonCode = this.allTimings.get(0).getNum();
            for (int i =1; i < this.allTimings.size(); i++ ) {
                if (this.allTimings.get(i).getNum().equals(lessonCode)) {
                    this.isGrouped = true;
                    checked = true;
                }
            }

            if (!checked) {
                this.isGrouped = false;
            }
        } else {
            this.isGrouped = false;
        }
    }

    public boolean getIsGrouped() {
        return this.isGrouped;
    }


    public List<Lesson> findLesson(String lessonNum) {
        List<Lesson> listLesson = new ArrayList<>();
        for (Lesson lesson : this.allTimings) {
            if (lesson.getNum().equals(lessonNum)) {
                listLesson.add(lesson);
            }
        }
        return listLesson;
    }


    public static AllLesson deepCopy(AllLesson lesson) {
        List<Lesson> lst = new ArrayList<>();
        for (Lesson newLesson : lesson.getAllTimings()) {
            lst.add(newLesson);
        }
        return new AllLesson(lesson.getCode(), lst);
    }

    public String getCode() {
        return this.moduleCode;
    }

    public String toString() {
        return moduleCode;
    }

    //if alltimings only consists of lessons on mon and tues, will return 2
    public int getUniqueDays() {
        List<Integer> days = new ArrayList<>();
        for (Lesson lesson : this.allTimings) {
            if (! days.contains(lesson.getDay())) {
                days.add(lesson.getDay());
            }
        }
        return days.size();
    }

    public void setLesson(List<Lesson> lessons) {
        this.allTimings = lessons;
    }

    public void remove(Lesson lesson) {
        this.allTimings.remove(lesson);
    }

    public List<Lesson> getAllTimings() {
        return this.allTimings;
    }

    public boolean isEmpty() {
        return this.allTimings.isEmpty();
    }

    public int size() {
        return this.allTimings.size();
    }

    //sorted
    public AllLesson possibleTime() {
        int allSize = this.allTimings.size();
        ArrayList<Lesson> possibleLesson = new ArrayList<>();
        possibleLesson.add(this.allTimings.get(0)); //earliest in the week, earliest timing, smallest lesson code
        //Edit Integer to String
        ArrayList<String> includedLessonNum = new ArrayList<>();
        includedLessonNum.add(this.allTimings.get(0).getNum());
        for (int i = 1; i < allSize; i++) {
            Lesson newLesson = this.allTimings.get(i);
            boolean checked = false;
            //REMOVE: 1 condition
            if (includedLessonNum.contains(newLesson.getNum()) /*&& newLesson.getWeeks() == "None"*/) {
                possibleLesson.add(newLesson);
                checked = true;
            } else {
                for (int j = 0; j < possibleLesson.size(); j++) {
                    //same day + same start time will not be added in
                    //same lesson code shld be added in
                    if (possibleLesson.get(j).getStartTime() == newLesson.getStartTime() &&
                            possibleLesson.get(j).getDay() == newLesson.getDay()) {
                        checked = true;
                        break;
                    }
                }
            }
            if (checked == false) {
                possibleLesson.add(newLesson);
                includedLessonNum.add(newLesson.getNum());
            }

        }
        return new AllLesson(this.moduleCode, possibleLesson);
    }

    //exclude same lesson code
    public int numAltLesson() {
        //EDIT: Integer to String
        ArrayList<String> includedCode = new ArrayList<>();
        int numLessons = 0;
        for (Lesson lesson : possibleTime().getAllTimings()) {
            if (! includedCode.contains(lesson.getNum())) {
                numLessons ++;
                includedCode.add(lesson.getNum());
            }
        }
        return numLessons;
    }
}
