package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class OrganizerMenuActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;


    private String androidId;

    private UserRefactored currentUser;
    private Event currentEvent;

    private RecyclerView checkedInAttendeesView;
    private CheckedInAttendeeAdapter attendeeAdapter;
    private ArrayList<String> checkedAttendees;
    private TextView checkedAttendeesHeader;
    private Button createAnnouncementButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set views
        setContentView(R.layout.activity_organizer_menu);
        checkedInAttendeesView = findViewById(R.id.recycler_view_checked_attendees);
        checkedAttendeesHeader = findViewById(R.id.text_view_checked_attendees_header);
        createAnnouncementButton = findViewById(R.id.button_create_announcement);

        // fetch event from intent extras
        Intent intent = getIntent();
        currentEvent = (Event) intent.getSerializableExtra("event");

        // connect to database and collections
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");

        // set adapters
        checkedAttendees = new ArrayList<>();
        Log.d("Brayden", currentEvent.getCheckedAttendees().toString());
        if (currentEvent.getCheckedAttendees() != null) {
            populateCheckedAttendees();
        }
        attendeeAdapter = new CheckedInAttendeeAdapter(checkedAttendees);
        checkedInAttendeesView = findViewById(R.id.recycler_view_checked_attendees);
        checkedInAttendeesView.setLayoutManager(new LinearLayoutManager(this));
        checkedInAttendeesView.setAdapter(attendeeAdapter);

        // fetch current user build number
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // fetch current user data from Firestore
        usersRef.document(androidId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(UserRefactored.class);
                Log.d("Firestore", "Fetched user data");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firestore", "Failed to fetch user data");
            }
        });

        // send notifications (announcements)
        createAnnouncementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreateAnnouncement.showDialog(getApplicationContext(), currentEvent);
            }
        });
    }

    // share QR code
    // geolocation map

    public void populateCheckedAttendees() {
        ArrayList<String> attendeesData = currentEvent.getCheckedAttendees();
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserRefactored user = document.toObject(UserRefactored.class);
                        if (attendeesData.contains(user.getUserId())) {
                            checkedAttendees.add(user.getUserId());
                            Log.d("Brayden", "Added: " + user.getUserId());
                        }
                    }
                    attendeeAdapter.notifyDataSetChanged();
                    Log.d("Brayden", "updated adapter");
                    // update number of check-ins
                    checkedAttendeesHeader.setText(checkedAttendees.size() + " " + checkedAttendeesHeader.getText());
                } else {
                    Log.d("Firestore", "Error getting docuemnts: ", task.getException());
                }
            }
        });
    }
}
