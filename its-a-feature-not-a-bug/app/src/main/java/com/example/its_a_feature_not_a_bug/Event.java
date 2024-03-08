package com.example.its_a_feature_not_a_bug;

import android.net.Uri;
import android.widget.ImageView;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is a class that represents an event created by a user. Other users can sign-up to attend
 * it, view it on the browsing page, or view its details.
 */
public class Event implements Serializable {
    private String imageId; // event poster
    private String title; // name of the event
    private String host; // user that created the event
    private Date date; // date of the event
    private ArrayList<String> attendees; // list of users attending the event
    private ArrayList<Announcement> announcements;
    private String description; // short description of the event posted by the host

    /**
     * This is a constructor for the Event class.
     * @param imageId the image (event poster)
     * @param title the name of the event
     * @param host the organizer of the event
     * @param date the date the event will take place
     * @param description a short description describing the event
     */
    public Event(String imageId, String title, String host, Date date, String description) {
        this.imageId = imageId;
        this.title = title;
        this.host = host;
        this.date = date;
        this.description = description;
    }

    /**
     * This returns the uri of the event image.
     * @return the event uri
     */
    public String getImageId() {
        return this.imageId;
    }

    /**
     * This sets the image of the event to a new value.
     * @param imageId the new image
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    /**
     * This returns the name of the event
     * @return the event name
     */
    public String getTitle() {
        return title;
    }

    /**
     * This sets the name of the event to a new value.
     * @param title the new name
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This returns the organizer of the event.
     * @return the event organizer
     */
    public String getHost() {
        return host;
    }

    /**
     * This sets the organizer of the event to a new value.
     * @param host the new organizer
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * This returns the date of the event.
     * @return the event date
     */
    public Date getDate() {
        return date;
    }

    /**
     * This sets the date of the event to a new value.
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * This returns the description of the event.
     * @return the event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * This sets the description of the event to a new value.
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This returns the list users attending of the event.
     * @return the event attendees
     */
    public ArrayList<String> getAttendees() {
        return attendees;
    }

    /**
     * This sets the list of users attending the event to a new value.
     * @param attendees the new attendees
     */
    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    /**
     * This returns the list of announcements of the event.
     * @return the event announcements
     */
    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    /**
     * This sets the list of announcements of the event to a new value.
     * @param announcements the new event announcements
     */
    public void setAnnouncements(ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }
}
