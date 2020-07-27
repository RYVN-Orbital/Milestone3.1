package com.example.myfirstapplication;

public class ExamDateTime {
    private String date;
    private int startTime;
    private int endTime;
    public boolean isEmpty;

    public ExamDateTime(String rawDateTime, int duration) {
        this.date = rawDateTime.substring(0, 10);
        //01:00:00 format
        String rawTime = rawDateTime.substring(11,19);
        int hour = Integer.parseInt(rawTime.substring(0,2));
        int min = Integer.parseInt(rawTime.substring(3,5));
        //start time/ end time will be in this format: 100 (1am/pm) , 120 (1am/pm 20min)
        this.startTime = hour * 100 + min;
        int totalMinutes = hour * 60 + min + duration;
        int endMin = totalMinutes % 60; //remainder is the minutes
        int endHr = totalMinutes / 60;
        this.endTime = endHr * 100 + endMin;
        this.isEmpty = false;
    }

    public String getDate() {
        return this.date;
    }


    public int getStartTime() {
        return this.startTime;
    }

    public int getEndTime() {
        return this.endTime;
    }


    public boolean coincide(ExamDateTime newExam) {

        if (! this.date.equals(newExam.getDate())) {
            return false;
        } else {
            //same start time/ same end time
            if (this.startTime == newExam.getStartTime() || this.endTime == newExam.getEndTime() ) {
                return true;
            //start time of the newExam is in between the start time and end time of an included exam date
            } else if (this.startTime < newExam.getStartTime() && newExam.getStartTime() < this.endTime ) {
                return true;
                //end time of the newExam is in between the start time and end time of an included exam date
            } else if (this.startTime < newExam.getEndTime() && newExam.getEndTime() < this.endTime) {
                return true;
            } else {
                return false;
            }
        }

    }

    public void setEmpty() {
        this.isEmpty = true;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public static ExamDateTime emptyExamDateTime() {
        ExamDateTime dateTime = new ExamDateTime("",0);
        dateTime.setEmpty();
        return dateTime;
    }
}
