package com.example.its_a_feature_not_a_bug;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowseEventsActivity extends AppCompatActivity implements AddEventDialogueListener{
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;
    private FloatingActionButton fab;


    @Override
    public void addEvent(Event event) {
        // Adds event to the Firestore collection

        Map<String, Object> data = new HashMap<>();
        data.put("Host", event.getHost());
        data.put("Date", event.getDate());
        data.put("Description", event.getDescription());
        data.put("Poster", event.getImageId());
        data.put("AttendeeCount", event.getAttendeeCount());

        // Include attendee limit if available
        if (event.getAttendeeLimit() > 0) {
            data.put("AttendeeLimit", event.getAttendeeLimit());
        }

        eventsRef.document(event.getTitle())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "DocumentSnapshot successfully written");
                    }
                });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // connect to database
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        // listview
        eventList = findViewById(R.id.list_view_events_list);
        eventDataList = new ArrayList<>();


        // adapter
        eventAdapter = new EventAdapter(this, eventDataList);
        eventList.setAdapter(eventAdapter);
        // add on click listener to click event
        eventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = eventDataList.get(position);
            Intent intent = new Intent(this, EventDetailsActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });

        // fab
        fab = findViewById(R.id.fab_add_event);
        fab.setOnClickListener(v -> {
            new AddEventFragment().show(getSupportFragmentManager(), "Add Event");
        });
        FloatingActionButton fabUpdateProfile = findViewById(R.id.fab_update_profile);
        fabUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BrowseEventsActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
            }
        });


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
                        String eventId = doc.getId();
                        String host = doc.getString("Host");
                        Date date = doc.getDate("Date");
                        String description = doc.getString("Description");
                        int attendeeLimit = doc.contains("AttendeeLimit") ? doc.getLong("AttendeeLimit").intValue() : 0;
                        int attendeeCount = doc.contains("AttendeeCount") ? doc.getLong("AttendeeCount").intValue() : 0;

                        List<String> attendees = new ArrayList<>();

                        DocumentReference eventIdRef = eventsRef.document(eventId); // Assuming eventId is the ID of the event
                        eventIdRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    ArrayList<Object> attendeesList = (ArrayList<Object>) document.get("signedAttendees");
                                    System.out.print(attendeesList);
//                                    if (signedAttendees != null) {
//                                        for (Map<String, Object> attendeeData : signedAttendees) {
//                                            String attendeeName = (String) attendeeData.get("signedAttendees");
//                                            attendees.add(attendeeName);
//                                        }
//                                    }
                                }
                            }
                        });

//                        ArrayList<User> signedAttendees = new ArrayList<>();
//                        if (attendees.size() > 0){
//                            for (String attendee : attendees){
//                                User user = new User(attendee);
//                                signedAttendees.add(user);
//                            }
//                        }

                        String imageUriString = doc.getString("Poster");
                        Uri imageUri = null;
                        if (imageUriString != null && !imageUriString.isEmpty()) {
                            imageUri = Uri.parse(imageUriString);
                        }

                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventId, host));
                        Event event;
                        if (attendeeLimit > 0) {
                            event = new Event(imageUri, eventId, host, date, description, attendeeLimit);
                        } else {
                            event = new Event(imageUri, eventId, host, date, description);
                        }
//                        event.setSignedAttendees(signedAttendees);
                        event.setAttendeeCount(attendeeCount);
                        eventDataList.add(event);
                    }
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}