package com.example.its_a_feature_not_a_bug;
import org.junit.Test;
import static org.junit.Assert.*;


public class AddEventTest {
    @Test
    public void testAddEvent() {
        Event event = new Event(null, "Test Event", "Test Host", null, "Test Description", 10);
        assertEquals("Test Event", event.getTitle());
        assertEquals("Test Host", event.getHost());
        assertEquals("Test Description", event.getDescription());
        assertEquals(10, event.getAttendeeLimit());
    }
}
