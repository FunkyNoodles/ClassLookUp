package io.github.funkynoodles.classlookup.lookup.models;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import io.github.funkynoodles.classlookup.models.Section;

public class Room {
    private String name;
    public List<Section> sections;

    public Room() {
        sections = new ArrayList<>();
    }

    public void insert(Section section) {
        sections.add(section);
    }

    public Section get(DateTime date) {
        for (Section s : sections) {
            if(s.containsDate(date)){
                return s;
            }
        }
        return null;
    }
}
