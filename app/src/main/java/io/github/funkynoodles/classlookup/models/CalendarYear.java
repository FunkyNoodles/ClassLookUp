package io.github.funkynoodles.classlookup.models;

import java.util.ArrayList;
import java.util.List;

public class CalendarYear {
    private String year;
    private String href;
    private List<MetaTerm> metaTerms;

    public CalendarYear(String year, String href){
        this.year = year;
        this.href = href;
        metaTerms = new ArrayList<>();
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<MetaTerm> getMetaTerms() {
        return metaTerms;
    }

    public void insertTerm(MetaTerm metaTerm){
        metaTerms.add(metaTerm);
    }
}
