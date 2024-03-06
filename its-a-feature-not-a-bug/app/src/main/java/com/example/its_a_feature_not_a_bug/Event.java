package com.example.its_a_feature_not_a_bug;

import android.widget.ImageView;

import java.util.Date;

public class Event {
    private int imageID; // event poster
    private String title; // name of the event
    private String host; // user that created the event
    private Date date; // date of the event
    private String description; // short description of the event posted by the host

    public Event(String title, String host, Date date, String description) {
        this.title = title;
        this.host = host;
        this.date = date;
        this.description = description;
    }

    public int getPoster() {
        return this.imageID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
