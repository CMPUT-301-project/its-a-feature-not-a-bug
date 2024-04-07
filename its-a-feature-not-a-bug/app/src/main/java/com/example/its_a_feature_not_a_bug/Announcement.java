// This source code file implements the class that represents an announcement.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;

/**
 * A class representing an announcement.
 */
public class Announcement implements Serializable {
    // Attributes
    public String announcementId;
    private String eventId;
    private String title;
    private String description;

    // Constructors
    /**
     * This is a constructor for the Announcement class that takes no arguments.
     */
    public Announcement() {}

    /**
     * This is a constructor for the Announcement class that takes all attributes as arguments.
     * @param eventId the ID of the event that this announcement is for
     * @param title the title of the announcement
     * @param description the message of the announcement
     */
    public Announcement(String announcementId, String eventId, String title, String description) {
        this.announcementId = announcementId;
        this.eventId = eventId;
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    /**
     * This returns the announcement ID.
     * @return the announcement ID
     */
    public String getAnnouncementId() {
        return announcementId;
    }

    /**
     * This sets the announcement ID to a new value.
     * @param announcementId the new announcement ID
     */
    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    /**
     * This returns the ID of the event that this announcement is for.
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * This sets the ID of the event to a new value.
     * @param eventId the new event ID
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * This returns the title of the announcement.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * This sets the title of the announcement to a new value.
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This returns the message of the announcement.
     * @return the message
     */
    public String getDescription() {
        return description;
    }

    /**
     * This sets the message of the announcement to a new value.
     * @param description the new message
     */
    public void setDescription(String description) {
        this.description = description;
    }
}