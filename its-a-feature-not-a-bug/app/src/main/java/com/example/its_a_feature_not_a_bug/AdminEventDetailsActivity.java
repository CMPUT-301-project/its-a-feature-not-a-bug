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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdminEventDetailsActivity extends AppCompatActivity {
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

    private FirebaseStorage storage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_details);
        eventPoster = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
        eventHost = findViewById(R.id.eventHost);
        eventDate = findViewById(R.id.eventDate);
        eventDescription = findViewById(R.id.eventDescription);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EVENT DETAILS" + "</b></font>"));

        }

        ImageView deleteEventButton = findViewById(R.id.deleteEventButton);
        deleteEventButton.setOnClickListener(v -> showDeleteEventOptionsDialog());

        // System.out.print("reached this point");
        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Intent intent = getIntent();
        currentEvent = (Event) getIntent().getSerializableExtra("event");
        if (currentEvent != null) {
            displayInfo();
        }


//        if (currentEvent != null) {
//            Log.d("IntentDebug", "Received event: " + currentEvent.toString());
//            displayInfo();
//        } else {
//            Log.d("IntentDebug", "Received null event");
//            // Optionally, handle the case where the event is null, e.g., by closing the activity or showing an error message
//        }

        // get user data from database
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (androidId.equals(document.getId())) {
                            currentUser = document.toObject(User.class);
                            if (!currentUser.getUserId().equals(currentEvent.getHost())) {
//                                organizerMenuButton.setVisibility(View.GONE);
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

    private void showDeleteEventOptionsDialog() {
        final CharSequence[] options = {"Event Poster", "Event"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What would you like to delete?");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                showConfirmDeleteEventPosterDialog();
            } else if (which == 1) {
                showConfirmDeleteEventDialog();
            }
        });
        builder.show();
    }

    private void showConfirmDeleteEventPosterDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event Poster")
                .setMessage("Are you sure you want to delete this event poster?")
                .setPositiveButton("OK", (dialog, which) -> removeEventPoster())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showConfirmDeleteEventDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("OK", (dialog, which) -> deleteEvent())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void removeEventPoster() {
        ImageView eventPosterImageView = findViewById(R.id.eventImage);
        eventPosterImageView.setImageResource(R.drawable.default_poster);

        // Remove the profile picture from Firebase Storage
        if (currentEvent.getImageId() != null && !currentEvent.getImageId().isEmpty()) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentEvent.getImageId());
            photoRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("AdminEventDetailsActivity", "Event poster deleted successfully.");
                // Update the event details in Firestore to reflect the removal of the event poster.
                db.collection("events").document(currentEvent.getTitle())
                        .update("imageId", null)
                        .addOnSuccessListener(aVoid1 -> Log.d("AdminEventDetailsActivity", "Event details updated."))
                        .addOnFailureListener(e -> Log.e("AdminEventDetailsActivity", "Error updating event details.", e));
            }).addOnFailureListener(e -> Log.e("AdminEventDetailsActivity", "Error deleting event poster.", e));
        }
    }


    /**
     * This deletes the current event.
     */
    private void deleteEvent() {
        // delete image if event has one
        if (currentEvent.getImageId() != null) {
            String imageToDelete = currentEvent.getImageId();
            storageRef = storage.getReferenceFromUrl(imageToDelete);
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("Firestore", "Image Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Firestore", "Error deleting image", e);
                }
            });
        }

        // delete database entry
        eventsRef.document(currentEvent.getTitle()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AdminEventDetailsActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminEventDetailsActivity.this, "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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
                            Log.d("Testing", user.getFullName());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
