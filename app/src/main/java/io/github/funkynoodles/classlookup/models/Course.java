package io.github.funkynoodles.classlookup.models;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String id;
    private String label;
    private String href;
    private String description;
    private String creditHours;
    private String classScheduleInformation;
    private List<Section> sections;

    public Course(String id, String href, String label){
        this.id = id;
        this.label = label;
        this.href = href;
        sections = new ArrayList<>();
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

    public String getHref() {
        return href;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(String creditHours) {
        this.creditHours = creditHours;
    }

    public String getClassScheduleInformation() {
        return classScheduleInformation;
    }

    public void setClassScheduleInformation(String classScheduleInformation) {
        this.classScheduleInformation = classScheduleInformation;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void insertSection(Section section){
        sections.add(section);
    }
}
