// This source code file implements the class that represents events.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A class representing an event.
 */
public class Event implements Serializable {
    // Attributes
    private String imageId; // event poster
    private String title; // name of the event
    private String host; // user that created the event
    private Date date; // date of the event
    private String description; // short description of the event posted by the host
    private Integer attendeeLimit; // maximum number of attendees
    private ArrayList<String> signedAttendees; // list of users signed up for the event
    private ArrayList<String> checkedInAttendees; // list of users checked into the event
    private ArrayList<String> announcements; // list of event annoucements

    private int attendeeCount;
    private Map<String, List<Double>> attendeeLocations;

// Constructors

    /**
     * This is a constructor for the Event class that takes no arguments.
     */
    public Event() {}

    /**
     * This is a constructor for the Event class that initializes everything except for the attendee and announcement lists.
     * @param imageId the image (event poster) of the event
     * @param title the name of the event
     * @param host the userId of the event organizer
     * @param date the date the event will take place
     * @param description a short description describing the event
     * @param attendeeLimit the maximum number of attendees
     */
    public Event(String imageId, String title, String host, Date date, String description, Integer attendeeLimit, Integer attendeeCount) {
        this.imageId = imageId;
        this.title = title;
        this.host = host;
        this.date = date;
        this.description = description;
        this.attendeeLimit = attendeeLimit;
        this.attendeeCount = attendeeCount;
    }

    public Event(String imageId, String title, String host, Date date, String description, Integer attendeeLimit, ArrayList<String> checkedInAttendees) {
        this.imageId = imageId;
        this.title = title;
        this.host = host;
        this.date = date;
        this.description = description;
        this.attendeeLimit = attendeeLimit;
        this.checkedInAttendees = checkedInAttendees;
    }

    // Getters and Setters
    /**
     * This returns the Firebase Storage URL of the event image.
     * @return the event uri
     */
    public String getImageId() {
        return this.imageId;
    }

    /**
     * This sets the Firebase Storage URL of the event image to a new value.
     * @param imageId the new image URL
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    /**
     * This returns the name of the event.
     * @return the event name
     */
    public String getTitle() {
        return title;
    }

    /**
     * This sets the name of the event to a new value.
     * @param title the new event name
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This returns the userId of the event organizer.
     * @return the userId of the event organizer
     */
    public String getHost() {
        return host;
    }

    /**
     * This sets the organizer userId of the event to a new value.
     * @param host the new event organizer userId
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
     * @param date the new event date
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
     * @param description the new event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This returns the maximum number of attendees for an event.
     * @return the maximum number of attendees
     */
    public Integer getAttendeeLimit() {
        return attendeeLimit;
    }

    /**
     * This sets the maximum number of attendees to a new value.
     * @param attendeeLimit the new maximum number of attendees
     */
    public void setAttendeeLimit(Integer attendeeLimit) {
        this.attendeeLimit = attendeeLimit;
    }

    /**
     * This returns the userIds of the attendees signed up for the event.
     * @return the list of attendee userIds
     */
    public ArrayList<String> getSignedAttendees() {
        return signedAttendees;
    }

    /**
     * This sets the list of attendees signed up for the event to a new value.
     * @param attendees the new list of attendees
     */
    public void setSignedAttendees(ArrayList<String> attendees) {
        this.signedAttendees = attendees;
    }

    /**
     * Returns the userIds of users who have checked in to the event.
     * @return the userIds
     */
    public ArrayList<String> getCheckedInAttendees() {
        return checkedInAttendees;
    }

    /**
     * Sets the list of attendees who have checked in to a new value.
     * @param checkedInAttendees the new list of attendees
     */
    public void setCheckedInAttendees(ArrayList<String> checkedInAttendees) {
        this.checkedInAttendees = checkedInAttendees;
    }

    /**
     * This returns the list of announcements for the event.
     * @return the list of announcements
     */
    public ArrayList<String> getAnnouncements() {
        return announcements;
    }

    /**
     * This sets the list of announcements to a new value.
     * @param announcements the new announcements
     */
    public void setAnnouncements(ArrayList<String> announcements) {
        this.announcements = announcements;
    }

