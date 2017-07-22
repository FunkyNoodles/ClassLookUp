package io.github.funkynoodles.classlookup.models;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Section implements Serializable{

    private String id;
    private String sectionNumber;
    private String href;
    private String sectionText;
    private String sectionNotes;
    private DateTime startDate;
    private DateTime endDate;
    private List<Meeting> meetings;
    private String term;
    private String subject;
    private String subjectId;
    private String course;
    private String courseId;

    public Section(String id, String href, String sectionNumber) {
        this.id = id;
        this.href = href;
        this.sectionNumber = sectionNumber;
        meetings = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getSectionText() {
        return sectionText;
    }

    public void setSectionText(String sectionText) {
        this.sectionText = sectionText;
    }

    public String getSectionNotes() {
        return sectionNotes;
    }

    public void setSectionNotes(String sectionNotes) {
        this.sectionNotes = sectionNotes;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public void insertMeeting(Meeting meeting) {
        meetings.add(meeting);
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public boolean containsDate(DateTime date) {
        for (Meeting meeting : meetings) {
            if (meeting.containsDate(date)) {
                return true;
            }
        }
        return false;
    }
}
