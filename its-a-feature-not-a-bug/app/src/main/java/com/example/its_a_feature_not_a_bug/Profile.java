// This source code file implements profiles.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.net.Uri;

import java.io.Serializable;

/**
 * A class representing a user's profile.
 */
public class Profile implements Serializable {
    private Uri profilePicId; // User profile pic
    private String fullName; // Full name of the user
    private String email; // User email
    private String phoneNumber; // User phone number
    private boolean geolocationDisabled;

    /**
     * This is a constructor for the class
     */
    public Profile() {
    }

    /**
     * This is a constructor for the class.
     * @param fullName the profile name
     * @param contactInfo the user's contact info
     * @param geolocationDisabled the user's option of geolocation
     */
    public Profile(String fullName, String contactInfo, boolean geolocationDisabled){
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.geolocationDisabled = geolocationDisabled;
    }


    /**
     * This is a constructor for the class
     * @param profilePicId the profile image
     * @param fullName the profile name
     * @param email the users email address
     * @param phoneNumber the users phone number
     */
    public Profile(Uri profilePicId, String fullName, String email, String phoneNumber) {
        this.profilePicId = profilePicId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * This returns the Uri of the profile image.
     * @return the image Uri
     */
    public Uri getProfilePicId() {
        return profilePicId;
    }

    /**
     * This sets the Uri of the profile image to a new value.
     * @param profilePicId the new Uri
     */
    public void setProfilePicId(Uri profilePicId) {
        this.profilePicId = profilePicId;
    }

    /**
     * This returns the profile name.
     * @return the profile name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * This sets the profile name to a new value.
     * @param fullName the new name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * This returns the users email address
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * This sets the email address to a new value.
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This returns the users phone number
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * This sets the phone number to a new value.
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}


