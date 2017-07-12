package io.github.funkynoodles.classlookup.models;

import java.util.ArrayList;
import java.util.List;

public class CalendarYears {
    private List<CalendarYear> years;

    public CalendarYears(){
        years = new ArrayList<>();
    }

    public List<CalendarYear> getYears(){
        return years;
    }

    public void insertCalendarYear(CalendarYear year){
        years.add(year);
    }
}
