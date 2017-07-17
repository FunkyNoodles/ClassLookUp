package io.github.funkynoodles.classlookup.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.adapters.TermSpinnerAdapter;

public class HomeFragment extends Fragment {

    private TermSpinnerAdapter termAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
//                String filePath = getActivity().getFilesDir() + "/" + "Summer 2017.json";
//                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//
//                    Gson gson = new GsonBuilder()
//                            .registerTypeAdapter(DateTime.class, new DateTimeConverter())
//                            .create();
//                    Term term = gson.fromJson(br, Term.class);
//                    SearchIndex index = new SearchIndex();
//                    index.buildIndex(term);
//                    DateTime testTime = new DateTime(2017, 7, 11, 13, 50);
//                    Section s = index.get("Electrical & Computer Eng Bldg", "2013", testTime);
//                    if(s != null){
//                        System.out.println(s.getSubject() + " " + s.getCourse());
//                    }else{
//                        System.out.println("This shouldnt happen");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
        MaterialBetterSpinner termSpinner = (MaterialBetterSpinner)view.findViewById(R.id.termSpinner);
        termSpinner.setAdapter(termAdapter);
        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

}