    // Extra methods for signedAttendees
    /**
     * This adds an attendee to the list of signed attendees.
     * @param signedAttendee the attendee to be added
     */
    public void addSignedAttendee(String signedAttendee) {
        if (signedAttendees == null) {
            this.signedAttendees = new ArrayList<>();
        }
        this.signedAttendees.add(signedAttendee);

    }

    /**
     * This removes an attendee from the list of signed attendees.
     * @param signedAttendee the attendee to be removed
     */
    public  void removeSignedAttendee(String signedAttendee) {
        if (signedAttendees == null) {
            throw new NullPointerException();
        }
        if (!signedAttendees.contains(signedAttendee)) {
            throw new IllegalArgumentException();
        }
        signedAttendees.remove(signedAttendee);
    }

    /**
     * This checks whether an attendee has signed up for the event.
     * @param signedAttendee the attendee to check
     * @return true if the attendee is in the list, false otherwise
     */
    public boolean hasSignedAttendee(String signedAttendee) {
        if (signedAttendees == null) {
            throw new NullPointerException();
        }
        return signedAttendees.contains(signedAttendee);
    }

    /**
     * This returns the number of attendees that have signed into the event.
     * @return the number of signed attendees
     */
    public int getNumberSignedAttendees() {
        if (signedAttendees == null) {
            return 0;
        }
        return signedAttendees.size();
    }

    // Extra methods for check-ins
    /**
     * This add an attendee to the list of checked-in attendees.
     * @param checkInAttendee the attendee to be added
     */
    public  void addCheckIn(String checkInAttendee) {
        if (checkedInAttendees == null) {
            this.checkedInAttendees = new ArrayList<>();
        }
        this.checkedInAttendees.add(checkInAttendee);
    }

    /**
     * This removes an attendee from the list of checked-in attendees.
     * @param checkInAttendee the attendee to be removed
     */
    public void removeCheckIn(String checkInAttendee) {
        if (checkedInAttendees == null) {
            throw new NullPointerException();
        }
        if (!checkedInAttendees.contains(checkInAttendee)) {
            throw new IllegalArgumentException();
        }
        checkedInAttendees.remove(checkInAttendee);
    }

    /**
     * This checks whether an attendee has checked-in to the event.
     * @param checkInAttendee the attendee to be checked
     * @return true if the attendee is in the list, false otherwise
     */
    public boolean hasCheckIn(String checkInAttendee) {
        if (checkedInAttendees == null) {
            throw new NullPointerException();
        }
        return checkedInAttendees.contains(checkInAttendee);
    }

    /**
     * Returns the number of unique attendee check-ins. If the list has not been initialized, return 0.
     * @return the number of check-ins
     */
    public int getNumberCheckIns() {
        if (checkedInAttendees == null) {
            return 0;
        }
        return checkedInAttendees.size();
    }

    // Extra methods for announcements

    /**
     * This adds an announcement to the list of announcement IDs
     * @param announcement the announcement ID to be added
     */
    public void addAnnouncement(String announcement) {
        if (announcements == null) {
            this.announcements = new ArrayList<>();
        }
        this.announcements.add(announcement);
    }

    /**
     * This removes an announcement from the list of announcement IDs
     * @param announcement the announcement ID to be removed
     */
    public void removeAnnouncement(String announcement) {
        if (announcements == null) {
            throw new NullPointerException();
        }
        if (!announcements.contains(announcement)) {
            throw new IllegalArgumentException();
        }
        announcements.remove(announcement);
    }

    /**
     * This checks whether the list of announcements has a certain ID.
     * @param announcement the announcement to be checked for
     * @return true if the announcement is in the list, false otherwise
     */
    public boolean hasAnnouncement(String announcement) {
        if (announcements == null) {
            throw new NullPointerException();
        }
        return announcements.contains(announcement);
    }

    /**
     * This returns the number of announcements in the list of announcements.
     * @return the number of announcements
     */
    public int getNumberAnnouncements() {
        if (announcements == null) {
            return 0;
        }
        return announcements.size();
    }

    public Map<String, List<Double>> getAttendeeLocations() {
        return attendeeLocations;
    }

    public void setAttendeeLocations(Map<String, List<Double>> attendeeLocations) {
        this.attendeeLocations = attendeeLocations;
    }
}