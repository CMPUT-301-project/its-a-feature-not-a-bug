// This source code file implements the functionality for a user to view event details.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.content.Intent;

import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Locale;
import java.text.SimpleDateFormat;


/**
 * An activity that allows users to view the details of an event.
 * This activity extends AppCompatActivity to inherit its basic functionalities.
 */
public class AttendeeEventDetailsActivity extends AppCompatActivity {
    // Firebase attributes
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference announcementsRef;

    // View attributes
    private TextView eventTitle;
    private TextView eventDate;
    private TextView eventHost;
    private TextView eventDescription;
    private ImageView eventPoster;
    private RecyclerView attendeesRecyclerView;
    private RecyclerView announcementRecyclerView;
    private Button signUpButton;
    private Button organizerMenuButton;
    private ImageView qrCodeImageView;

    // Adapter attributes
    private AttendeeAdapter attendeeAdapter;
    private ArrayList<User> attendees;
    private AnnouncementAdapter announcementAdapter;
    private ArrayList<Announcement> announcements;

    // Current attributes
    private User currentUser;
    private Event currentEvent;

    // Local device attributes
    private String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        db = FirebaseFirestore.getInstance();

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EVENT DETAILS" + "</b></font>"));

        }

        ImageView deleteEventButton = findViewById(R.id.deleteEventButton);
//        deleteEventButton.setOnClickListener(v -> showDeleteEventOptionsDialog());

        // System.out.print("reached this point");
        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Intent intent = getIntent();
        currentEvent = (Event) intent.getSerializableExtra("event");

        eventsRef = db.collection("events");
        usersRef = db.collection("users");
        announcementsRef = db.collection("announcements");

        eventPoster = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
        eventHost = findViewById(R.id.eventHost);
        eventDate = findViewById(R.id.eventDate);
        eventDescription = findViewById(R.id.eventDescription);
        organizerMenuButton = findViewById(R.id.button_organizer_menu);

        // get user data from database
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (androidId.equals(document.getId())) {
                            currentUser = document.toObject(User.class);
                            if (!currentUser.getUserId().equals(currentEvent.getHost())) {
                                organizerMenuButton.setVisibility(View.GONE);
                            }
                            Log.d("Brayden", "currentUser: " + currentUser.getUserId());
                            break;
                        }
                    }
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

        // get attendees
        attendees = new ArrayList<>();
        if (currentEvent.getSignedAttendees() != null && !currentEvent.getSignedAttendees().isEmpty()) {
            Log.d("Brayden", "got here");
            populateSignedAttendees();
        }
        attendeeAdapter = new AttendeeAdapter(attendees, currentEvent);
        attendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeesRecyclerView.setAdapter(attendeeAdapter);

        organizerMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent organizerMenuIntent = new Intent(getApplicationContext(), OrganizerMenuActivity.class);
                organizerMenuIntent.putExtra("event", currentEvent);
                startActivity(organizerMenuIntent);
            }
        });

        announcements = new ArrayList<>();
        if (currentEvent.getAnnouncements() != null && !currentEvent.getAnnouncements().isEmpty()) {
            populateAnnouncements();
        }
        announcementAdapter = new AnnouncementAdapter(announcements);
        announcementRecyclerView = findViewById(R.id.announcementsRecyclerView);
        announcementRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        announcementRecyclerView.setAdapter(announcementAdapter);

        // Initialize the ImageView and Button for QR code
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        // Initialize and set OnClickListener for the Show QR Code button
        Button btnShowQRCode = findViewById(R.id.btnShowQRCode);

        btnShowQRCode.setOnClickListener(v -> showQROptionsDialog());


        signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpForEvent();
            }
        });

        displayInfo();

        if (currentEvent.getImageId() != null) {
            Glide.with(this)
                    .load(currentEvent.getImageId())
                    .centerCrop()
                    .into(eventPoster);
        } else {
            // Set a placeholder image if no image is available
            eventPoster.setImageResource(R.drawable.default_poster);
        }

    }

//    private void showDeleteEventOptionsDialog() {
//        final CharSequence[] options = {"Event Poster", "Event"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("What would you like to delete?");
//        builder.setItems(options, (dialog, which) -> {
//            if (which == 0) {
//                showConfirmDeleteEventPosterDialog();
//            } else if (which == 1) {
//                showConfirmDeleteEventDialog();
//            }
//        });
//        builder.show();
//    }
//
//    private void showConfirmDeleteEventPosterDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Delete Event Poster")
//                .setMessage("Are you sure you want to delete this event poster?")
//                .setPositiveButton("OK", (dialog, which) -> removeEventPoster())
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
//                .show();
//    }
//
//    private void showConfirmDeleteEventDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Delete Event")
//                .setMessage("Are you sure you want to delete this event?")
//                .setPositiveButton("OK", (dialog, which) -> deleteEvent())
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
//                .show();
//    }

