package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HandleDeepLinkActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Event event; // the event that the deep link goes to

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Brayden", "entered deep link handler");

        // connect to database and create collection reference
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        // get event id from intent data
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String eventId = data.getLastPathSegment();

            // Use the eventId to navigate to the corresponding event page
            eventsRef.document(eventId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("Brayden", "DocumentSnapshot data: " + document.getData());
                            event = document.toObject(Event.class);
                            Intent intent = new Intent(HandleDeepLinkActivity.this, EventDetailsActivity.class);
                            intent.putExtra("event", event);
                            startActivity(intent);
                        } else {
                            Log.d("Brayden", "No such document");
                        }
                    } else {
                        Log.d("Brayden", "get failed with ", task.getException());
                    }
                }
            });
        } else {
            finish();
        }

        // If user enters back button while on event details, it will bring them here
        // So this is required to send them back to events browsing.
        finish();
    }
}
