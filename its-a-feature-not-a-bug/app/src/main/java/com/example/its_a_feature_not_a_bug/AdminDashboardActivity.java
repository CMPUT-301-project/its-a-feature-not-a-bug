package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button buttonEvents = findViewById(R.id.buttonEvents);
        Button buttonProfiles = findViewById(R.id.buttonProfiles);

        Button backButton = findViewById(R.id.buttonBack);

        // Set OnClickListener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call finish() to close the current activity
                finish();
            }
        });

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
}
