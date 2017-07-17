package io.github.funkynoodles.classlookup.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.gsonconverters.DateTimeConverter;
import io.github.funkynoodles.classlookup.lookup.SearchIndex;
import io.github.funkynoodles.classlookup.models.Section;
import io.github.funkynoodles.classlookup.models.Term;


public class HomeFragment extends Fragment {

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button testButton = (Button)view.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = getActivity().getFilesDir() + "/" + "Summer 2017.json";
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(DateTime.class, new DateTimeConverter())
                            .create();
                    Term term = gson.fromJson(br, Term.class);
                    SearchIndex index = new SearchIndex();
                    index.buildIndex(term);
                    DateTime testTime = new DateTime(2017, 7, 11, 13, 50);
                    Section s = index.get("Electrical & Computer Eng Bldg", "2013", testTime);
                    if(s != null){
                        System.out.println(s.getSubject() + " " + s.getCourse());
                    }else{
                        System.out.println("This shouldnt happen");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

}
