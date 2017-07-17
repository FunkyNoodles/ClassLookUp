package io.github.funkynoodles.classlookup.models;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class Meeting {

    private final static String[] daysOfWeekTranslation = {
            "", "M", "T", "W", "R", "F"
    };

    private String id;
    private String type;
    private String typeCode;
    private DateTime startTime;
    private DateTime endTime;
    private String daysOfTheWeek;
    private String roomNumber;
    private String buildingName;
    private List<String> instructors;

    public Meeting(String id) {
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

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
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
        this.instructors.add(instructor);
    }

    public boolean containsDate(DateTime date) {
        if (daysOfTheWeek.contains(daysOfWeekTranslation[date.getDayOfWeek()])) {
            DateTime startTime = this.startTime.withDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
            DateTime endTime = this.endTime.withDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
            if ((!endTime.isBefore(date)) && (!startTime.isAfter(date))) {
                return true;
            }
        }
        return false;
    }
}
