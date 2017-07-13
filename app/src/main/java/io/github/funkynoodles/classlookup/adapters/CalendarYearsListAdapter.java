package io.github.funkynoodles.classlookup.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.models.CalendarYear;
import io.github.funkynoodles.classlookup.models.CalendarYears;
import io.github.funkynoodles.classlookup.models.Term;

public class CalendarYearsListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private CalendarYears calendarYears = null;

    public CalendarYearsListAdapter(Activity context){
        this.context = context;
    }

    @Override
    public Term getChild(int groupPosition, int childPosition){
        return calendarYears.getYears().get(groupPosition).getTerms().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.schedule_child_item, null);
        }

        Term term = getChild(groupPosition, childPosition);

        TextView termText = (TextView)convertView.findViewById(R.id.termText);
        termText.setText(term.getText());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return calendarYears.getYears().get(groupPosition).getTerms().size();
    }

    @Override
    public CalendarYear getGroup(int groupPosition){
        return calendarYears.getYears().get(groupPosition);
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.scheudule_group_item, parent, false);
        }

        CalendarYear calendarYear = calendarYears.getYears().get(groupPosition);
        TextView calendarYearText = (TextView) convertView.findViewById(R.id.calendarYearText);
        calendarYearText.setTypeface(null, Typeface.BOLD);
        calendarYearText.setText(calendarYear.getYear());

        return convertView;
    }

    @Override
    public int getGroupCount(){
        return calendarYears.getYears().size();
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition){
        return true;
    }

    public void clear(){
        if(this.calendarYears != null) {
            this.calendarYears.getYears().clear();
        }
    }

    public void setCalendarYears(CalendarYears calendarYears){
        this.calendarYears = calendarYears;
    }
}
