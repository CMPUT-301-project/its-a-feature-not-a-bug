package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DialogCreateAnnouncement {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    public static void showDialog(Context context, Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.layout_create_announcement_dialogue, null);
        final EditText editTitle = dialogView.findViewById(R.id.edit_text_announcement_title);
        final EditText editMsg = dialogView.findViewById(R.id.edit_text_announcement_description);

        builder.setView(dialogView)
                .setTitle("Enter Text")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String anounTitle = editTitle.getText().toString();
                        String anounMsg = editMsg.getText().toString();
                        Announcement announcement = new Announcement(anounTitle, anounMsg);
                        uploadAnnouncementToFirestore(announcement, event);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void uploadAnnouncementToFirestore(Announcement announcement, Event event) {
        // create a map of the data
        Map<String, Object> data = new HashMap<>();
        CollectionReference eventsRef = FirebaseFirestore.getInstance().collection("events");
//        eventsRef.document(event.getTitle()).update();
    }
}
