package com.example.its_a_feature_not_a_bug;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String name;
    private List<Event> events;

    public User(String name) {
        this.name = name;
        this.events = new ArrayList<>();
    }

    public void signUpForEvent(Event event) {
        this.events.add(event);
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
}