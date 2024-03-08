package com.example.its_a_feature_not_a_bug;

import org.junit.Test;
import static org.junit.Assert.*;

public class AddProfileTest {
    @Test
    public void testAddProfile() {
        User user = new User("Test User");
        assertEquals("Test User", user.getName());
    }
}
