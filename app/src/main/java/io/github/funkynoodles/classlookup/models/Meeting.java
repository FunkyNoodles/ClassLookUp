package io.github.funkynoodles.classlookup.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Meeting {

    private String id;
    private String type;
    private String typeCode;
    private Date startTime;
    private Date endTime;
    private String daysOfTheWeek;
    private String roomNumber;
    private String buildingName;
    private List<String> instructors;

    public Meeting(String id){
        this.id = id;
        instructors = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDaysOfTheWeek() {
        return daysOfTheWeek;
    }

    public void setDaysOfTheWeek(String daysOfTheWeek) {
        this.daysOfTheWeek = daysOfTheWeek;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public List<String> getInstructors() {
        return instructors;
    }

    public void insertInstructor(String instructor) {
        this.instructors.add(instructor);;
    }
}
