// This source code file implements the functionality of a user selecting what page to browse as an admin.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * An activity that serves as the dashboard for administrators.
 * This activity extends AppCompatActivity.
 */
public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("ADMIN"); // Set the title for the action bar
        }

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));

        actionBar.setBackgroundDrawable(colorDrawable);

        Button buttonEvents = findViewById(R.id.buttonEvents);
        Button buttonProfiles = findViewById(R.id.buttonProfiles);

        buttonEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the events management activity
                Intent intent = new Intent(AdminDashboardActivity.this, AdminBrowseEventsActivity.class);
                startActivity(intent);
            }
        });

        buttonProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the profiles management activity
                // You'll need to create this activity if it doesn't exist
                Intent intent = new Intent(AdminDashboardActivity.this, AdminBrowseProfilesActivity.class);
                startActivity(intent);
            }
        });
    }
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
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
