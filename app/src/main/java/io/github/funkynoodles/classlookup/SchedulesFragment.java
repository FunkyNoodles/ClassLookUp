package io.github.funkynoodles.classlookup;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import io.github.funkynoodles.classlookup.adpaters.CalendarYearsListAdapter;
import io.github.funkynoodles.classlookup.models.CalendarYear;
import io.github.funkynoodles.classlookup.models.CalendarYears;
import io.github.funkynoodles.classlookup.models.Term;


public class SchedulesFragment extends Fragment{

    private ExpandableListView calendarYearsListView;
    private ArcProgress fetchYearsProgress;
    private SwipeRefreshLayout refreshLayout;

    public static SchedulesFragment newInstance() {
        return new SchedulesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownloadXmlTask(getString(R.string.coursesApiRoot)).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);
        calendarYearsListView = (ExpandableListView)view.findViewById(R.id.calendarYearsList);
        fetchYearsProgress = (ArcProgress)view.findViewById(R.id.fetchYearsProgress);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.schedulesSwipeRefresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        return view;
    }

    private class DownloadXmlTask extends AsyncTask<Void, Integer, CalendarYears>{

        private String mUrl;

        DownloadXmlTask(String url){
            this.mUrl = url;
        }

        @Override
        protected CalendarYears doInBackground(Void... params) {
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(mUrl);
                urlConnection = (HttpsURLConnection)url.openConnection();

                int responseCode = urlConnection.getResponseCode();
                CalendarYears calendarYears = new CalendarYears();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    String responseString = readStream(urlConnection.getInputStream());

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(new InputSource(new StringReader(responseString)));
                    doc.getDocumentElement().normalize();

                    NodeList calendarYearsNodes = doc.getDocumentElement().getElementsByTagName("calendarYear");
                    for(int i = 0; i < calendarYearsNodes.getLength(); ++i){
                        Node node = calendarYearsNodes.item(i);
                        calendarYears.insertCalendarYear(new CalendarYear(
                                node.getAttributes().getNamedItem("id").getNodeValue(),
                                node.getAttributes().getNamedItem("href").getNodeValue()
                        ));
                    }
                    urlConnection.disconnect();
                }else{
                    return null;
                }
                // Load terms
                int progress;
                for(int i = 0; i < calendarYears.getYears().size(); ++i){
                    CalendarYear calendarYear = calendarYears.getYears().get(i);
                    progress = (int)((float)i / calendarYears.getYears().size() * 100);
                    System.out.println(progress);
                    publishProgress(progress);
                    String href = calendarYear.getHref();
                    URL termUrl = new URL(href);
                    HttpsURLConnection termUrlConnection = (HttpsURLConnection)termUrl.openConnection();

                    int termResponseCode = termUrlConnection.getResponseCode();
                    if(termResponseCode == HttpURLConnection.HTTP_OK){
                        String responseString = readStream(termUrlConnection.getInputStream());

                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(new InputSource(new StringReader(responseString)));
                        doc.getDocumentElement().normalize();

                        NodeList termsNodes = doc.getDocumentElement().getElementsByTagName("term");
                        for(int j = 0; j < termsNodes.getLength(); ++j){
                            Node node = termsNodes.item(j);
                            calendarYear.insertTerm(new Term(
                                    node.getAttributes().getNamedItem("id").getNodeValue(),
                                    node.getAttributes().getNamedItem("href").getNodeValue(),
                                    node.getTextContent()
                            ));
                        }
                    }
                }
                return calendarYears;
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
            fetchYearsProgress.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(CalendarYears calendarYears){
            final CalendarYearsListAdapter adapter = new CalendarYearsListAdapter(getActivity(), calendarYears);
            calendarYearsListView.setAdapter(adapter);
            fetchYearsProgress.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
            for(CalendarYear calendarYear : calendarYears.getYears()){
                System.out.println(calendarYear.getYear());
                for(Term term : calendarYear.getTerms()){
                    System.out.println("\t" + term.getText());
                }
            }
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
