package io.github.funkynoodles.classlookup.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.adapters.BuildingNameAdapter;
import io.github.funkynoodles.classlookup.adapters.TermSpinnerAdapter;
import io.github.funkynoodles.classlookup.gsonconverters.DateTimeConverter;
import io.github.funkynoodles.classlookup.lookup.SearchIndex;
import io.github.funkynoodles.classlookup.models.Section;
import io.github.funkynoodles.classlookup.models.Term;

public class HomeFragment extends Fragment {

    private TermSpinnerAdapter termAdapter;
    private BuildingNameAdapter buildingNameAdapter;
    private Map<String, SearchIndex> searchIndexMap;

    public HomeFragment() {
        searchIndexMap = new HashMap<>();
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildingNameAdapter = new BuildingNameAdapter(getContext(), new ArrayList<String>());

        List<String> fileNames = new ArrayList<>();
        File file = getContext().getDir("schedules", Context.MODE_PRIVATE);
        if (!file.exists()) {
            return;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                fileNames.add(f.getName());
            }
        }
        termAdapter = new TermSpinnerAdapter(getContext(), fileNames);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        BetterSpinner termSpinner = (BetterSpinner) view.findViewById(R.id.termSpinner);
        termSpinner.setAdapter(termAdapter);


        final AutoCompleteTextView buildingNameAutoText = (AutoCompleteTextView) view.findViewById(R.id.buildingNameAutoText);
        buildingNameAutoText.setThreshold(1);
        buildingNameAutoText.setAdapter(buildingNameAdapter);


        termSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String termName = (String) parent.getItemAtPosition(position);
                File file = new File(getActivity().getDir("schedules", Context.MODE_PRIVATE), termName + ".json");
                // TODO: This needs to be in an AsyncTask
                if (searchIndexMap.get(termName) == null) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(DateTime.class, new DateTimeConverter())
                                .create();
                        Term term = gson.fromJson(br, Term.class);
                        SearchIndex searchIndex = new SearchIndex();
                        searchIndex.buildIndex(term);
                        searchIndexMap.put(termName, searchIndex);
                        //buildingNameAdapter.clear();
                        for (String s : searchIndex.buildingMap.keySet()) {
                            buildingNameAdapter.add(s);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                buildingNameAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}
