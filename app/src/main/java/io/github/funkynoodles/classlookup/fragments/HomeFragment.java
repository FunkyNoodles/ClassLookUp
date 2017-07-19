package io.github.funkynoodles.classlookup.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.adapters.BuildingNameAdapter;
import io.github.funkynoodles.classlookup.adapters.TermSpinnerAdapter;
import io.github.funkynoodles.classlookup.lookup.SearchIndex;
import io.github.funkynoodles.classlookup.tasks.LoadScheduleTask;

public class HomeFragment extends Fragment {

    private TermSpinnerAdapter termAdapter;
    private BuildingNameAdapter buildingNameAdapter;
    private SearchIndex searchIndex;
    private String currentTermName;

    public HomeFragment() {
        searchIndex = new SearchIndex();
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
        final BetterSpinner termSpinner = (BetterSpinner) view.findViewById(R.id.termSpinner);
        termSpinner.setAdapter(termAdapter);


        final AutoCompleteTextView buildingNameAutoText = (AutoCompleteTextView) view.findViewById(R.id.buildingNameAutoText);
        buildingNameAutoText.setThreshold(1);
        buildingNameAutoText.setAdapter(buildingNameAdapter);


        termSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String termName = (String) parent.getItemAtPosition(position);
                if(termName.equals(currentTermName)){
                    return;
                }
                new LoadScheduleTask(searchIndex, getActivity(), termName, buildingNameAdapter).execute();
                currentTermName = termName;
            }
        });

        return view;
    }
}
