// This source code file implements the functionality for a user to browse and event page.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.content.Intent;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is an Activity that allows a user to navigate listed events
 */
public class BrowseEventsActivity extends AppCompatActivity implements AddEventDialogueListener{
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;
    private FloatingActionButton fab;
    private Button cameraButton;
    ActivityResultLauncher<ScanOptions> barLauncher;
    private ArrayList<String> signedAttendees = new ArrayList<>();

    private Button myEventButton;
    private String androidId;


    private ArrayList<String> attendees = new ArrayList<String>();

    /**
     * This method adds an event to the Firebase Firestore
     * @param event the event to be added
     */
    @Override
    public void addEvent(Event event) {
        // Adds event to the Firestore collection

        Map<String, Object> data = new HashMap<>();
        data.put("host", event.getHost());
        data.put("date", event.getDate());
        data.put("description", event.getDescription());
        data.put("imageId", event.getImageId());
        data.put("attendeeCount", event.getAttendeeCount());

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
//            actionBar.setTitle("EVENTS"); // Set the title for the action bar
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EVENTS" + "</b></font>"));

        }

        // connect to database
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        // listview
        eventList = findViewById(R.id.list_view_events_list);
        eventDataList = new ArrayList<>();


        cameraButton = findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanOptions options = new ScanOptions();
                options.setOrientationLocked(true);
                options.setCaptureActivity(QRCodeScanner.class);
                barLauncher.launch(options);
            }
        });


        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                // Parse the URI
                Uri uri = Uri.parse(result.getContents());

                // Extract the handlerType from the URI
                String handlerType = uri.getHost();

                // Handle the QR code
                Class<?> targetActivityClass;
                if ("promotional".equals(handlerType)) {
                    targetActivityClass = HandlePromotionalQRActivity.class;
                } else if ("checkin".equals(handlerType)) {
                    targetActivityClass = HandleCheckInQRActivity.class;
                } else {
                    targetActivityClass = BrowseEventsActivity.class;
                }


                // Start the appropriate activity with the parsed URI
                Intent intent = new Intent(this, targetActivityClass);
                intent.setData(uri);
                startActivity(intent);

//                Intent intent = new Intent(this, HandlePromotionalQRActivity.class);
//                Uri uri = Uri.parse(result.getContents());
//                intent.setData(uri);
//                startActivity(intent);
            }
        });

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
        // add on click listener to click event
//        Button eventsButton = findViewById(R.id.button_events); // Ensure this ID matches your layout
//        eventsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Intent to start AttendeesActivity
//                Intent intent = new Intent(BrowseEventsActivity.this, AttendeesActivity.class);
//                intent.putExtra("attendees", attendees);
//                //intent.putExtra("attendees", signedAttendees);
//                startActivity(intent);
//            }
//        });


        // fab
        fab = findViewById(R.id.fab_add_event);
        fab.setOnClickListener(v -> {
            new AddEventFragment().show(getSupportFragmentManager(), "Add Event");
        });
        Button profileButton = findViewById(R.id.button_profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
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
                        String host = doc.getString("host");
                        Date date = doc.getDate("date");
                        String description = doc.getString("description");

                        Long attendeeLimitLong = doc.getLong("attendeeLimit");
                        int attendeeLimit = attendeeLimitLong != null ? attendeeLimitLong.intValue() : 0;

                        Long attendeeCountLong = doc.getLong("attendeeCount");
                        int attendeeCount = attendeeCountLong != null ? attendeeCountLong.intValue() : 0;




                        //int attendeeLimit = doc.contains("attendeeLimit") ? doc.getLong("attendeeLimit").intValue() : 0;
                        //int attendeeCount = doc.contains("attendeeCount") ? doc.getLong("attendeeCount").intValue() : 0;



                        if (doc.get("signedAttendees") != null) {
                            attendees = (ArrayList<String>) doc.get("signedAttendees");
                        }



                        String imageURLString = doc.getString("imageId");

                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventId, host));

                        Event event;
                        if (attendeeLimit > 0) {
                            event = new Event(imageURLString, eventId, host, date, description, attendeeLimit);
                        } else {
                            event = new Event(imageURLString, eventId, host, date, description);
                        }
                        event.setAttendeeCount(attendeeCount);
                        event.setSignedAttendees(signedAttendees);
                        eventDataList.add(event);
                    }


                    if (attendees.size() >  0) {
                        for (String attendee : attendees) {
                            signedAttendees.add(attendee);
                        }
                    }


                    eventAdapter.notifyDataSetChanged();
                    Log.d("", "ATTENDEE LIST "  + attendees);

                }
            }
        });


        myEventButton = findViewById(R.id.button_events); // Ensure this ID matches your layout
        myEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                ArrayList<Event> myEventsList = getMyEvents(androidId, eventDataList);
                Intent intent = new Intent(BrowseEventsActivity.this, MyEventsActivity.class);
                intent.putExtra("myEventsList", myEventsList);
                startActivity(intent);
            }
        });
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

    public ArrayList<Event> getMyEvents(String androidId, ArrayList <Event> eventDataList) {
        ArrayList<Event> myEvents = new ArrayList<>();
        for (Event event : eventDataList) {
            if (event.getSignedAttendees() != null) {
                for (String attendeeId : event.getSignedAttendees()) {
                    if (attendeeId.equalsIgnoreCase(androidId)) {
                        myEvents.add(event);
                    }
                }
            }
        }
        return myEvents;
    }
}