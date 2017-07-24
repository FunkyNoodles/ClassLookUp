package io.github.funkynoodles.classlookup.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import io.github.funkynoodles.classlookup.adapters.BuildingNameAdapter;
import io.github.funkynoodles.classlookup.dialogfragments.LoadingScheduleDialogFragment;
import io.github.funkynoodles.classlookup.lookup.SearchIndex;
import io.github.funkynoodles.classlookup.lookup.TermIndex;
import io.github.funkynoodles.classlookup.models.Term;

public class LoadScheduleTask extends AsyncTask<Void, Integer, Boolean> {

    private SearchIndex searchIndex;
    private Activity context;
    private String termName;
    private BuildingNameAdapter buildingNameAdapter;
    LoadingScheduleDialogFragment loadingScheduleDialogFragment;

    public LoadScheduleTask(SearchIndex searchIndex, Activity context, String termName,
                            BuildingNameAdapter buildingNameAdapter) {
        this.searchIndex = searchIndex;
        this.context = context;
        this.termName = termName;
        this.buildingNameAdapter = buildingNameAdapter;
    }

    @Override
    protected void onPreExecute() {
        loadingScheduleDialogFragment = LoadingScheduleDialogFragment.newInstance();
        loadingScheduleDialogFragment.show(context.getFragmentManager(), "loadingSchedule");
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        File file = new File(context.getDir("schedules", Context.MODE_PRIVATE), termName + ".json");
        buildingNameAdapter.clearAll();
        if (searchIndex.termIndexMap.get(termName) == null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JodaModule());
                Term term = mapper.readValue(br, Term.class);
                TermIndex termIndex = new TermIndex();
                termIndex.buildIndex(term);
                searchIndex.termIndexMap.put(termName, termIndex);
                for (String s : termIndex.buildingMap.keySet()) {
                    buildingNameAdapter.addToPermanent(s);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            for (String s : searchIndex.termIndexMap.get(termName).buildingMap.keySet()) {
                buildingNameAdapter.addToPermanent(s);
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean ignore) {
        buildingNameAdapter.notifyDataSetChanged();
        loadingScheduleDialogFragment.dismiss();
    }
}
