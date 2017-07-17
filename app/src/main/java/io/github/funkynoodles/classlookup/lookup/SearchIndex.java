package io.github.funkynoodles.classlookup.lookup;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

import io.github.funkynoodles.classlookup.lookup.models.Building;
import io.github.funkynoodles.classlookup.models.Course;
import io.github.funkynoodles.classlookup.models.Meeting;
import io.github.funkynoodles.classlookup.models.Section;
import io.github.funkynoodles.classlookup.models.Subject;
import io.github.funkynoodles.classlookup.models.Term;

public class SearchIndex {
    public Map<String, Building> buildingMap;

    public SearchIndex() {
        buildingMap = new HashMap<>();
    }

    public void buildIndex(Term term) {
        for (Subject subject : term.getSubjects()) {
            for (Course course : subject.getCourses()) {
                for (Section section : course.getSections()) {
                    for (Meeting meeting : section.getMeetings()) {
                        if (buildingMap.get(meeting.getBuildingName()) == null) {
                            buildingMap.put(meeting.getBuildingName(), new Building());
                        }
                        Building building = buildingMap.get(meeting.getBuildingName());
                        building.insert(section, meeting);
                    }
                }
            }
        }
    }

    public Section get(String buildingName, String roomNumber, DateTime date) {
        Building building = buildingMap.get(buildingName);
        if (building == null) {
            return null;
        }
        return building.get(roomNumber, date);
    }

}
