package com.example.myfirstapplication;

/*added new package Arrays & List*/
import java.util.List;
import java.util.Arrays;

public /*abstract*/ class Lesson {
    protected final String moduleCode;
    protected final String lessonNum;
    protected final int startTime;
    protected final int endTime;
    protected final int dayOfTheWeek;
    //Edit: added new attribute (string day)
    protected final String day;
    protected final String venue;
    protected Type lessonType;
    protected List<Integer> listOfWeeks;
    protected String typeOfWeeks;

    protected Lesson(String code, String number, int start, int end, String day, String venue, List<Integer> weeks, String typeOfLesson) {
        this.moduleCode = code;
        this.lessonNum = number;
        this.startTime = start;
        this.endTime = end;
        this.day = day;
        this.venue = venue;
        this.listOfWeeks = weeks;

        /*New 1*/
        if (day.equals("Monday")) {
            this.dayOfTheWeek = 1;
        } else if (day.equals("Tuesday")) {
            this.dayOfTheWeek = 2;
        } else if (day.equals("Wednesday")) {
            this.dayOfTheWeek = 3;
        } else if (day.equals("Thursday")) {
            this.dayOfTheWeek = 4;
        } else if (day.equals("Friday")) {
            this.dayOfTheWeek = 5;
        } else if (day.equals("Saturday")) {
            this.dayOfTheWeek = 6;
        } else {
            this.dayOfTheWeek = 7;
        }
        /*New 1*/

        /*New 2*/
        if (typeOfLesson.equals("Sectional Teaching")) {
            this.lessonType = Type.SECTIONAL;
        } else if (typeOfLesson.equals("Lecture")) {
            this.lessonType = Type.LECTURE;
        } else if (typeOfLesson.equals("Recitation")) {
            this.lessonType = Type.RECITATION;
        } else if (typeOfLesson.equals("Tutorial")) {
            this.lessonType = Type.TUTORIAL;
        } else if (typeOfLesson.equals("Laboratory")) {
            this.lessonType = Type.LAB;
        } else {	//error
            this.lessonType = null;
        }

        if (this.listOfWeeks.size() <= 1) {
            this.typeOfWeeks = "None";
        } else {
            List<Integer> oddWeeks = Arrays.asList(1,3,5,7,9,11,13);
            List<Integer> evenWeeks = Arrays.asList(2,4,6,8,10,12);
            boolean included = false;
            if (this.listOfWeeks.get(0) % 2 == 0) { //means even number
                for (int week : this.listOfWeeks) {
                    if (! evenWeeks.contains(week) ) {
                        this.typeOfWeeks = "None";
                        included = true;
                        break;
                    }
                }
                if (included == false) {
                    this.typeOfWeeks = "Even weeks";
                }
            } else {
                for (int week : this.listOfWeeks) {
                    if (! oddWeeks.contains(week)) {
                        this.typeOfWeeks = "None";
                        included = true;
                        break;
                    }
                }
                if (included == false) {
                    this.typeOfWeeks = "None";
                }
            }
        }

    }



    public String getCode() {
        return this.moduleCode;
    }

    public /*int*/ String getNum() {
        return this.lessonNum;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public int getDay() {
        return this.dayOfTheWeek;
    }


    public String getVenue() {
        return this.venue;
    }

    //public abstract Type getType();
    //EDIT METHOD
    public Type getType() {
        return this.lessonType;
    }

    public /*String*/ List<Integer> getWeeks() {
        return this.listOfWeeks;
    }

    /*New 2*/
    public String getTypeOfWeeks() {
        return this.typeOfWeeks;
    }
    /*End of New 2*/


    @Override
    public String toString() {
        return this.startTime + "-" + this.endTime + " " +
                this.moduleCode + " " + this.lessonType + " "+ this.lessonNum + " " + this.venue;
    }
}


