package io.github.funkynoodles.classlookup.lookup.models;

import java.util.Date;
import java.util.Set;

import io.github.funkynoodles.classlookup.models.Section;

class TimeSlot {
    private Date start;
    private Date end;
    public Set<Section> sections;
}
