package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

/**
 * This class is the activity that displays the attendees for an event.
 */
public class OrganizerEventDetailsActivity extends AppCompatActivity {
    // Firebase attributes
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private CollectionReference announcementsRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // View attributes
    private Button attendeesButton;
    private Button mapButton;
    private Button qrCodeButton;
    private ImageView deleteEventButton;
    private ImageView editEventButton;
    private ImageView eventPoster;
    private TextView eventTitle;
    private TextView eventHost;
    private TextView eventDate;
    private TextView eventDescription;
    private RecyclerView announcementRecyclerView;
    private ImageView qrCodeImageView;


    // Adapter attributes
    private AnnouncementAdapter announcementAdapter;
    private ArrayList<Announcement> announcements;

    // Current attributes
    private Event currentEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch event from extras
        currentEvent = (Event) getIntent().getSerializableExtra("event");

        // Set views
        setContentView(R.layout.activity_organizer_event_details);
        attendeesButton = findViewById(R.id.button_attendees);
        mapButton = findViewById(R.id.button_map);
        qrCodeButton = findViewById(R.id.button_qr_codes);
        deleteEventButton = findViewById(R.id.deleteEventButton);
        editEventButton = findViewById(R.id.editEventButton);
        eventPoster = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
        eventHost = findViewById(R.id.eventHost);
        eventDate = findViewById(R.id.eventDate);
        eventDescription = findViewById(R.id.eventDescription);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        // Set action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EVENT DETAILS" + "</b></font>"));

        }

        // connect to Firebase
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");
        announcementsRef = db.collection("announcements");
        storage = FirebaseStorage.getInstance();

        // Set adapters and populate relevant data
        announcements = new ArrayList<>();
        if (currentEvent.getAnnouncements() != null && !currentEvent.getAnnouncements().isEmpty()) {
            populateAnnouncements();
        }
        announcementAdapter = new AnnouncementAdapter(announcements);
        announcementRecyclerView = findViewById(R.id.announcementsRecyclerView);
        announcementRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        announcementRecyclerView.setAdapter(announcementAdapter);

        // Populate event views
        populateViews();

        // Set click listeners
        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editEventIntent = new Intent(OrganizerEventDetailsActivity.this, EditEventActivity.class);
                editEventIntent.putExtra("event", currentEvent);
                startActivity(editEventIntent);
            }
        });

        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent();
            }
        });

        attendeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showAttendeesIntent = new Intent(OrganizerEventDetailsActivity.this, AttendeesActivity.class);
                showAttendeesIntent.putExtra("event", currentEvent);
                startActivity(showAttendeesIntent);
            }
        });

        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQROptionsDialog();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapEventIntent = new Intent(OrganizerEventDetailsActivity.this, OrganizerMapActivity.class);
                mapEventIntent.putExtra("event", currentEvent);
                startActivity(mapEventIntent);
            }
        });
    }

    /**
     * This populates the announcements view with relevant data.
     */
    private void populateAnnouncements() {
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

    /**
     * This populates the views with relevant event data.
     */
    private void populateViews() {
        if (currentEvent.getImageId() != null) {
            Glide.with(this)
                    .load(currentEvent.getImageId())
                    .centerCrop()
                    .into(eventPoster);
        } else {
            eventPoster.setImageResource(R.drawable.default_poster);
        }

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
                Toast.makeText(OrganizerEventDetailsActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrganizerEventDetailsActivity.this, "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
}
