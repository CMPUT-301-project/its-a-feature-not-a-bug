package com.example.its_a_feature_not_a_bug;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminBrowseProfilesActivity extends AppCompatActivity implements ProfileAdapter.OnProfileClickListener{
    private FirebaseFirestore db;

    private CollectionReference profilesRef;
    private RecyclerView profilesRecyclerView;
    private ProfileAdapter profileAdapter;
    private List<Profile> profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profiles);

        // Connect to database
        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("profiles");
        profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        profileList = new ArrayList<>();

        setupRecyclerView();
        loadProfiles();

    }

    @Override
    public void onProfileClick(int position, Profile profile) {
        // Handle the click event with the position
        Toast.makeText(this, "Clicked profile at position " + position + ": " + profile.getFullName(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRemoveProfile(int position) {
        // Remove the profile at the specified position
        Profile removedProfile = profileList.remove(position);

        // Get the ID of the profile document to be deleted
        String profileIdToDelete = removedProfile.getFullName();

        // Delete the profile document from the Firestore database
        profilesRef.document(profileIdToDelete)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Profile successfully deleted from the database
                        profileAdapter.notifyItemRemoved(position);
                        Toast.makeText(AdminBrowseProfilesActivity.this, "Profile removed successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete the profile from the database
                        Toast.makeText(AdminBrowseProfilesActivity.this, "Failed to remove profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setupRecyclerView() {
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileAdapter = new ProfileAdapter(this, profileList, this); // Pass the click listener
        profilesRecyclerView.setAdapter(profileAdapter);
    }

    private void loadProfiles() {
        db.collection("profiles").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle the error
                    return;
                }

                profileList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Profile profile = document.toObject(Profile.class);
                    profileList.add(profile);
                }
                profileAdapter.notifyDataSetChanged();
            }
        });
    }
}
