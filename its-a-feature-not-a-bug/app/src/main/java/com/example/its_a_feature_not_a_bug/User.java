package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String name;
    private List<Event> signedEvents;

    private List<Event> checkedEvents;

    private Map<String, Integer> NumTimesCheckedIn;

    public User(String name) {
        this.name = name;
        this.signedEvents = new ArrayList<>();
        this.checkedEvents = new ArrayList<>();
        NumTimesCheckedIn = new HashMap<String, Integer>();
    }

    public void signUpForEvent(Event event) {
        this.signedEvents.add(event);
    }

//    public void checkInToEvent(Event event){
//        //TODO: functionality for actually checking into event
//        String eventTitle = event.getTitle();
//
//        // Check if the event title is already in the HashMap
//        if (NumTimesCheckedIn.containsKey(eventTitle)) {
//            // If it exists, increment the value of the integer key
//            int currentCheckIns = NumTimesCheckedIn.get(eventTitle);
//            NumTimesCheckedIn.put(eventTitle, currentCheckIns + 1);
//        } else {
//            // If it doesn't exist, initialize with value 1
//            this.checkedEvents.add(event);
//            NumTimesCheckedIn.put(eventTitle, 1);
//        }
//    }


    public List<Event> getSignedUpEvents() {
        return signedEvents;
    }

    public List<Event> getCurrentEvents() {
        List<Event> currentEvents = new ArrayList<>();
        Date now = new Date();
        for (Event event : signedEvents) {
            if (event.getDate().equals(now)) {
                currentEvents.add(event);
            }
        }
        return currentEvents;
    }


    public List<Event> getFutureEvents() {
        List<Event> futureEvents = new ArrayList<>();
        Date now = new Date();
        for (Event event : signedEvents) {
            if (event.getDate().after(now)) {
                futureEvents.add(event);
            }
        }
        return futureEvents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getSignedEvents() {
        return signedEvents;
    }

    public void setSignedEvents(List<Event> signedEvents) {
        this.signedEvents = signedEvents;
    }

    public List<Event> getCheckedEvents() {
        return checkedEvents;
    }

    public void setCheckedEvents(List<Event> checkedEvents) {
        this.checkedEvents = checkedEvents;
    }

    public int getNumTimesCheckedIn(Event event){
        return NumTimesCheckedIn.get(event.getTitle());
    }
}