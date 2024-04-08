package com.example.its_a_feature_not_a_bug;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class AttendeesActivity extends AppCompatActivity {
    // Firebase attributes
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    // View attributes
    private TextView signedAttendeesHeader;
    private TextView checkedAttendeesHeader;
    private RecyclerView signedAttendeesRecyclerView;
    private RecyclerView checkedAttendeesRecyclerView;

    // Adapter attributes
    private AttendeeAdapter signedAttendeesAdapter;
    private AttendeeAdapter checkedAttendeesAdapter;
    private ArrayList<User> signedAttendees;
    private ArrayList<User> checkedAttendees;

    // Current attributes
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch intent extras
        currentEvent = (Event) getIntent().getSerializableExtra("event");

        // Set views
        setContentView(R.layout.activity_attendees);
        signedAttendeesHeader = findViewById(R.id.text_view_signed_attendees_header);
        checkedAttendeesHeader = findViewById(R.id.text_view_checked_attendees_header);
        signedAttendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        checkedAttendeesRecyclerView = findViewById(R.id.recycler_view_checked_attendees);

        // Set action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "ATTENDEES" + "</b></font>"));

        }

        // connect to Firebase
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        // Set adapters
        signedAttendees = new ArrayList<>();
        if (currentEvent.getNumberSignedAttendees() > 0) {
            populateSignedAttendees();
        }
        signedAttendeesAdapter = new AttendeeAdapter(signedAttendees, currentEvent);
        signedAttendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        signedAttendeesRecyclerView.setAdapter(signedAttendeesAdapter);

        checkedAttendees = new ArrayList<>();
        Log.d("Brayden", currentEvent.getNumberCheckIns() + " check-ins");
        if (currentEvent.getNumberCheckIns() > 0) {
            populateCheckedAttendees();
        }
        checkedAttendeesAdapter = new AttendeeAdapter(checkedAttendees, currentEvent);
        checkedAttendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkedAttendeesRecyclerView.setAdapter(checkedAttendeesAdapter);

        // update headers with numbers of attendees
        signedAttendeesHeader.setText(currentEvent.getNumberSignedAttendees() + " Signed Attendees");
        checkedAttendeesHeader.setText(currentEvent.getNumberCheckIns() + " Checked-in Attendees");
    }

    private void populateSignedAttendees() {
        ArrayList<String> attendeesData = currentEvent.getSignedAttendees();
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (attendeesData.contains(user.getUserId())) {
                            signedAttendees.add(user);
                        }
                    }
                    signedAttendeesAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void populateCheckedAttendees() {
        ArrayList<String> attendeesData = currentEvent.getCheckedInAttendees();
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (attendeesData.contains(user.getUserId())) {
                            checkedAttendees.add(user);
                            Log.d("Brayden", user.getUserId());
                        }
                    }
                    checkedAttendeesAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}