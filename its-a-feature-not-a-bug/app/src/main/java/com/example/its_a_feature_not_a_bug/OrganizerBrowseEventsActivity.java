package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the activity that allows organizers to browse their own events and create new ones.
 */
public class OrganizerBrowseEventsActivity extends AppCompatActivity implements AddEventDialogueListener {
    // Firebase attributes
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    // local device attributes
    private String androidId;

    // View attributes
    private ListView eventList;
    private Button newEventButton;
    private Button profileButton;

    // Adapter attributes
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set views
        setContentView(R.layout.activity_organizer_browse_events);
        eventList = findViewById(R.id.list_view_events_list);
        newEventButton = findViewById(R.id.button_new_event);
        profileButton = findViewById(R.id.button_profile);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "MY EVENTS" + "</b></font>"));
        }

        // Fetch build number
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // connect to Firebase
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        // Set up adapters
        eventDataList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventDataList);
        eventList.setAdapter(eventAdapter);

        // Events Firebase snapshot listener
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        Event event = doc.toObject(Event.class);
                        event.setTitle(doc.getId());

                        // only add event if current user created it
                        if (event.getHost().equals(androidId)) {
                            Log.d("Firestore", String.format("Event(%s, %s) fetched", event.getTitle(), event.getHost()));
                            eventDataList.add(event);
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });

        // set up click listeners
        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddEventFragment().show(getSupportFragmentManager(), "Add Event");
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(OrganizerBrowseEventsActivity.this, UpdateProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = eventDataList.get(position);
                Intent eventDetailsIntent = new Intent(OrganizerBrowseEventsActivity.this, OrganizerEventDetailsActivity.class);
                eventDetailsIntent.putExtra("event", event);
                startActivity(eventDetailsIntent);
            }
        });
    }

    @Override
    public void addEvent(Event event) {
        // Adds event to the Firestore collection

        Map<String, Object> data = new HashMap<>();
        data.put("host", event.getHost());
        data.put("date", event.getDate());
        data.put("description", event.getDescription());
        data.put("imageId", event.getImageId());

        // Include attendee limit if available
        if (event.getAttendeeLimit() > 0) {
            data.put("attendeeLimit", event.getAttendeeLimit());
        }

        eventsRef.document(event.getTitle())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "DocumentSnapshot successfully written");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to upload event", e);
                    }
                });
    }
}
