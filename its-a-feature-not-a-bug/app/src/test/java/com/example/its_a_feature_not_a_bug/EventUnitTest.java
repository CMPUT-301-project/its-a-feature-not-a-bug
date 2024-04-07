package com.example.its_a_feature_not_a_bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EventUnitTest {
    private Event mockEvent() {
        return new Event(null, "Test Event", "Brayden", null, "This is a test event.", 6);
    }

    // test signed attendee methods
    @Test
    public void testAddSignedAttendee() {
        Event event = mockEvent();
        event.addSignedAttendee("User 1");
        event.addSignedAttendee("User 2");
        assertEquals(2, event.getNumberSignedAttendees());
        assertTrue(event.hasSignedAttendee("User 1"));
        assertTrue(event.hasSignedAttendee("User 2"));
    }

    @Test
    public void testRemoveSignedAttendee() {
        Event event = mockEvent();
        event.addSignedAttendee("User 1");
        assertEquals(1, event.getNumberSignedAttendees());
        assertTrue(event.hasSignedAttendee("User 1"));
        event.removeSignedAttendee("User 1");
        assertEquals(0, event.getNumberSignedAttendees());
        assertFalse(event.hasSignedAttendee("User 1"));
    }

    @Test
    public void testHasSignedAttendee() {
        Event event = mockEvent();
        event.addSignedAttendee("User 1");
        assertTrue(event.hasSignedAttendee("User 1"));
        assertFalse(event.hasSignedAttendee("User 2"));
    }

    @Test
    public void testGetNumberSignedAttendees() {
        Event event = mockEvent();
        assertEquals(0, event.getNumberSignedAttendees());
        event.addSignedAttendee("User 1");
        assertEquals(1, event.getNumberSignedAttendees());
        event.removeSignedAttendee("User 1");
        assertEquals(0, event.getNumberSignedAttendees());
    }

    // test checked-in attendee methods
    @Test
    public void testAddCheckIn() {
        Event event = mockEvent();
        event.addCheckIn("User 1");
        event.addCheckIn("User 2");
        assertEquals(2, event.getNumberCheckIns());
        assertTrue(event.hasCheckIn("User 1"));
        assertTrue(event.hasCheckIn("User 2"));
    }

    @Test
    public void testRemoveCheckIn() {
        Event event = mockEvent();
        event.addCheckIn("User 1");
        assertEquals(1, event.getNumberCheckIns());
        assertTrue(event.hasCheckIn("User 1"));
        event.removeCheckIn("User 1");
        assertEquals(0, event.getNumberCheckIns());
        assertFalse(event.hasCheckIn("User 1"));
    }

    @Test
    public void testHasCheckIn() {
        Event event = mockEvent();
        event.addCheckIn("User 1");
        assertTrue(event.hasCheckIn("User 1"));
        assertFalse(event.hasCheckIn("User 2"));
    }

    @Test
    public void testGetNumberCheckIns() {
        Event event = mockEvent();
        assertEquals(0, event.getNumberCheckIns());
        event.addCheckIn("User 1");
        assertEquals(1, event.getNumberCheckIns());
        event.removeCheckIn("User 1");
        assertEquals(0, event.getNumberCheckIns());
    }

    // test announcement methods
    @Test
    public void testAddAnnouncement() {
        Event event = mockEvent();
        event.addAnnouncement("Announcement 1");
        event.addAnnouncement("Announcement 2");
        assertEquals(2, event.getNumberAnnouncements());
        assertTrue(event.hasAnnouncement("Announcement 1"));
        assertTrue(event.hasAnnouncement("Announcement 2"));
    }

    @Test
    public void testRemoveAnnouncement() {
        Event event = mockEvent();
        event.addAnnouncement("Announcement 1");
        assertEquals(1, event.getNumberAnnouncements());
        assertTrue(event.hasAnnouncement("Announcement 1"));
        event.removeAnnouncement("Announcement 1");
        assertEquals(0, event.getNumberAnnouncements());
        assertFalse(event.hasAnnouncement("Announcement 1"));
    }

    @Test
    public void testHasAnnouncement() {
        Event event = mockEvent();
        event.addAnnouncement("Announcement 1");
        assertTrue(event.hasAnnouncement("Announcement 1"));
        assertFalse(event.hasAnnouncement("Announcement 2"));
    }

    @Test
    public void testGetNumberAnnouncements() {
        Event event = mockEvent();
        assertEquals(0, event.getNumberAnnouncements());
        event.addAnnouncement("Announcement 1");
        assertEquals(1, event.getNumberAnnouncements());
        event.removeAnnouncement("Announcement 1");
        assertEquals(0, event.getNumberAnnouncements());
    }
}
