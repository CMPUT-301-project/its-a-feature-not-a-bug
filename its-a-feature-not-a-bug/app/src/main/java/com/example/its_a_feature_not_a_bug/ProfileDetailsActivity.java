package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView profilesRecyclerView;
    private ProfileAdapter profileAdapter;
    private Profile profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details);

        db = FirebaseFirestore.getInstance();

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "PROFILE DETAILS" + "</b></font>"));

        }

        // Get the Profile object from the intent extra
        profile = (Profile) getIntent().getSerializableExtra("profile");

        // Initialize views
        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView fullNameTextView = findViewById(R.id.fullNameTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView phoneNumberTextView = findViewById(R.id.phoneNumberTextView);

        // Populate views with profile data
        if (profile != null) {
            // Load profile picture using Glide
            if (profile.getProfilePicId() != null) {
                Glide.with(this)
                        .load(profile.getProfilePicId())
                        .placeholder(R.drawable.default_profile_pic)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.default_profile_pic);
            }

            fullNameTextView.setText(profile.getFullName());
            emailTextView.setText(profile.getEmail());
            phoneNumberTextView.setText(profile.getPhoneNumber());
        }

        // Get reference to the delete profile button
        ImageView deleteProfileButton = findViewById(R.id.deleteProfileButton);

        // Set click listener for the delete profile button
        deleteProfileButton.setOnClickListener(v -> showDeleteConfirmationDialog());
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

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("OK", (dialog, which) -> deleteProfile())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteProfile() {
        // Assuming profile's full name is the unique identifier; please replace with a more suitable ID.
        String profileIdentifier = profile.getFullName(); // Ideally, use profile.getId() if you have an ID field.

        db.collection("profiles").document(profileIdentifier)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle successful deletion
                    Toast.makeText(ProfileDetailsActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, navigate the user away from the current activity, back to the profile list or previous activity
                    finish(); // Close the current activity
                })
                .addOnFailureListener(e -> {
                    // Handle any errors during deletion
                    Toast.makeText(ProfileDetailsActivity.this, "Error deleting profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}


