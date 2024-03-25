// This source code file implements the fuctionality for a user to browse profiles as an admin.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity that allows administrators to browse events.
 */
public class AdminBrowseProfilesActivity extends AppCompatActivity implements ProfileAdapter.OnProfileClickListener {
    private FirebaseFirestore db;
    private RecyclerView profilesRecyclerView;
    private ProfileAdapter profileAdapter;
    private List<Profile> profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profiles);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "PROFILES" + "</b></font>"));
        }


        // Connect to the database
        db = FirebaseFirestore.getInstance();
        profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        profileList = new ArrayList<>();

        setupRecyclerView();
        loadProfiles();
    }

    /**
     * This sets up a recycler view.
     */
    private void setupRecyclerView() {
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileAdapter = new ProfileAdapter(this, profileList, this); // Passing 'this' as the OnProfileClickListener
        profilesRecyclerView.setAdapter(profileAdapter);
    }

    /**
     * This loads the profiles from the database and adds them to a list.
     */
    private void loadProfiles() {
        db.collection("profiles").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(AdminBrowseProfilesActivity.this, "Error loading profiles", Toast.LENGTH_SHORT).show();
                    return;
                }

                profileList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Profile profile = document.toObject(Profile.class);
                    // Assuming your Profile model has a method to set the document ID
                    // profile.setId(document.getId());
                    profileList.add(profile);
                }
                profileAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Allows the user to select a profile to be deleted by clicking on it.
     * @param profile the profile to be deleted
     */
    @Override
    public void onProfileClick(Profile profile) {
        // Start ProfileDetailsActivity and pass the profile details
        Intent intent = new Intent(AdminBrowseProfilesActivity.this, ProfileDetailsActivity.class);
        intent.putExtra("profile", profile);
        startActivity(intent);
//        new AlertDialog.Builder(this)
//                .setTitle("Delete Profile")
//                .setMessage("Are you sure you want to delete this profile?")
//                .setPositiveButton("Yes", (dialog, which) -> deleteProfile(profile))
//                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                .create().show();
    }

    /**
     * Deletes a profile from the database.
//     * @param profile the profile to be deleted.
     */
//    private void deleteProfile(Profile profile) {
//        // Here you should use a unique identifier for the profile to delete.
//        // This could be a userID or any unique field in your Profile model.
//        // For demonstration purposes, let's assume you have a unique ID and it's accessible via profile.getId()
//        String profileId = profile.getFullName(); // Adjust according to your unique identifier
//
//        db.collection("profiles").document(profileId)
//                .delete()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(AdminBrowseProfilesActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
//                    profileList.remove(profile);
//                    profileAdapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> Toast.makeText(AdminBrowseProfilesActivity.this, "Error deleting profile", Toast.LENGTH_SHORT).show());
//    }

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