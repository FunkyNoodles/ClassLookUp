package io.github.funkynoodles.classlookup.models;

import java.util.ArrayList;
import java.util.List;

public class CalendarYear {
    private String year;
    private String href;
    private List<Term> terms;

    public CalendarYear(String year, String href){
        this.year = year;
        this.href = href;
        terms = new ArrayList<>();
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

    public List<Term> getTerms() {
        return terms;
    }

    public void insertTerm(Term term){
        terms.add(term);
    }
}
