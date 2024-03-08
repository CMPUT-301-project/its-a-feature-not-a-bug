// This source code file implements the fuctionality for a user to browse profiles as an admin.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

        // Connect to the database
        db = FirebaseFirestore.getInstance();
        profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        profileList = new ArrayList<>();

        setupRecyclerView();
        loadProfiles();
    }

    private void setupRecyclerView() {
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileAdapter = new ProfileAdapter(this, profileList, this); // Passing 'this' as the OnProfileClickListener
        profilesRecyclerView.setAdapter(profileAdapter);
    }

    /**
     * Loads the profiles from the database and adds them to a list.
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
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile(profile))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    /**
     * Deletes a profile from the database.
     * @param profile the profile to be deleted.
     */
    private void deleteProfile(Profile profile) {
        // Here you should use a unique identifier for the profile to delete.
        // This could be a userID or any unique field in your Profile model.
        // For demonstration purposes, let's assume you have a unique ID and it's accessible via profile.getId()
        String profileId = profile.getFullName(); // Adjust according to your unique identifier

        db.collection("profiles").document(profileId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminBrowseProfilesActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    profileList.remove(profile);
                    profileAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(AdminBrowseProfilesActivity.this, "Error deleting profile", Toast.LENGTH_SHORT).show());
    }
}