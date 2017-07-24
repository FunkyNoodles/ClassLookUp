package io.github.funkynoodles.classlookup.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Term {

    private String id;
    private String label;
    private List<Subject> subjects;

    @JsonCreator
    public Term(@JsonProperty("id") String id, @JsonProperty("label") String label) {
        this.id = id;
        this.label = label;
        subjects = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void insertSubject(Subject subject) {
        subjects.add(subject);
    }
}
