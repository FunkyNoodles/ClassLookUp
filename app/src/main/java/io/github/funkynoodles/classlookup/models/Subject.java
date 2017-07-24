package io.github.funkynoodles.classlookup.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Subject {

    private String id;
    private String href;
    private String text;
    private List<Course> courses;

    @JsonCreator
    public Subject(@JsonProperty("id") String id, @JsonProperty("href") String href, @JsonProperty("text") String text){
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
