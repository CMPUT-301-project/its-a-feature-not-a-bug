package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserCheckInActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;


    private String androidId;

    private UserRefactored currentUser;
    private Event checkInEvent;

    private ImageView eventImage;
    private TextView eventTitle;
    private TextView eventHost;
    private TextView eventDate;
    private TextView eventDescription;
    private Button declineButton;
    private Button confirmButton;

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

        // fetch event from intent extras
        Intent intent = getIntent();
        checkInEvent = (Event) intent.getSerializableExtra("event");

        // fill event info views
        populateEventData();

        // connect to database and collections
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");

        // fetch current user build number
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // fetch current user data from firestore
        usersRef.document(androidId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentUser = documentSnapshot.toObject(UserRefactored.class);
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
            }
        });
    }

    // update data or exit activity depending on user response
    public void checkInUser() {
        Map<String, Object> data = new HashMap<>();

        // TODO: update event lists
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
        Map<String, Integer> checkedAttendees = checkInEvent.getCheckedAttendees();
        if (checkedAttendees == null) {
            checkedAttendees = new HashMap<>();
        }
        if (!checkedAttendees.containsKey(currentUser.getUserId())) {
            checkedAttendees.put(currentUser.getUserId(), 1);
        } else {
            checkedAttendees.compute(currentUser.getUserId(), (key, value) -> (value == null) ? 1 : value + 1);
        }
        data.put("checkedAttendees", checkedAttendees);

        // TODO: update database (finish activity in OnCompleteListener)
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
}
