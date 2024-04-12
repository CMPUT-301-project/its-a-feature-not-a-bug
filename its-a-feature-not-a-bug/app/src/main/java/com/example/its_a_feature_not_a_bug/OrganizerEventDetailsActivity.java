// This source code file implements the functionality for an organizer to view the details
// of one of their events.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import static com.example.its_a_feature_not_a_bug.QRCodeGenerator.shareQRCode;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
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
    private Button newAnnouncementButton;
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

    private static final int EDIT_EVENT_REQUEST_CODE = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentEvent = (Event) getIntent().getSerializableExtra("event");

        setContentView(R.layout.activity_organizer_event_details);
        attendeesButton = findViewById(R.id.button_attendees);
        mapButton = findViewById(R.id.button_map);
        qrCodeButton = findViewById(R.id.button_qr_codes);
        editEventButton = findViewById(R.id.editEventButton);
        deleteEventButton = findViewById(R.id.deleteEventButton);
        newAnnouncementButton = findViewById(R.id.button_new_announcement);
        eventPoster = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
        eventHost = findViewById(R.id.eventHost);
        eventDate = findViewById(R.id.eventDate);
        eventDescription = findViewById(R.id.eventDescription);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        Button buttonShareQR = findViewById(R.id.button_share_qr);


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

        populateViews();

        editEventButton.setOnClickListener(v -> {
            Intent editEventIntent = new Intent(OrganizerEventDetailsActivity.this, EditEventActivity.class);
            editEventIntent.putExtra("event", currentEvent);
            startActivityForResult(editEventIntent, EDIT_EVENT_REQUEST_CODE);
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
        buttonShareQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap qrCodeBitmap = QRCodeGenerator.generatePromotionalQRCode(currentEvent, 200);
                shareQRCode(OrganizerEventDetailsActivity.this, qrCodeBitmap, "QRCode.png");
            }
        });

        newAnnouncementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddAnnouncementFragment(currentEvent).show(getSupportFragmentManager(), "Add Announcement");
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentEvent.getDate());

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String formattedTime = timeFormat.format(currentEvent.getDate());

        eventDate.setText(String.format("%s at %s", formattedDate, formattedTime));

        eventDescription.setText(currentEvent.getDescription());
    }

    /**
     * This shows the dialog that gives QR code display options.
     */
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
                Bitmap qrCodeBitmap = QRCodeGenerator.generatePromotionalQRCode(currentEvent, 200);
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
                qrCodeImageView.setVisibility(View.VISIBLE);
            } else if (which == 1) {
                Bitmap qrCodeBitmap = QRCodeGenerator.generateCheckInQRCode(currentEvent, 200);
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
                qrCodeImageView.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_EVENT_REQUEST_CODE && resultCode == RESULT_OK) {
            Event editedEvent = (Event) data.getSerializableExtra("editedEvent");
            if (editedEvent != null) {
                updateEventInFirestore(editedEvent);
            }
        }
    }

    /**
     * This updates an event in Firebase.
     * @param editedEvent the event to be updated
     */
    private void updateEventInFirestore(Event editedEvent) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(editedEvent.getTitle()).set(editedEvent)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Event updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating event.", Toast.LENGTH_SHORT).show());
    }

    /**
     * This implements the back button functionality for the action bar.
     * @param item The menu item that was selected
     * @return whether the back button was selected
     */
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
