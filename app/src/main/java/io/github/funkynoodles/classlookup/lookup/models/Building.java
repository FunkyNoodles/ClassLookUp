package io.github.funkynoodles.classlookup.lookup.models;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.github.funkynoodles.classlookup.models.Meeting;
import io.github.funkynoodles.classlookup.models.Section;

public class Building {
    private String name;
    public Map<String, Room> roomMap;

    public Building() {
        roomMap = new HashMap<>();
    }

    public void insert(Section section, Meeting meeting) {
        if (roomMap.get(meeting.getRoomNumber()) == null) {
            roomMap.put(meeting.getRoomNumber(), new Room());
        }
        Room room = roomMap.get(meeting.getRoomNumber());
        room.insert(section);
    }

    public Section get(String roomNumber, DateTime date) {
        Room room = roomMap.get(roomNumber);
        if (room == null) {
            return null;
        }
        return room.get(date);
    }
}
