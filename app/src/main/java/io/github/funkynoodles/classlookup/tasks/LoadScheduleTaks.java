package io.github.funkynoodles.classlookup.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.AutoCompleteTextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import io.github.funkynoodles.classlookup.adapters.BuildingNameAdapter;
import io.github.funkynoodles.classlookup.gsonconverters.DateTimeConverter;
import io.github.funkynoodles.classlookup.lookup.SearchIndex;
import io.github.funkynoodles.classlookup.lookup.TermIndex;
import io.github.funkynoodles.classlookup.models.Term;

public class LoadScheduleTaks extends AsyncTask<Void, Integer, Boolean> {

    private SearchIndex searchIndex;
    private Context context;
    private String termName;
    private AutoCompleteTextView autoCompleteTextView;
    private BuildingNameAdapter buildingNameAdapter;

    public LoadScheduleTaks(SearchIndex searchIndex, Context context, String termName,
                            AutoCompleteTextView autoCompleteTextView, BuildingNameAdapter buildingNameAdapter) {
        this.searchIndex = searchIndex;
        this.context = context;
        this.termName = termName;
        this.autoCompleteTextView = autoCompleteTextView;
        this.buildingNameAdapter = buildingNameAdapter;
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        File file = new File(context.getDir("schedules", Context.MODE_PRIVATE), termName + ".json");
        buildingNameAdapter.clearAll();
        if (searchIndex.termIndexMap.get(termName) == null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(DateTime.class, new DateTimeConverter())
                        .create();
                Term term = gson.fromJson(br, Term.class);
                TermIndex termIndex = new TermIndex();
                termIndex.buildIndex(term);
                searchIndex.termIndexMap.put(termName, termIndex);
                for (String s : termIndex.buildingMap.keySet()) {
                    buildingNameAdapter.addToPermanent(s);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            for (String s : searchIndex.termIndexMap.get(termName).buildingMap.keySet()) {
                buildingNameAdapter.addToPermanent(s);
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean ignore){
        buildingNameAdapter.notifyDataSetChanged();
    }
}
