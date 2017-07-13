package io.github.funkynoodles.classlookup.models;

import java.util.Date;

public class Section {

    private String id;
    private String sectionNumber;
    private String href;
    private String sectionText;
    private String sectionNotes;
    private Date startDate;
    private Date endDate;

    public Section(String id, String href, String sectionNumber){
        this.id = id;
        this.href = href;
        this.sectionNumber = sectionNumber;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
