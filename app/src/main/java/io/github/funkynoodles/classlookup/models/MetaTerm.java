package io.github.funkynoodles.classlookup.models;

import android.view.View;
import android.widget.Button;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.Serializable;

import io.github.funkynoodles.classlookup.adapters.CalendarYearsListAdapter;
import io.github.funkynoodles.classlookup.tasks.DownloadTermTask;

public class MetaTerm implements Serializable{
    private String id;
    private String href;
    private String text;
    private NumberProgressBar downloadProgress;
    private Button downloadButton;
    private Button downloadedButton;
    private View downloadingLayout;
    private DownloadTermTask downloadTermTask = null;

    public MetaTerm(String id, String href, String text){
        this.id = id;
        this.href = href;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NumberProgressBar getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(NumberProgressBar downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public Button getDownloadButton() {
        return downloadButton;
    }

    public void setDownloadButton(Button downloadButton) {
        this.downloadButton = downloadButton;
    }

    public Button getDownloadedButton() {
        return downloadedButton;
    }

    public void setDownloadedButton(Button downloadedButton) {
        this.downloadedButton = downloadedButton;
    }

    public View getDownloadingLayout() {
        return downloadingLayout;
    }

    public void setDownloadingLayout(View downloadingLayout) {
        this.downloadingLayout = downloadingLayout;
    }

    public void cancelDownloadTermTask() {
        downloadTermTask.cancel(true);
        downloadTermTask = null;
    }

    public void executeDownloadTermTask(){
        if(downloadTermTask != null) {
            downloadTermTask.execute();
        }
    }

    public void setDownloadTermTask(DownloadTermTask downloadTermTask) {
        if(this.downloadTermTask == null) {
            this.downloadTermTask = downloadTermTask;
        }
    }
}
