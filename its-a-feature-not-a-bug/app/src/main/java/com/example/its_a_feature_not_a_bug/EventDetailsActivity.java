// This source code file implements the functionality for a user to view event details.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Array;
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
    private ImageView eventPoster;
    private RecyclerView attendeesRecyclerView;
    private AttendeeAdapter attendeeAdapter;
    private RecyclerView announcementRecyclerView;
    private AnnouncementAdapter announcementAdapter;

    private Button signUpButton;

    private Button removeEventButton;
    private Button sendNotificationButton;

    private UserRefactored currentUser;

    private FirebaseFirestore db;

    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private StorageReference storageRef;

    private ArrayList<UserRefactored> attendees;

    private ImageView qrCodeImageView;
    private String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EVENT DETAILS" + "</b></font>"));

        }

        // System.out.print("reached this point");
        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Intent intent = getIntent();
        event = (Event) intent.getSerializableExtra("event");
        if (event.getTitle() != null) {
            Log.d("Brayden", event.getTitle());
        }

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");

        eventPoster = findViewById(R.id.eventImage);
        name = findViewById(R.id.eventTitle);
        date = findViewById(R.id.eventDate);
        description = findViewById(R.id.eventDescription);

        // get user data from database
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (androidId.equals(document.getId())) {
                            currentUser = document.toObject(UserRefactored.class);
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
        if (event.getSignedAttendees() != null) {
            populateSignedAttendees();
        }

        sendNotificationButton = findViewById(R.id.button_send_notification);
        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNotification();
            }
        });

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

        // Initialize the ImageView and Button for QR code
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        // Initialize and set OnClickListener for the Show QR Code button
        Button btnShowQRCode = findViewById(R.id.btnShowQRCode);
        btnShowQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qrCodeImageView.getVisibility() == View.GONE) {
                    // Generate and show the QR code if it's not already visible
                    Bitmap qrCodeBitmap = QRCodeGenerator.generatePromotionalQRCode(event, 200); // Adjust size as needed
                    qrCodeImageView.setImageBitmap(qrCodeBitmap);
                    qrCodeImageView.setVisibility(View.VISIBLE);
                } else {
                    // Hide the QR code if it's already visible
                    qrCodeImageView.setVisibility(View.GONE);
                }
            }
        });


        signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpForEvent();
            }
        });

        displayInfo();

        if (event.getImageId() != null) {
            Glide.with(this)
                    .load(event.getImageId())
                    .centerCrop()
                    .into(eventPoster);
        } else {
            // Set a placeholder image if no image is available
            eventPoster.setImageResource(R.drawable.default_poster);
        }

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
                ArrayList<String> formattedAttendees = new ArrayList<>();
                for (UserRefactored user : attendees) {
                    formattedAttendees.add(user.getFullName());
                }
                event.setSignedAttendees(formattedAttendees);

                // Extract names from the attendees list
                ArrayList<String> attendeeNames = new ArrayList<>();
                for (UserRefactored user : attendees) {
                    attendeeNames.add(user.getFullName());
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
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (eventToDelete.getImageId() != null) {
            storageRef = storage.getReferenceFromUrl(eventToDelete.getImageId());
            storageRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Firestore", "Image Deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Firestore", "Error deleting image", exception);
                        }
                    });
        }

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

        public void makeNotification() {
            String channelId = "CHANNEL_ID_NOTIFICATION";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            builder.setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle("Notification Title")
                    .setContentText("Some text for notification here")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra("data", "Some value to be passed here");

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);

                if (notificationChannel == null) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    notificationChannel = new NotificationChannel(channelId, "Some description", importance);
                    notificationChannel.setLightColor(Color.GREEN);
                    notificationChannel.enableVibration(true);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }

            notificationManager.notify(0, builder.build());
        }

        public void populateSignedAttendees() {
            ArrayList<String> attendeesData = event.getSignedAttendees();
            usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserRefactored user = document.toObject(UserRefactored.class);
                            if (attendeesData.contains(document.getId())) {
                                attendees.add(user);
                            }
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
}
