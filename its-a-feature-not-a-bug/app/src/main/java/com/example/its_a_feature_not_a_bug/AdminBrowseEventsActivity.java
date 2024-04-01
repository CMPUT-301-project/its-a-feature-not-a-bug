// This source code file implements the functionality for a user to browse events as an admin.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * An activity that allows administrators to browse events.
 * This activity extends BrowseEventsActivity to inherit its basic functionalities.
 */
public class AdminBrowseEventsActivity extends BrowseEventsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EVENTS" + "</b></font>"));
        }

        // Hide add event fab
        FloatingActionButton fabAddEvent = findViewById(R.id.fab_add_event);
        fabAddEvent.setVisibility(View.GONE);

        // Hide update profile fab
        Button profileButton = findViewById(R.id.button_profile);
        profileButton.setVisibility(View.GONE);

        // Hide camera Button
        Button cameraButton = findViewById(R.id.button_camera);
        cameraButton.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}