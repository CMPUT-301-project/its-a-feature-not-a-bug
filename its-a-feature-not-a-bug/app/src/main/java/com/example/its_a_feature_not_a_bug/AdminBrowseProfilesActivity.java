package com.example.its_a_feature_not_a_bug;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.its_a_feature_not_a_bug.Profile;
import com.example.its_a_feature_not_a_bug.ProfileAdapter;
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

public class AdminBrowseProfilesActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference profilesRef;
    private ListView profilesListView; // Change RecyclerView to ListView
    private ArrayAdapter<String> profileAdapter;
    private List<Profile> profileList;

    private List<String> profileNames;

    private Button removeButton;

    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profiles);

        // Connect to database
        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("profiles");
        profilesListView = findViewById(R.id.profilesListView); // Change to ListView
        profileList = new ArrayList<>();
        profileList = loadProfiles(profileList);

        profileNames = new ArrayList<>();

        for(Profile profile: profileList){
            profileNames.add(profile.getFullName());
        }

    }

    private List<Profile> loadProfiles(List<Profile> profileList) {
        db.collection("profiles").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle the error
                    Log.e("Load Profiles", "Error fetching profiles: " + e.getMessage());
                    return;
                }

                profileList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Retrieve profile data
                    String fullName = document.getString("fullName");
                    String contactInfo = document.getString("contactInfo");
                    boolean geolocationDisabled = document.getBoolean("geolocationDisabled"); // Retrieve additional field

                    // Create a new Profile object with retrieved data
                    Profile profile = new Profile(fullName, contactInfo, geolocationDisabled); // Assuming you have a constructor for Profile class

                    // Add the profile to the list
                    profileList.add(profile);
                }
            }
        });
        return profileList;
    }
}
