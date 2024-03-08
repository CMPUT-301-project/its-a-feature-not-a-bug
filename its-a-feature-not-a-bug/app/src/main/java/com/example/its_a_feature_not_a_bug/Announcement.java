// This source code file implements the class that represents an announcement to be displayed on an event page.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;

/**
 * A class representing an announcement.
 */
public class Announcement implements Serializable {
    private String title;
    private String description;

    /**
     * This is a constructor for the class.
     * @param title the title of the announcement
     * @param description the message of the announcement
     */
    public Announcement(String title, String description) {
        this.title = title;
        this.description = description;
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