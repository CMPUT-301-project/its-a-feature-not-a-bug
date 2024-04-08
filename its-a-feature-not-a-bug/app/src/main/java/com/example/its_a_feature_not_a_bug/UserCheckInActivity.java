package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserCheckInActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    private static final int REQUEST_CODE_LOCATION = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private CollectionReference usersRef;

    private CollectionReference locationsRef;


    private String androidId;

    private User currentUser;
    private Event checkInEvent;

    private ImageView eventImage;
    private TextView eventTitle;
    private TextView eventHost;
    private TextView eventDate;
    private TextView eventDescription;
    private Button declineButton;
    private Button confirmButton;

    private Switch geolocationSwitch;
    private boolean blockLocation = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout views
        setContentView(R.layout.activity_user_checkin);
        eventImage = findViewById(R.id.image_view_event_poster);
        eventTitle = findViewById(R.id.text_view_event_name);
        eventHost = findViewById(R.id.text_view_event_host);
        eventDate = findViewById(R.id.text_view_event_date);
        eventDescription = findViewById(R.id.text_view_event_description);
        declineButton = findViewById(R.id.button_deny_checkin);
        confirmButton = findViewById(R.id.button_confirm_checkin);
        geolocationSwitch = findViewById(R.id.switchGeolocation);

        // fetch event from intent extras
        Intent intent = getIntent();
        checkInEvent = (Event) intent.getSerializableExtra("event");

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "Check-in" + "</b></font>"));

        }

        // fill event info views
        populateEventData();

        // connect to database and collections
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        eventsRef = db.collection("events");
        usersRef = db.collection("users");
        locationsRef = db.collection("events")
                .document(checkInEvent.getTitle()) // Assuming the event title is unique
                .collection("Attendee Locations");

        // fetch current user build number
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // fetch current user data from firestore
        usersRef.document(androidId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUser = documentSnapshot.toObject(User.class);
                        Log.d("Firestore", "Fetched user data");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Failed to fetch user data");
                    }
                });

        // allow user to exit
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // confirm user check-in
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInUser();
                if (!blockLocation) {
                    storeUserLocation();
                }
            }
        });

        // Set switch initial state
        geolocationSwitch.setChecked(!blockLocation);
        geolocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the value of blockLocation based on the switch state
                blockLocation = !isChecked;
            }
        });
    }

    // update data or exit activity depending on user response
    public void checkInUser() {
        Map<String, Object> data = new HashMap<>();

        // if user not in signed users, add user to signed users
        ArrayList<String> signedAttendees = checkInEvent.getSignedAttendees();
        if (signedAttendees == null) {
            signedAttendees = new ArrayList<>();
        }
        if (!signedAttendees.contains(currentUser.getUserId())) {
            signedAttendees.add(currentUser.getUserId());
            data.put("signedAttendees", signedAttendees);
        }

        // update the map of check-ins and counts
        ArrayList<String> checkedAttendees = checkInEvent.getCheckedInAttendees();
        if (checkedAttendees == null) {
            checkedAttendees = new ArrayList<>();
        }
        if (!checkedAttendees.contains(currentUser.getUserId())) {
            checkedAttendees.add(currentUser.getUserId());
        }
        data.put("checkedInAttendees", checkedAttendees);

        eventsRef.document(checkInEvent.getTitle()).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Added user to event lists");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to add user to event lists", e);
                        finish();
                    }
                });
    }

    public void populateEventData() {
        if (checkInEvent.getImageId() != null) {
            Glide.with(this)
                    .load(checkInEvent.getImageId())
                    .centerCrop()
                    .into(eventImage);
        }
        if (checkInEvent.getTitle() != null && !checkInEvent.getTitle().isEmpty()) {
            eventTitle.setText(checkInEvent.getTitle());
        }
        if (checkInEvent.getHost() != null && !checkInEvent.getHost().isEmpty()) {
            eventHost.setText(checkInEvent.getHost());
        }
        if (checkInEvent.getDate() != null && !checkInEvent.getDate().toString().isEmpty()) {
            eventDate.setText(checkInEvent.getDate().toString());
        }
        if (checkInEvent.getDescription() != null && !checkInEvent.getDescription().isEmpty()) {
            eventDescription.setText(checkInEvent.getDescription());
        }
    }
    public void storeUserLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Map<String, Object> locationData = new HashMap<>();
                                locationData.put("latitude", location.getLatitude());
                                locationData.put("longitude", location.getLongitude());

                                // Store the user's location data in the AttendeeLocations subcollection
                                locationsRef.document(androidId)
                                        .set(locationData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(UserCheckInActivity.this, "Location stored successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UserCheckInActivity.this, "Failed to store location", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(UserCheckInActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }
    }
}
