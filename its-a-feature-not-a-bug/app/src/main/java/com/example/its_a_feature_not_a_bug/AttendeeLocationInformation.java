// This source code file implements the class that represents an attendee's check-in location.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;

/**
 * This implements the location class.
 */
public class AttendeeLocationInformation implements Serializable {
    private String latitude;

    private String longitude;

    private String title;

    /**
     * This is a constructor that takes no arguments.
     */
    public AttendeeLocationInformation() {}

    /**
     * This is a constructor that takes the latitude and longitde as arguments.
     * @param latitude this is the latitude
     * @param longitude this is the longitude
     */
    public AttendeeLocationInformation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * This returns the latitude of the location.
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * This sets the latitude of the location to a new value.
     * @param latitude the new longitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * This returns the longitude of the location.
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * This sets the longitude of the location to a new value.
     * @param longitude the new longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * This returns the event id that the user checked-in to.
     * @return the event id
     */
    public String getTitle() {
        return title;
    }

    /**
     * This sets the event id to a new value.
     * @param title the new event id
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
