// This source code file implements the class that represents users.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class implements users
 */
public class User implements Serializable {
    // Attributes
    private String userId; // id of the user in the database
    private String imageId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private boolean geoLocationDisabled;

    // Constructors
    /**
     * This is a constructor for the User class that takes no arguments
     */
    public User(){}

    /**
     * This is a constructor for the User class that takes all attributes as arguments.
     * @param imageId the URL of the user's profile image
     * @param fullName the full name of the user
     * @param email the email of the user
     * @param phoneNumber the phone number of the user
     * @param geoLocationDisabled whether the user allows geolocation tracking
     */
    public User(String userId, String imageId, String fullName, String email, String phoneNumber, boolean geoLocationDisabled, List<String> signedEvents, List<String> checkedEvents, Map<String, Integer> numTimesCheckedIn) {
        this.userId = userId;
        this.imageId = imageId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.geoLocationDisabled = geoLocationDisabled;
    }

    // Getters and Setters
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
     * This returns the Firebase Storage URL of the profile picture of the user.
     * @return the image URL
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * This sets the Firebase Storage URL of the profile picture to a new value.
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
     * This returns the phone number of the user, as a String.
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
}
