package com.example.its_a_feature_not_a_bug;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private String eventName;
    private String eventDate;
    private String eventLocation;
    private String eventDescription;

    private String eventQRCode;
    private List<Attendee> attendees;


    public class Event(String eventName, String eventDate, String eventLocation, String eventDescription) {

        private List<Attendee> attendees;
        public Event(String eventName, String eventDate, String eventLocation, String eventDescription, List<Attendee> attendeeList) {
            this.eventName = eventName;
            this.eventDate = eventDate;
            this.eventLocation = eventLocation;
            this.eventDescription = eventDescription;
            this.eventQRCode = QRCodeGenerator.generateQRCode(this);
            this.attendees = new ArrayList<>();
        }
    }
    public void addAttendee(Attendee attendee) {
        this.attendees.add(attendee);
    }
    public List<Attendee> getCheckedInAttendees() {
        List<Attendee> checkedInAttendees = new ArrayList<>();
        for (Attendee attendee : this.attendees) {
            if (attendee.isCheckedIn()) {
                checkedInAttendees.add(attendee);
            }
        }
        return checkedInAttendees;
    }
    public List<Attendee> getAttendees() {
        return this.attendees;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventDescription() {
        return eventDescription;
    }
}
