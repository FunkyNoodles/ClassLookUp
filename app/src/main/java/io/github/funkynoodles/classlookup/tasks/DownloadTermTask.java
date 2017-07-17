package io.github.funkynoodles.classlookup.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import io.github.funkynoodles.classlookup.gsonconverters.DateTimeConverter;
import io.github.funkynoodles.classlookup.helpers.Utilities;
import io.github.funkynoodles.classlookup.models.Course;
import io.github.funkynoodles.classlookup.models.Meeting;
import io.github.funkynoodles.classlookup.models.MetaTerm;
import io.github.funkynoodles.classlookup.models.Section;
import io.github.funkynoodles.classlookup.models.Subject;
import io.github.funkynoodles.classlookup.models.Term;

public class DownloadTermTask extends AsyncTask<Void, Integer, Term> {
    private MetaTerm metaTerm;
    private Activity context;

    public DownloadTermTask(MetaTerm metaTerm, Activity context){
        this.metaTerm = metaTerm;
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
        metaTerm.getDownloadProgress().setProgress(0);
        metaTerm.getDownloadButton().setVisibility(View.GONE);
        metaTerm.getDownloadedButton().setVisibility(View.GONE);
        metaTerm.getDownloadingLayout().setVisibility(View.VISIBLE);
    }

