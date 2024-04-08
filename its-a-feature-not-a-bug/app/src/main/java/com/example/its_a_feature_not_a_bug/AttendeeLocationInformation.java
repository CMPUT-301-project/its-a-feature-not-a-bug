package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;

public class AttendeeLocationInformation implements Serializable {
    private String latitude;

    private String longitude;

    private String title;

    public AttendeeLocationInformation() {}

    public AttendeeLocationInformation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
