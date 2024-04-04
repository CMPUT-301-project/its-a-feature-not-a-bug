// This source code file implements the class that represents users.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class implements users
 */
public class UserRefactored implements Serializable {
    private String userId; // id of the user in the database
    private String imageId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean geoLocationDisabled;
//    private List<String> signedEvents;
//    private List<String> checkedEvents;
//    private Map<String, Integer> numTimesCheckedIn;

    /**
     * This is a constructor for the User class that takes no arguments
     */
    public UserRefactored(){}

    /**
     * This is a constructor for the User class that takes all attributes as arguments.
     * @param imageId the URL of the user's profile image
     * @param fullName the full name of the user
     * @param email the email of the user
     * @param phoneNumber the phone number of the user
     * @param geoLocationDisabled whether the user allows geolocation tracking
     * @param signedEvents the events the user has signed up for
     * @param checkedEvents the events the user is checked into
     * @param numTimesCheckedIn the the number of times a user has checking into a signed event, for each signed event
     */
//    public UserRefactored(String userId, String imageId, String fullName, String email, String phoneNumber, boolean geoLocationDisabled, List<String> signedEvents, List<String> checkedEvents, Map<String, Integer> numTimesCheckedIn) {
//        this.userId = userId;
//        this.imageId = imageId;
//        this.fullName = fullName;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.geoLocationDisabled = geoLocationDisabled;
//        this.signedEvents = signedEvents;
//        this.checkedEvents = checkedEvents;
//        this.numTimesCheckedIn = numTimesCheckedIn;
//    }

    /**
     * This returns the ID of the user.
     * @return the ID of the user
     */
    public String getUserId() {
        return userId;
    }

    /**
     * This sets the ID of the user to a new value.
     * @param userId the new user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * This returns the URL of the profile picture of the user.
     * @return the image URL as a String
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * This sets the URL of the profile picture to a new value.
     * @param imageId the new image URL as a String
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    /**
     * This returns the name of the user.
     * @return the name of the user
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * This sets the name of the user to a new value
     * @param fullName the new name of the user
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * This returns the email of the user.
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * This sets the email of the user to a new value.
     * @param email the new email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This retuns the phone number of the user, as a String.
     * @return the phone number of the user
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * This sets the phone number of the user to a new value.
     * @param phoneNumber the new phone number of the user
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * This returns whether the user allows geolocation tracking (true = disabled).
     * @return the boolean representing their consent
     */
    public boolean isGeoLocationDisabled() {
        return geoLocationDisabled;
    }

    /**
     * Sets the consent of geolocation tracking for a user to a new value.
     * @param geoLocationDisabled the new value of the boolean
     */
    public void setGeoLocationDisabled(boolean geoLocationDisabled) {
        this.geoLocationDisabled = geoLocationDisabled;
    }

//    /**
//     * This returns the list of events that the user is signed up for.
//     * @return the list of events
//     */
//    public List<String> getSignedEvents() {
//        return signedEvents;
//    }

//    /**
//     * This sets the list of events that a user is signed up for to a new value.
//     * @param signedEvents the new list of events
//     */
//    public void setSignedEvents(List<String> signedEvents) {
//        this.signedEvents = signedEvents;
//    }

//    /**
//     * Adds the event to the user's signed events.
//     * @param event the event to be added
//     */
//    public void signUpForEvent(Event event) {
//        this.signedEvents.add(event.getTitle());
//    }

//    /**
//     * This returns the list of events that a user is checked into.
//     * @return the list of events
//     */
//    public List<String> getCheckedEvents() {
//        return checkedEvents;
//    }

//    /**
//     * This sets the list of events that a user is checked into to a new value.
//     * @param checkedEvents the new list of events
//     */
//    public void setCheckedEvents(List<String> checkedEvents) {
//        this.checkedEvents = checkedEvents;
//    }

//    /**
//     * This returns the map of the number of times a user has checked into each of their signed events.
//     * @return the map of events
//     */
//    public Map<String, Integer> getNumTimesCheckedIn() {
//        return numTimesCheckedIn;
//    }

//    /**
//     * This sets the the map of the number of times a user has checked into each of their signed events to a new value.
//     * @param numTimesCheckedIn the new map of events
//     */
//    public void setNumTimesCheckedIn(Map<String, Integer> numTimesCheckedIn) {
//        this.numTimesCheckedIn = numTimesCheckedIn;
//    }
}
