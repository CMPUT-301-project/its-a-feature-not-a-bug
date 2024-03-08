package com.example.its_a_feature_not_a_bug;

import android.os.Bundle;
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
 * This activity extends BrowseEventsActivity to inherit its basic functionalities.
 *
 * <p>In this activity, the FloatingActionButton for adding new events is hidden,
 * as administrators won't be adding events from this screen.</p>
 *
 * <p>Additional customizations needed for the admin view, such as removing events,
 * can be implemented in the onCreate method.</p>
 */
public class AdminBrowseProfilesActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView profilesRecyclerView;
    private ProfileAdapter profileAdapter;
    private List<Profile> profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profiles);

        // Connect to database
        db = FirebaseFirestore.getInstance();
        profilesRecyclerView = findViewById(R.id.profilesRecyclerView);
        profileList = new ArrayList<>();

        setupRecyclerView();
        loadProfiles();
    }

    private void setupRecyclerView() {
        profilesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileAdapter = new ProfileAdapter(this, profileList);
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


