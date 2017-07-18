package io.github.funkynoodles.classlookup.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import io.github.funkynoodles.classlookup.R;

public class BuildingNameAdapter extends ArrayAdapter<String> {

    private List<String> data;
    private Context context;

    public BuildingNameAdapter(Context context, List<String> data) {
        super(context, R.layout.term_spinner_item, data);
        this.data = data;
        this.context = context;
    }

    @Override
    public void clear(){
        data.clear();
    }

    @Override
    public void addAll(@NonNull Collection<? extends String> collection){
        data.addAll(collection);
    }

    @Override
    public void add(String s){
        data.add(s);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        String buildingName = data.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.buildingname_autocomplete_item, parent, false);
        }
        TextView termSpinnerText = (TextView)convertView.findViewById(R.id.buildingNameText);
        termSpinnerText.setText(buildingName);

        return convertView;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
