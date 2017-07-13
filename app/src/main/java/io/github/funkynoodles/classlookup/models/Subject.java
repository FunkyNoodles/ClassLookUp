package io.github.funkynoodles.classlookup.models;

import java.util.ArrayList;
import java.util.List;

public class Subject {

    private String id;
    private String href;
    private String text;
    private List<Course> courses;

    public Subject(String id, String href, String text){
        this.id = id;
        this.href = href;
        this.text = text;
        courses = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public String getText() {
        return text;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void insertCourse(Course course){
        courses.add(course);
    }
}
