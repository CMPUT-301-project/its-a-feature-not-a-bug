// This source code file implements the Event class.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * A class representing an event.
 */
public class Event implements Serializable {
    private String imageId; // event poster
    private String title; // name of the event
    private String host; // user that created the event
    private Date date; // date of the event
    private ArrayList<String> signedAttendees; // list of users signed up for the event
    private ArrayList<Announcement> announcements;
    private ArrayList<String> checkedAttendees; // second value is the number of times checked in
    private String description; // short description of the event posted by the host
    private Integer attendeeLimit;
    private Integer attendeeCount;

    public Event() {}

    /**
     * This is a constructor for the Event class.
     * @param imageId the image (event poster)
     * @param title the name of the event
     * @param host the organizer of the event
     * @param date the date the event will take place
     * @param description a short description describing the event
     * @param attendeeLimit the max number of attendees
     */
    public Event(String imageId, String title, String host, Date date, String description, Integer attendeeLimit) {
        this.imageId = imageId;
        this.title = title;
        this.host = host;
        this.date = date;
        this.description = description;
        this.attendeeLimit = attendeeLimit;
    }

    /**
     * This is a constructor the the Event class.
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
     * This is a constructor the the Event class.
     * @param title the name of the event
     * @param host the organizer of the event
     * @param date the date the event will take place
     * @param description a short description describing the event
     */
    public Event(String title, Date date, String host, String description) {
        this.title = title;
        this.date = date;
        this.host = host;
        this.description = description;
    }

    public Event(String title, Date date, String host, String description, Integer attendeeLimit) {
        this.title = title;
        this.date = date;
        this.host = host;
        this.description = description;
        if (attendeeLimit != 0) {
            this.attendeeLimit = attendeeLimit;
        }
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
     * This gets the list of attendees signed up for the event.
     * @return the list of attendees
     */
    public ArrayList<String> getSignedAttendees() {
        return signedAttendees;
    }

    /**
     * This sets the list of attendees signed up for the event.
     * @param attendees the new list of attendees
     */
    public void setSignedAttendees(ArrayList<String> attendees) {
        this.signedAttendees = attendees;
    }

    /**
     * This returns the list of announcements for an event.
     * @return the list of announcements
     */
    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    /**
     * This sets the list of announcements to a new value.
     * @param announcements the new announcements
     */
    public void setAnnouncements(ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }

    /**
     * Returns the map of users who have checked in and the number of times each has done so.
     * @return the list of users and check-ins
     */
    public ArrayList<String> getCheckedAttendees() {
        return checkedAttendees;
    }

    /**
     * Sets the map of users:counts to a new value.
     * @param checkedAttendees the new map
     */
    public void setCheckedAttendees(ArrayList<String> checkedAttendees) {
        this.checkedAttendees = checkedAttendees;
    }

    /**
     * This returns the maximum number of attendees for an event.
     * @return the number of attendees
     */
    public Integer getAttendeeLimit() {
        return attendeeLimit;
    }

    /**
     * This sets the maximum number of attendees to a new value.
     * @param attendeeLimit the new number of attendees
     */
    public void setAttendeeLimit(Integer attendeeLimit) {
        this.attendeeLimit = attendeeLimit;
    }

    /**
     * The returns the number of attendees currently signed up for the event.
     * @return the number of attendees
     */
    public Integer getAttendeeCount() {
        return attendeeCount;
    }

    /**
     * This sets the number of attendees currently signed up for the event.
     * @param attendeeCount the new number of attendees
     */
    public void setAttendeeCount(Integer attendeeCount) {
        this.attendeeCount = attendeeCount;
    }

    /**
     * Returns the number of unique attendee check-ins.
     * @return the number of check-ins
     */
    public Integer getNumTimesCheckedIn() {
        return checkedAttendees.size();
    }
}