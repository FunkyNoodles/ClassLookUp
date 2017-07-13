package io.github.funkynoodles.classlookup.models;

import java.util.ArrayList;
import java.util.List;

public class Term {

    private String id;
    private String label;
    private List<Subject> subjects;

    public Term(String id, String label){
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

    public void insertSubject(Subject subject){
        subjects.add(subject);
    }
}
