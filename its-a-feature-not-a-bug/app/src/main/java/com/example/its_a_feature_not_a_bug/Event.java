package com.example.its_a_feature_not_a_bug;

import android.widget.ImageView;

import java.util.Date;

public class Event {
    private int imageID; // event poster
    private String title; // name of the event
    private User host; // user that created the event
    private Date date; // date of the event
    private String description; // short description of the event posted by the host

    public Event(String title, User host, Date date, String description) {
        this.title = title;
        this.host = host;
        this.date = date;
        this.description = description;
    }

    public int getPoster() {
        return this.imageID;
    }
}
