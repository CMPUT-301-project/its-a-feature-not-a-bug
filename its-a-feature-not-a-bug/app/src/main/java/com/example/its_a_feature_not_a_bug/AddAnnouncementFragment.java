package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddAnnouncementFragment extends DialogFragment {
    // Firebase attributes
    private FirebaseFirestore db;
    private CollectionReference announcementsRef;
    private CollectionReference eventsRef;

    // View attributes
    private EditText editTitle;
    private EditText editBody;

    // Current attributes
    private Event currentEvent;

    // Constructors

    public AddAnnouncementFragment (Event event) {
        this.currentEvent = event;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Set views
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_announcement, null);
        editTitle = view.findViewById(R.id.edit_text_announcement_title);
        editBody = view.findViewById(R.id.edit_text_announcement_body);

        // Connect to Firebase
        db = FirebaseFirestore.getInstance();
        announcementsRef = db.collection("announcements");
        eventsRef = db.collection("events");

        // Create Dialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder
                .setView(view)
                .setTitle("Add Announcement")
                .setNegativeButton("cancel", null)
                .setPositiveButton("ok", null)
                .create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String announcementId = UUID.randomUUID().toString();
                String eventId = currentEvent.getTitle();
                String announcementTitle = editTitle.getText().toString();
                String announcementBody = editBody.getText().toString();

                Announcement announcement = new Announcement(announcementId, eventId, announcementTitle, announcementBody);
                addAnnouncement(announcement);
                alertDialog.dismiss();
            });
        });

        return alertDialog;
    }

    private void addAnnouncement(Announcement announcement) {
        // set announcement data
        Map<String, Object> announcementData = new HashMap<>();
        announcementData.put("announcementId", announcement.getAnnouncementId());
        announcementData.put("eventId", announcement.getEventId());
        announcementData.put("title", announcement.getTitle());
        announcementData.put("description", announcement.getDescription());

        // add announcement to Firebase
        announcementsRef.document(announcement.getAnnouncementId()).set(announcementData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "DocumentSnapshot successfully written");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to upload announcement", e);
                    }
                });

        // set event data
        currentEvent.addAnnouncement(announcement.getAnnouncementId());
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("announcements", currentEvent.getAnnouncements());

        // update event
        eventsRef.document(currentEvent.getTitle()).update(eventData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Firestore", "DocumentSnapshot successfully written");
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to upload event", e);
                    }
                });
    }
}
