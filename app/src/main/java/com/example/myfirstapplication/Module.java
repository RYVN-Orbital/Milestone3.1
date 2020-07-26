package com.example.myfirstapplication;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Module {
    protected final String moduleCode;
    protected final List<Lesson> lessons;
    protected AllLecture allLect;
    protected AllLab allLab;
    protected AllTutorial allTut;
    protected AllSectional allSec;
    protected AllRecitation allRec;

    public Module(String moduleCode, Lesson... arrayLesson) {
        this.moduleCode = moduleCode;
        this.lessons = Arrays.asList(arrayLesson);

        List<Lesson> listOfLectures = new ArrayList<>();
        List<Lesson> listOfTutorials = new ArrayList<>();
        List<Lesson> listOfRecitations = new ArrayList<>();
        List<Lesson> listOfSectionals = new ArrayList<>();
        List<Lesson> listOfLabs = new ArrayList<>();

        for (Lesson lesson : Arrays.asList(arrayLesson)) {
            if (lesson.getType().equals(Type.LECTURE)) {
                listOfLectures.add(lesson);
            } else if (lesson.getType().equals(Type.TUTORIAL)) {
                listOfTutorials.add(lesson);
            } else if (lesson.getType().equals(Type.RECITATION)) {
                listOfRecitations.add(lesson);
            } else if (lesson.getType().equals(Type.LAB)) {
                listOfLabs.add(lesson);
            } else {
                listOfSectionals.add(lesson);
            }
        }
        Comparator<Lesson> comp = new LessonComparator();
        listOfLectures.sort(comp);
        listOfRecitations.sort(comp);
        listOfTutorials.sort(comp);
        listOfSectionals.sort(comp);
        listOfLabs.sort(comp);

        this.allLect = new AllLecture(this.moduleCode, listOfLectures);
        this.allRec = new AllRecitation(this.moduleCode, listOfRecitations);
        this.allTut = new AllTutorial(this.moduleCode, listOfTutorials);
        this.allSec = new AllSectional(this.moduleCode, listOfSectionals);
        this.allLab = new AllLab(this.moduleCode, listOfLabs);
    }

    public String getCode() {
        return this.moduleCode;
    }

    public AllLesson getLect() {
        return this.allLect;
    }

    public AllLesson getLab() {
        return this.allLab;
    }

    public AllLesson getRec() {
        return this.allRec;
    }

    public AllLesson getTut() {
        return this.allTut;
    }

    public AllLesson getSec() {
        return this.allSec;
    }

    //NEW 1
    //Finding lesson based on lesson num n type
    public List<Lesson> getLesson(String lessonNum, String lessonType) {
        List<Lesson> lesson = new ArrayList<>();
        if (lessonType.equals("Lecture")) {
            lesson = this.allLect.findLesson(lessonNum);
        } else if (lessonType.equals("Recitation")) {
            lesson = this.allRec.findLesson(lessonNum);
        } else if (lessonType.equals("Tutorial")) {
            lesson = this.allTut.findLesson(lessonNum);
        } else if (lessonType.equals("Laboratory")) {
            lesson = this.allLab.findLesson(lessonNum);
        } else if (lessonType.equals("Sectional Teaching")) {
            lesson = this.allSec.findLesson(lessonNum);
        }

        return lesson;
    }
    //NEW 1

    //NEW 2
    public boolean updateAllLesson(String lessonType) {
        List<Lesson> emptyList = new ArrayList<>();
        if (lessonType.equals("Lecture")) {
            this.allLect.setLesson(emptyList);
            return true;
        } else if (lessonType.equals("Recitation")) {
            this.allRec.setLesson(emptyList);
            return true;
        } else if (lessonType.equals("Tutorial")) {
            this.allTut.setLesson(emptyList);
            return true;
        } else if (lessonType.equals("Laboratory")) {
            this.allLab.setLesson(emptyList);
            return true;
        } else if (lessonType.equals("Sectional Teaching")) {
            this.allSec.setLesson(emptyList);
            return true;
        }
        return false;
    }
    //END OF NEW 2


    @Override
    public String toString() {
        return this.moduleCode;
    }
}
