package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private User currentUser;

    private FirebaseFirestore db;

    private CollectionReference eventsRef;

    private ArrayList<User> attendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        System.out.print("reached this point");

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        name = findViewById(R.id.eventTitle);
        date = findViewById(R.id.eventDate);
        description = findViewById(R.id.eventDescription);
        currentUser = new User("YourName");

        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("event");

        attendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        announcementRecyclerView = findViewById(R.id.announcementsRecyclerView);
        announcementRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendees = event.getSignedAttendees();
        if (attendees == null) {
            attendees = new ArrayList<>();
        }
        attendeeAdapter = new AttendeeAdapter(attendees, event);
        attendeesRecyclerView.setAdapter(attendeeAdapter);

        List<Announcement> announcements = event.getAnnouncements();
        if (announcements == null) {
            announcements = new ArrayList<>();
        }
        announcementAdapter = new AnnouncementAdapter(announcements);
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
    public void displayInfo() {
        name.setText(event.getTitle());
        // convert date to string
        date.setText(event.getDate().toString());
        description.setText(event.getDescription());
    }
    private void signUpForEvent() {
        if (currentUser != null) {
            // Successfully updated attendee count in the database
            currentUser.signUpForEvent(event);
            Toast.makeText(EventDetailsActivity.this, "Signed up for event", Toast.LENGTH_SHORT).show();
            attendees.add(currentUser);
            event.setSignedAttendees(attendees);
            attendeeAdapter.notifyDataSetChanged();
        }
    }

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
