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
    // private String userId; // Unique ID for the user
    private String fullName; // Full name of the user
    private String contactInfo; // User contact info

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
        this.contactInfo = contactInfo;
        this.geolocationDisabled = geolocationDisabled;
    }


    /**
     * This is a constructor for the class
     * @param profilePicId the profile image
     * @param userId the user's id
     * @param fullName the profile name
     * @param contactInfo the user's contact info
     */
    public Profile(Uri profilePicId, String userId, String fullName, String contactInfo) {
        this.profilePicId = profilePicId;
        // this.userId = userId;
        this.fullName = fullName;
        this.contactInfo = contactInfo;
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

//    public String getUserId() {
//        return userId;
//    }

//    public void setUserId(String userId) {
//        this.userId = userId;
//    }

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
     * This returns the contact info of the profile.
     * @return the contact info
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * This sets the contact info to a new value.
     * @param contactInfo the new contact info
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}


