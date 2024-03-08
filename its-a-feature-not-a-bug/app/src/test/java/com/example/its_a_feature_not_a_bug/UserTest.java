package com.example.its_a_feature_not_a_bug;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



public class UserTest {

    @Test
    public void testUserCreation() {
        String userName = "John Doe";
        User user = new User(userName);

        // Test user object creation
        assertNotNull(user);

        // Test user name assignment
        assertEquals(userName, user.getName());

        // Test initial signed events list is not null
        assertNotNull(user.getSignedEvents());

        // Test initial signed events list is empty
        assertEquals(0, user.getSignedEvents().size());

        // Test initial checked events list is not null
        assertNotNull(user.getCheckedEvents());

        // Test initial checked events list is empty
        assertEquals(0, user.getCheckedEvents().size());
    }

    @Test
    public void testSignUpForEvent() {
        User user = new User("John Doe");
        Event event = new Event("Event Title", new Date(), "Host", "Description");

        // Test signing up for an event
        user.signUpForEvent(event);

        // Test that the event is added to the signed events list
        assertEquals(1, user.getSignedEvents().size());
        assertEquals(event, user.getSignedEvents().get(0));
    }

    @Test
    public void testSetName() {
        String newName = "Jane Smith";
        User user = new User("John Doe");

        // Test setting a new name
        user.setName(newName);

        // Test that the name is correctly updated
        assertEquals(newName, user.getName());
    }

    @Test
    public void testSetAndGetSignedEvents() {
        User user = new User("John Doe");
        List<Event> events = new ArrayList<>();
        events.add(new Event("Event 1", new Date(), "Host 1", "Description 1"));
        events.add(new Event("Event 2", new Date(), "Host 2", "Description 2"));

        // Test setting signed events
        user.setSignedEvents(events);

        // Test getting signed events
        assertEquals(events, user.getSignedEvents());
    }

    @Test
    public void testSetAndGetCheckedEvents() {
        User user = new User("John Doe");
        List<Event> events = new ArrayList<>();
        events.add(new Event("Event 1", new Date(), "Host 1", "Description 1"));
        events.add(new Event("Event 2", new Date(), "Host 2", "Description 2"));

        // Test setting checked events
        user.setCheckedEvents(events);

        // Test getting checked events
        assertEquals(events, user.getCheckedEvents());
    }

    @Test
    public void testGetCurrentEvents() {
        User user = new User("John Doe");
        List<Event> events = new ArrayList<>();
        Date currentDate = new Date();
        events.add(new Event("Current Event 1", currentDate, "Host 1", "Description 1"));
        events.add(new Event("Current Event 2", currentDate, "Host 2", "Description 2"));

        // Test signing up for current events
        user.setSignedEvents(events);

        // Test getting current events
        assertEquals(events, user.getCurrentEvents());
    }

    @Test
    public void testGetFutureEvents() {
        User user = new User("John Doe");
        List<Event> events = new ArrayList<>();
        Date futureDate = new Date(System.currentTimeMillis() + 86400000); // Adding one day
        events.add(new Event("Future Event 1", futureDate, "Host 1", "Description 1"));
        events.add(new Event("Future Event 2", futureDate, "Host 2", "Description 2"));

        // Test signing up for future events
        user.setSignedEvents(events);

        // Test getting future events
        assertEquals(events, user.getFutureEvents());
    }
}
