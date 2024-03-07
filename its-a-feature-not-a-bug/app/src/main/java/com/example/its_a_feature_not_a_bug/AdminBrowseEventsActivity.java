package com.example.its_a_feature_not_a_bug;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Extends BrowseEventsActivity to inherit its basic functionalities
public class AdminBrowseEventsActivity extends BrowseEventsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the FloatingActionButton since admins won't be adding events from this screen.
        FloatingActionButton fabAddEvent = findViewById(R.id.fab_add_event);
        fabAddEvent.setVisibility(View.GONE);

        // Implement additional customizations needed for the admin view here
        // such as removing events.

    }


}


