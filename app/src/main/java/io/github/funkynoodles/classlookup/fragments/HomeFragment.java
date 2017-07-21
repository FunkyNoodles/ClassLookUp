package io.github.funkynoodles.classlookup.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.joda.time.DateTime;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.adapters.BuildingNameAdapter;
import io.github.funkynoodles.classlookup.adapters.TermSpinnerAdapter;
import io.github.funkynoodles.classlookup.lookup.SearchIndex;
import io.github.funkynoodles.classlookup.lookup.TermIndex;
import io.github.funkynoodles.classlookup.models.Section;
import io.github.funkynoodles.classlookup.tasks.LoadScheduleTask;

public class HomeFragment extends Fragment {

    private TermSpinnerAdapter termAdapter;
    private BuildingNameAdapter buildingNameAdapter;
    private SearchIndex searchIndex;
    private String currentTermName;
    private Calendar selectedCalendar;

    private BetterSpinner termSpinner;
    private AutoCompleteTextView buildingNameAutoText;
    private EditText roomNameEditText;
    private TextView dateTextView;
    private TextView timeTextView;
    private Button searchButton;

    public HomeFragment() {
        searchIndex = new SearchIndex();
        selectedCalendar = Calendar.getInstance();
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
        termSpinner = (BetterSpinner) view.findViewById(R.id.termSpinner);
        termSpinner.setAdapter(termAdapter);

        buildingNameAutoText = (AutoCompleteTextView) view.findViewById(R.id.buildingNameAutoText);
        buildingNameAutoText.setThreshold(1);
        buildingNameAutoText.setAdapter(buildingNameAdapter);

        termSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String termName = (String) parent.getItemAtPosition(position);
                if (termName.equals(currentTermName)) {
                    return;
                }
                new LoadScheduleTask(searchIndex, getActivity(), termName, buildingNameAdapter).execute();
                currentTermName = termName;
            }
        });

        roomNameEditText = (EditText)view.findViewById(R.id.roomNameText);

        dateTextView = (TextView) view.findViewById(R.id.dateText);
        dateTextView.setText(getDisplayDateString(selectedCalendar));
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedCalendar.set(year, month, dayOfMonth);
                        dateTextView.setText(getDisplayDateString(selectedCalendar));
                    }
                },
                        Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        timeTextView = (TextView) view.findViewById(R.id.timeText);
        timeTextView.setText(getDisplayTimeString(selectedCalendar));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedCalendar.set(Calendar.MINUTE, minute);
                        timeTextView.setText(getDisplayTimeString(selectedCalendar));
                    }
                }, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        searchButton = (Button) view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new SearchButtonClickListener());

        return view;
    }

    public void updateTermAdapter() {
        termAdapter.updateContent();
    }

    private String getDisplayDateString(Calendar c) {
        String displayDate = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
        displayDate += ". " + c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        displayDate += " " + c.get(Calendar.DAY_OF_MONTH);
        displayDate += ", " + c.get(Calendar.YEAR);
        return displayDate;
    }

    private String getDisplayTimeString(Calendar c) {
        DecimalFormat formatter = new DecimalFormat("00");
        String displayTime = Integer.toString(c.get(Calendar.HOUR));
        displayTime += ":" + formatter.format(c.get(Calendar.MINUTE));
        displayTime += " " + c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.ENGLISH);
        return displayTime;
    }

    private class SearchButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String termName = termSpinner.getText().toString();
            if (searchIndex.termIndexMap.get(termName) == null) {
                searchButtonError();
                return;
            }
            TermIndex termIndex = searchIndex.termIndexMap.get(termName);
            String buildingName = buildingNameAutoText.getText().toString();
            String roomName = roomNameEditText.getText().toString();
            DateTime dateTime = new DateTime(selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH),
                    selectedCalendar.get(Calendar.HOUR_OF_DAY),
                    selectedCalendar.get(Calendar.MINUTE));
            Section section = termIndex.get(buildingName, roomName, dateTime);
            if(section == null){
                searchButtonError();
                return;
            }
        }
    }

    private void searchButtonError(){
        Toast.makeText(getActivity(), getString(R.string.textSearchInvalid), Toast.LENGTH_LONG).show();
    }
}