    @Override
    protected Term doInBackground(Void... params) {
        URL url;
        HttpsURLConnection urlConnection = null;
        Term term = new Term(metaTerm.getId(), metaTerm.getText());
        double progressTotal = 0;

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

            int subjectNum = term.getSubjects().size();
            for(int subjectIndex = 0; subjectIndex < subjectNum; ++subjectIndex){
                if(isCancelled()){
                    return null;
                }
                Subject subject = term.getSubjects().get(subjectIndex);
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

                progressTotal = (subjectIndex + 1) / (double)subjectNum;
                int courseNum = subject.getCourses().size();
                for(int courseIndex = 0; courseIndex < courseNum; ++courseIndex){
                    if(isCancelled()){
                        return null;
                    }
                    Course course = subject.getCourses().get(courseIndex);
                    HttpsURLConnection courseUrlConnection = (HttpsURLConnection)new URL(course.getHref()).openConnection();
                    int courseResponseCode = courseUrlConnection.getResponseCode();

                    if(courseResponseCode == HttpsURLConnection.HTTP_OK){
                        String courseResponseString = Utilities.readStream(courseUrlConnection.getInputStream());

                        Document courseDoc = dBuilder.parse(new InputSource(new StringReader(courseResponseString)));
                        courseDoc.getDocumentElement().normalize();

                        NodeList descriptions = courseDoc.getElementsByTagName("description");
                        if(descriptions.getLength() != 0) {
                            course.setDescription(descriptions.item(0).getTextContent());
                        }
                        NodeList creditHours =  courseDoc.getElementsByTagName("creditHours");
                        if(creditHours.getLength() != 0) {
                            course.setCreditHours(creditHours.item(0).getTextContent());
                        }

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

                    double progressCourse = (1.0 / courseNum) / (double)subjectNum;
                    progressTotal += progressCourse;
                    publishProgress((int)(progressTotal * 100.0));
                    int sectionNum = course.getSections().size();
                    for(int sectionIndex = 0; sectionIndex < sectionNum; ++sectionIndex){
                        if(isCancelled()){
                            return null;
                        }
                        Section section = course.getSections().get(sectionIndex);
                        HttpsURLConnection sectionUrlConnection = (HttpsURLConnection)new URL(section.getHref()).openConnection();
                        int sectionResponseCode = sectionUrlConnection.getResponseCode();

                        if(sectionResponseCode == HttpsURLConnection.HTTP_OK){
                            String sectionResponseString = Utilities.readStream(sectionUrlConnection.getInputStream());

                            Document sectionDoc = dBuilder.parse(new InputSource(new StringReader(sectionResponseString)));
                            sectionDoc.getDocumentElement().normalize();

                            NodeList parentTermNodeList = sectionDoc.getElementsByTagName("term");
                            if(parentTermNodeList.getLength() > 0){
                                section.setTerm(parentTermNodeList.item(0).getTextContent());
                            }

                            NodeList parentSubjectNodeList = sectionDoc.getElementsByTagName("subject");
                            if(parentSubjectNodeList.getLength() > 0){
                                Node node = parentSubjectNodeList.item(0);
                                section.setSubject(node.getTextContent());
                                section.setSubjectId(node.getAttributes().getNamedItem("id").getNodeValue());
                            }

                            NodeList parentCourseNodeList = sectionDoc.getElementsByTagName("course");
                            if(parentCourseNodeList.getLength() > 0){
                                Node node = parentCourseNodeList.item(0);
                                section.setCourse(node.getTextContent());
                                section.setCourseId(node.getAttributes().getNamedItem("id").getNodeValue());
                            }

                            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd-HH:mm");
                            NodeList startDates = sectionDoc.getElementsByTagName("startDate");
                            if (startDates.getLength() == 0){
                                // There is no start or end dates
                                continue;
                            }
                            DateTime startDate = format.parseDateTime(startDates.item(0).getTextContent());
                            DateTime endDate = format.parseDateTime(sectionDoc.getElementsByTagName("endDate").item(0).getTextContent());
                            section.setStartDate(startDate);
                            section.setEndDate(endDate);

                            NodeList meetingsNodes = sectionDoc.getElementsByTagName("meeting");
                            for(int i = 0; i < meetingsNodes.getLength(); ++i){
                                Element element = (Element)meetingsNodes.item(i);
                                String id = element.getAttributes().getNamedItem("id").getNodeValue();
                                Meeting meeting = new Meeting(id);
                                NodeList typeNodeList = element.getElementsByTagName("type");
                                if(typeNodeList.getLength() > 0){
                                    Node typeNode = typeNodeList.item(0);
                                    meeting.setType(typeNode.getTextContent());
                                    meeting.setTypeCode(typeNode.getAttributes().getNamedItem("code").getNodeValue());
                                }
                                NodeList startNodeList = element.getElementsByTagName("start");
                                NodeList endNodeList = element.getElementsByTagName("end");
                                if(startNodeList.getLength() > 0 && endNodeList.getLength() > 0){
                                    DateTimeFormatter timeFormat = DateTimeFormat.forPattern("hh:mm a");
                                    String test = "02:00 PM";
                                    DateTime startTime = timeFormat.parseDateTime(startNodeList.item(0).getTextContent());
                                    DateTime endTime = timeFormat.parseDateTime(endNodeList.item(0).getTextContent());
                                    meeting.setStartTime(startTime);
                                    meeting.setEndTime(endTime);
                                }
                                NodeList daysNodeList = element.getElementsByTagName("daysOfTheWeek");
                                if(daysNodeList.getLength() > 0){
                                    meeting.setDaysOfTheWeek(daysNodeList.item(0).getTextContent());
                                }
                                NodeList roomNodeList = element.getElementsByTagName("roomNumber");
                                if(roomNodeList.getLength() > 0){
                                    meeting.setRoomNumber(roomNodeList.item(0).getTextContent());
                                }
                                NodeList buildingNodeList = element.getElementsByTagName("buildingName");
                                if(buildingNodeList.getLength() > 0){
                                    meeting.setBuildingName(buildingNodeList.item(0).getTextContent());
                                }
                                NodeList instructorNodeList = element.getElementsByTagName("instructor");
                                for(int j = 0; j < instructorNodeList.getLength(); ++j){
                                    meeting.insertInstructor(instructorNodeList.item(j).getTextContent());
                                }
                                section.insertMeeting(meeting);
                            }

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
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(DateTime.class, new DateTimeConverter())
                    .create();
            String termJson = gson.toJson(term);
            String fileName = term.getLabel() + ".json";
            File scheduleDir = context.getDir("schedules", Context.MODE_PRIVATE);
            if(!scheduleDir.exists()){
                scheduleDir.mkdirs();
            }

            if(isCancelled()){
                return null;
            }
            FileOutputStream outputStream = new FileOutputStream(new File(scheduleDir, fileName));
            outputStream.write(termJson.getBytes());
            outputStream.close();

            return term;
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
        metaTerm.getDownloadProgress().setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Term term){
        if(term == null){
            return;
        }
        metaTerm.getDownloadingLayout().setVisibility(View.GONE);
        metaTerm.getDownloadedButton().setVisibility(View.VISIBLE);
    }
}