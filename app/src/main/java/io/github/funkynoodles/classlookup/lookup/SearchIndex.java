package io.github.funkynoodles.classlookup.lookup;

import java.util.HashMap;
import java.util.Map;

public class SearchIndex {

    public Map<String, TermIndex> termIndexMap;

    public SearchIndex(){
        termIndexMap = new HashMap<>();
    }
}
