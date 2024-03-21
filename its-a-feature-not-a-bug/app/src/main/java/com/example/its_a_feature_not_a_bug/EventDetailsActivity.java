// This source code file implements the functionality for a user to view event details.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An activity that allows users to view the details of an event.
 * This activity extends AppCompatActivity to inherit its basic functionalities.
 */
public class EventDetailsActivity extends AppCompatActivity {
    private Event event;
    private TextView name;
    private TextView date;
    private TextView location;
    private TextView description;
    private ImageView qrCode;
    private RecyclerView attendeesRecyclerView;
    private AttendeeAdapter attendeeAdapter;
    private RecyclerView announcementRecyclerView;
    private AnnouncementAdapter announcementAdapter;

    private Button signUpButton;

    private Button removeEventButton;

    private Button backButton;
    private User currentUser;

    private FirebaseFirestore db;

    private CollectionReference eventsRef;

    private ArrayList<User> attendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
//            actionBar.setTitle("EVENTS"); // Set the title for the action bar
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EVENT DETAILS" + "</b></font>"));

        }

        System.out.print("reached this point");

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        name = findViewById(R.id.eventTitle);
        date = findViewById(R.id.eventDate);
        description = findViewById(R.id.eventDescription);
        currentUser = new User("YourName");

        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("event");

        attendees = event.getSignedAttendees();
        if (attendees == null) {
            attendees = new ArrayList<>();
            attendees.add(new User("Jing"));
            attendees.add(new User("Tanveer"));
        }
        attendeeAdapter = new AttendeeAdapter(attendees, event);
        attendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeesRecyclerView.setAdapter(attendeeAdapter);

        List<Announcement> announcements = event.getAnnouncements();
        if (announcements == null) {
            announcements = new ArrayList<>();
        }
        announcementAdapter = new AnnouncementAdapter(announcements);
        announcementRecyclerView = findViewById(R.id.announcementsRecyclerView);
        announcementRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        announcementRecyclerView.setAdapter(announcementAdapter);


        signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpForEvent();
            }
        });
        displayInfo();

        removeEventButton = findViewById(R.id.btnRemoveEvent);
        removeEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { deleteEventFromDatabase(event);}
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

    /**
     * This displays the information of the event.
     */
    public void displayInfo() {
        name.setText(event.getTitle());
        // convert date to string
        date.setText(event.getDate().toString());
        description.setText(event.getDescription());
    }

    /**
     * This allows a user to sign up for an event.
     */
    private void signUpForEvent() {
        if (currentUser != null) {
            if (event.getAttendeeCount() < event.getAttendeeLimit()) {
                // Add the current user's name to the list of attendees
                attendees.add(currentUser);
                // Increment the attendee count
                event.setAttendeeCount(event.getAttendeeCount() + 1);
                // Set the updated list of attendees to the event
                event.setSignedAttendees(attendees);

                // Extract names from the attendees list
                ArrayList<String> attendeeNames = new ArrayList<>();
                for (User user : attendees) {
                    attendeeNames.add(user.getName());
                }

                // Update the Firestore document for the event with the names of attendees and attendee count
                eventsRef.document(event.getTitle())
                        .update("signedAttendees", attendeeNames, "attendeeCount", event.getAttendeeCount())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                currentUser.signUpForEvent(event);

                                // Successfully updated the list of attendees and attendee count in the database
                                Toast.makeText(EventDetailsActivity.this, "Signed up for event", Toast.LENGTH_SHORT).show();
                                // Notify the adapter of the data change
                                attendeeAdapter.notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to update the list of attendees and attendee count in the database
                                Toast.makeText(EventDetailsActivity.this, "Failed to sign up for event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Attendee limit reached
                Toast.makeText(EventDetailsActivity.this, "Attendee limit reached for this event", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * This deletes a selected event from the database.
     * @param eventToDelete the event to be deleted
     */
    private void deleteEventFromDatabase (Event eventToDelete){
            eventsRef.document(eventToDelete.getTitle())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Successfully deleted the event
                            Toast.makeText(EventDetailsActivity.this, "Event removed successfully", Toast.LENGTH_SHORT).show();
                            // Finish the activity
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete the event
                            Toast.makeText(EventDetailsActivity.this, "Failed to remove event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
}