//    private void removeEventPoster() {
//        ImageView eventPosterImageView = findViewById(R.id.eventImage);
//        eventPosterImageView.setImageResource(R.drawable.default_poster);
//
//        // Remove the profile picture from Firebase Storage
//        if (currentEvent.getImageId() != null && !currentEvent.getImageId().isEmpty()) {
//            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentEvent.getImageId());
//            photoRef.delete().addOnSuccessListener(aVoid -> {
//                Log.d("EventDetailsActivity", "Event poster deleted successfully.");
//                // Update the event details in Firestore to reflect the removal of the event poster.
//                db.collection("events").document(currentEvent.getTitle())
//                        .update("imageId", null)
//                        .addOnSuccessListener(aVoid1 -> Log.d("EventDetailsActivity", "Event details updated."))
//                        .addOnFailureListener(e -> Log.e("EventDetailsActivity", "Error updating event details.", e));
//            }).addOnFailureListener(e -> Log.e("EventDetailsActivity", "Error deleting event poster.", e));
//        }
//    }

    /**
     * This displays the information of the event.
     */
    public void displayInfo() {
        eventTitle.setText(currentEvent.getTitle());

        // fetch host name from firebase
        usersRef.document(currentEvent.getHost()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            eventHost.setText(user.getFullName());
                        } else {
                            Log.d("Firestore", "Document does not exist");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to fetch host name", e);
                    }
                });
        // Format and display the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentEvent.getDate());

        // Format and display the time
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String formattedTime = timeFormat.format(currentEvent.getDate());

        // Assuming 'date' TextView is used to show both date and time together
        // You might want to separate them or adjust according to your layout needs
        eventDate.setText(String.format("%s at %s", formattedDate, formattedTime));

        eventDescription.setText(currentEvent.getDescription());
    }

    /**
     * This allows a user to sign up for an event.
     */
    private void signUpForEvent() {
        if (currentUser != null) {
            if (currentEvent.getAttendeeLimit() == null || currentEvent.getNumberSignedAttendees() < currentEvent.getAttendeeLimit()) {
                if (!currentEvent.getSignedAttendees().contains(currentUser.getUserId())) {
                    // Add the current user's name to the list of attendees
                    attendees.add(currentUser);

                    // Set the updated list of attendees to the event
                    ArrayList<String> formattedAttendees = new ArrayList<>();
                    for (User user : attendees) {
                        formattedAttendees.add(user.getUserId());
                    }
                    currentEvent.setSignedAttendees(formattedAttendees);

                    // Update the Firestore document for the event with the names of attendees and attendee count
                    eventsRef.document(currentEvent.getTitle())
                            .update("signedAttendees", formattedAttendees)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
//                                    currentUser.signUpForEvent(event);

                                    // Successfully updated the list of attendees and attendee count in the database
                                    Toast.makeText(AttendeeEventDetailsActivity.this, "Signed up for event", Toast.LENGTH_SHORT).show();
                                    // Notify the adapter of the data change
                                    attendeeAdapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to update the list of attendees and attendee count in the database
                                    Toast.makeText(AttendeeEventDetailsActivity.this, "Failed to sign up for event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(AttendeeEventDetailsActivity.this, "Already signed up for this event", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Attendee limit reached
                Toast.makeText(AttendeeEventDetailsActivity.this, "Attendee limit reached for this event", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showQROptionsDialog() {
        // hide QR code if displayed on the screen
        if (qrCodeImageView.getVisibility() == View.VISIBLE) {
            qrCodeImageView.setVisibility(View.GONE);
            return;
        }

        final CharSequence[] options = {"Promotional QR", "Check-in QR"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Which QR Code would you like to see?");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // "Promotional QR" was clicked
                // Generate and show the QR code if it's not already visible
                Bitmap qrCodeBitmap = QRCodeGenerator.generatePromotionalQRCode(currentEvent, 200); // Adjust size as needed
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
                qrCodeImageView.setVisibility(View.VISIBLE);
            } else if (which == 1) {
                // "Check-in QR" was clicked
                // Generate and show the QR code if it's not already visible
                Bitmap qrCodeBitmap = QRCodeGenerator.generateCheckInQRCode(currentEvent, 200); // Adjust size as needed
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
                qrCodeImageView.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }

    public void populateSignedAttendees() {
        ArrayList<String> attendeesData = currentEvent.getSignedAttendees();
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (attendeesData.contains(user.getUserId())) {
                            attendees.add(user);
                        }
                    }
                    attendeeAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void populateAnnouncements() {
        ArrayList<String> announcementsData = currentEvent.getAnnouncements();
        announcementsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Announcement announcement = document.toObject(Announcement.class);
                        if (announcementsData.contains(announcement.getAnnouncementId())) {
                            announcements.add(announcement);
                        }
                    }
                    announcementAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
