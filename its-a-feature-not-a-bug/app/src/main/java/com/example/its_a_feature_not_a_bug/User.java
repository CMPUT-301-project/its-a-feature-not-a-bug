// This source code file implements the user class.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements users
 */
public class User implements Serializable {
    private String name;
    private List<Event> signedEvents;

    private List<Event> checkedEvents;

    private Map<String, Integer> NumTimesCheckedIn;

    /**
     * This is a constructor for the class
     * @param name the name of the user
     */
    public User(String name) {
        this.name = name;
        this.signedEvents = new ArrayList<>();
        this.checkedEvents = new ArrayList<>();
        NumTimesCheckedIn = new HashMap<String, Integer>();
    }

    /**
     * This allows a user to sign up for an event.
     * @param event the event
     */
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

    /**
     * This returns the list of events that the user is signed up for.
     * @return the list of events
     */
    public List<Event> getSignedUpEvents() {
        return signedEvents;
    }

    /**
     * This returns the list of events that are currently active.
     * @return the list of events.
     */
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

    /**
     * This returns the list of events that are not yet active.
     * @return the list of events
     */
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

    /**
     * This returns the name of the user.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * This sets the name of the user to a new value.
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This returns a list of events the user is signed up for.
     * @return the list of events.
     */
    public List<Event> getSignedEvents() {
        return signedEvents;
    }

    /**
     * This sets the list of events that a user is signed up for to a new value.
     * @param signedEvents the new list of events
     */
    public void setSignedEvents(List<Event> signedEvents) {
        this.signedEvents = signedEvents;
    }

    /**
     * This returns the list of events that the user is checked in to.
     * @return the list of events
     */
    public List<Event> getCheckedEvents() {
        return checkedEvents;
    }

    /**
     * This serts the list of checked-in events to a new value
     * @param checkedEvents the new event list
     */
    public void setCheckedEvents(List<Event> checkedEvents) {
        this.checkedEvents = checkedEvents;
    }

    /**
     * This returns the number of times this user has checked into an event.
     * @param event the event
     * @return the number of check-ins
     */
    public int getNumTimesCheckedIn(Event event){
        return NumTimesCheckedIn.get(event.getTitle());
    }
}