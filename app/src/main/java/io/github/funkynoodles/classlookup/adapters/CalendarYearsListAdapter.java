package io.github.funkynoodles.classlookup.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.fragments.DownloadedFileDialogFragment;
import io.github.funkynoodles.classlookup.helpers.Utilities;
import io.github.funkynoodles.classlookup.models.CalendarYear;
import io.github.funkynoodles.classlookup.models.CalendarYears;
import io.github.funkynoodles.classlookup.models.Course;
import io.github.funkynoodles.classlookup.models.MetaTerm;
import io.github.funkynoodles.classlookup.models.Section;
import io.github.funkynoodles.classlookup.models.Subject;
import io.github.funkynoodles.classlookup.models.Term;
import io.github.funkynoodles.classlookup.tasks.DownloadTermTask;

public class CalendarYearsListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private CalendarYears calendarYears = null;
    private List<String> fileNames;

    public CalendarYearsListAdapter(Activity context){
        this.context = context;
        fileNames = new ArrayList<>();
        File file = context.getFilesDir();
        for (File f : file.listFiles()){
            if(f.isFile()){
                fileNames.add(f.getName());
            }
        }
    }

    @Override
    public MetaTerm getChild(int groupPosition, int childPosition){
        return calendarYears.getYears().get(groupPosition).getMetaTerms().get(childPosition);
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
            convertView = inflater.inflate(R.layout.schedule_child_item, parent, false);
        }

        final MetaTerm metaTerm = getChild(groupPosition, childPosition);

        TextView termText = (TextView)convertView.findViewById(R.id.termText);
        termText.setText(metaTerm.getText());
        metaTerm.setDownloadButton((Button)convertView.findViewById(R.id.downloadButton));
        metaTerm.getDownloadButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metaTerm.setDownloadTermTask(new DownloadTermTask(metaTerm, context));
                metaTerm.executeDownloadTermTask();
            }
        });

        metaTerm.setDownloadedButton((Button)convertView.findViewById(R.id.downloadedButton));
        metaTerm.getDownloadedButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadedFileDialogFragment downloadedFileDialogFragment = DownloadedFileDialogFragment.newInstance(metaTerm);
                downloadedFileDialogFragment.show(context.getFragmentManager(), "downloadedOptions");
            }
        });

        Button cancelDownloadButton = (Button)convertView.findViewById(R.id.cancelButton);
        cancelDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metaTerm.cancelDownloadTermTask();
                metaTerm.getDownloadingLayout().setVisibility(View.GONE);
                metaTerm.getDownloadButton().setVisibility(View.VISIBLE);
            }
        });

        metaTerm.setDownloadProgress((NumberProgressBar)convertView.findViewById(R.id.downloadProgress));
        metaTerm.setDownloadingLayout(convertView.findViewById(R.id.downloadingLayout));

        String fileString = metaTerm.getText() + ".json";
        if(fileNames.contains(fileString)){
            // If the file has been downloaded before
            metaTerm.getDownloadButton().setVisibility(View.GONE);
            metaTerm.getDownloadedButton().setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return calendarYears.getYears().get(groupPosition).getMetaTerms().size();
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
