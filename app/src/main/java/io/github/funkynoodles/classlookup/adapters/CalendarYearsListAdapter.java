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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import io.github.funkynoodles.classlookup.R;
import io.github.funkynoodles.classlookup.helpers.Utilities;
import io.github.funkynoodles.classlookup.models.CalendarYear;
import io.github.funkynoodles.classlookup.models.CalendarYears;
import io.github.funkynoodles.classlookup.models.Course;
import io.github.funkynoodles.classlookup.models.MetaTerm;
import io.github.funkynoodles.classlookup.models.Section;
import io.github.funkynoodles.classlookup.models.Subject;
import io.github.funkynoodles.classlookup.models.Term;

public class CalendarYearsListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private CalendarYears calendarYears = null;



    public CalendarYearsListAdapter(Activity context){
        this.context = context;
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

    public class DownloadTermTask extends AsyncTask<Void, Integer, Term>{
        private MetaTerm metaTerm;
        private Activity context;

        DownloadTermTask(MetaTerm metaTerm, Activity context){
            this.metaTerm = metaTerm;
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            metaTerm.getDownloadButton().setVisibility(View.GONE);
            metaTerm.getDownloadingLayout().setVisibility(View.VISIBLE);
        }

        @Override
        protected Term doInBackground(Void... params) {
            URL url;
            HttpsURLConnection urlConnection = null;
            Term term = new Term(metaTerm.getId(), metaTerm.getText());

            try {
                url = new URL(metaTerm.getHref());
                urlConnection = (HttpsURLConnection)url.openConnection();

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpsURLConnection.HTTP_OK){
                    if(isCancelled()){
                        return null;
                    }
                    String responseString = Utilities.readStream(urlConnection.getInputStream());

                    Document doc = dBuilder.parse(new InputSource(new StringReader(responseString)));
                    doc.getDocumentElement().normalize();

                    NodeList subjectNodes = doc.getDocumentElement().getElementsByTagName("subject");
                    for(int i = 0; i < subjectNodes.getLength(); ++i){
                        Node node = subjectNodes.item(i);
                        term.insertSubject(new Subject(
                                node.getAttributes().getNamedItem("id").getNodeValue(),
                                node.getAttributes().getNamedItem("href").getNodeValue(),
                                node.getTextContent()
                        ));
                    }
                }else{
                    return null;
                }

                for(Subject subject : term.getSubjects()){
                    if(isCancelled()){
                        return null;
                    }
                    HttpsURLConnection subjectUrlConnection = (HttpsURLConnection)new URL(subject.getHref()).openConnection();
                    int subjectResponseCode = subjectUrlConnection.getResponseCode();

                    if(subjectResponseCode == HttpsURLConnection.HTTP_OK){
                        String subjectResponseString = Utilities.readStream(subjectUrlConnection.getInputStream());

                        Document subjectDoc = dBuilder.parse(new InputSource(new StringReader(subjectResponseString)));
                        subjectDoc.getDocumentElement().normalize();

                        NodeList courseNodes = subjectDoc.getDocumentElement().getElementsByTagName("course");
                        for(int i = 0; i < courseNodes.getLength(); ++i){
                            Node node = courseNodes.item(i);
                            subject.insertCourse(new Course(
                                    node.getAttributes().getNamedItem("id").getNodeValue(),
                                    node.getAttributes().getNamedItem("href").getNodeValue(),
                                    node.getTextContent()
                            ));
                        }
                    }else{
                        return null;
                    }
                    subjectUrlConnection.disconnect();
                    for(Course course : subject.getCourses()){
                        if(isCancelled()){
                            return null;
                        }
                        HttpsURLConnection courseUrlConnection = (HttpsURLConnection)new URL(course.getHref()).openConnection();
                        int courseResponseCode = courseUrlConnection.getResponseCode();

                        if(courseResponseCode == HttpsURLConnection.HTTP_OK){
                            String courseResponseString = Utilities.readStream(courseUrlConnection.getInputStream());

                            Document courseDoc = dBuilder.parse(new InputSource(new StringReader(courseResponseString)));
                            courseDoc.getDocumentElement().normalize();

                            course.setDescription(courseDoc.getElementsByTagName("description").item(0).getTextContent());
                            course.setCreditHours(courseDoc.getElementsByTagName("creditHours").item(0).getTextContent());

                            NodeList sectionNodes = courseDoc.getDocumentElement().getElementsByTagName("section");
                            for(int i = 0; i < sectionNodes.getLength(); ++i){
                                Node node = sectionNodes.item(i);
                                course.insertSection(new Section(
                                        node.getAttributes().getNamedItem("id").getNodeValue(),
                                        node.getAttributes().getNamedItem("href").getNodeValue(),
                                        node.getTextContent()
                                ));
                            }
                        }else{
                            return null;
                        }
                        courseUrlConnection.disconnect();

                        for(Section section : course.getSections()){
                            if(isCancelled()){
                                return null;
                            }
                            HttpsURLConnection sectionUrlConnection = (HttpsURLConnection)new URL(section.getHref()).openConnection();
                            int sectionResponseCode = sectionUrlConnection.getResponseCode();

                            if(sectionResponseCode == HttpsURLConnection.HTTP_OK){
                                String sectionResponseString = Utilities.readStream(sectionUrlConnection.getInputStream());

                                Document sectionDoc = dBuilder.parse(new InputSource(new StringReader(sectionResponseString)));
                                sectionDoc.getDocumentElement().normalize();

                                //section.setSectionText(sectionDoc.getElementsByTagName("sectionText").item(0).getNodeValue());
                                //section.setSectionNotes(sectionDoc.getElementsByTagName("sectionNotes").item(0).getNodeValue());
                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.ENGLISH);
                                Date startDate = format.parse(sectionDoc.getElementsByTagName("startDate").item(0).getTextContent());
                                Date endDate = format.parse(sectionDoc.getElementsByTagName("endDate").item(0).getTextContent());
                                section.setStartDate(startDate);
                                section.setEndDate(endDate);
                            }else{
                                return null;
                            }
                            sectionUrlConnection.disconnect();
                        }
                    }
                }
                if(isCancelled()){
                    return null;
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String termJson = gson.toJson(term);
                String fileName = term.getLabel() + ".json";
                FileOutputStream outputStream;

                if(isCancelled()){
                    return null;
                }
                outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream.write(termJson.getBytes());
                outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
        }

        @Override
        protected void onPostExecute(Term term){
            if(term == null){
                return;
            }

        }
    }
}
