package com.example.its_a_feature_not_a_bug;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;

/**
 * This class is an Activity that allows an admin to navigate listed events
 */
public class AdminBrowseEventsActivity extends AppCompatActivity {
    // Firebase attributes
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    // View attributes
    private ListView eventList;

    // Adapter attributes
    private EventAdapter eventAdapter;

    // Local device attributes
    private String androidId;

    // Other attributes
    ActivityResultLauncher<ScanOptions> barLauncher;
    private ArrayList<Event> eventDataList;
    private ArrayList<String> signedAttendees = new ArrayList<>();
    private ArrayList<String> attendees = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_events); // Ensure this layout exists without the buttons

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EVENTS" + "</b></font>"));

        }

        // connect to database
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        // Setup ListView
        eventList = findViewById(R.id.list_view_events_list);
        eventDataList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventDataList);
        eventList.setAdapter(eventAdapter);

        // Handle event item clicks
        eventList.setOnItemClickListener((parent, view, position, id) -> {
            Event event = eventDataList.get(position); // Assuming eventDataList is your ArrayList<Event>

            // Log the event being sent for debugging purposes
            Log.d("IntentDebug", "Sending event: " + event.toString());

            Intent intent = new Intent(AdminBrowseEventsActivity.this, AdminEventDetailsActivity.class);
            intent.putExtra("event", event); // Passing the Event object to the next activity
            startActivity(intent);
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
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        Event event = doc.toObject(Event.class);
                        event.setTitle(doc.getId());
                        Log.d("Firestore", String.format("Event(%s, %s) fetched", event.getTitle(), event.getHost()));
                        eventDataList.add(event);
                    }
                    eventAdapter.notifyDataSetChanged();
                    Log.d("", "ATTENDEE LIST " + attendees);
                }
            }
        });
    }

    /**
     * This implements the back button functionality for the action bar.
     * @param item The menu item that was selected
     * @return whether the back button was selected
     */
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}