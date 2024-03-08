package com.example.its_a_feature_not_a_bug;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String name;
    private List<Event> events;

    private Map<String, Integer> NumTimesCheckedIn;

    public User(String name) {
        this.name = name;
        this.events = new ArrayList<>();
        NumTimesCheckedIn = new HashMap<String, Integer>();
    }

    public void signUpForEvent(Event event) {
        String eventTitle = event.getTitle();

        // Check if the event title is already in the HashMap
        if (NumTimesCheckedIn.containsKey(eventTitle)) {
            // If it exists, increment the value of the integer key
            int currentCheckIns = NumTimesCheckedIn.get(eventTitle);
            NumTimesCheckedIn.put(eventTitle, currentCheckIns + 1);
        } else {
            // If it doesn't exist, initialize with value 1
            this.events.add(event);
            NumTimesCheckedIn.put(eventTitle, 1);
        }
    }


    public List<Event> getSignedUpEvents() {
        return events;
    }

    public List<Event> getCurrentEvents() {
        List<Event> currentEvents = new ArrayList<>();
        Date now = new Date();
        for (Event event : events) {
            if (event.getDate().equals(now)) {
                currentEvents.add(event);
            }
        }
        return currentEvents;
    }


    public List<Event> getFutureEvents() {
        List<Event> futureEvents = new ArrayList<>();
        Date now = new Date();
        for (Event event : events) {
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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public int getNumTimesCheckedIn(Event event){
        return NumTimesCheckedIn.get(event.getTitle());
    }
}