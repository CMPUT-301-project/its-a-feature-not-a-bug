package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OrganizerMenuActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;


    private String androidId;

    private User currentUser;
    private Event currentEvent;

    private RecyclerView checkedInAttendeesView;
    private CheckedInAttendeeAdapter attendeeAdapter;
    private ArrayList<String> checkedAttendees;
    private TextView checkedAttendeesHeader;
    private Button createAnnouncementButton;
    private Button sendQRButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set views
        setContentView(R.layout.activity_organizer_menu);
        checkedInAttendeesView = findViewById(R.id.recycler_view_checked_attendees);
        checkedAttendeesHeader = findViewById(R.id.text_view_checked_attendees_header);
        createAnnouncementButton = findViewById(R.id.button_create_announcement);
        sendQRButton = findViewById(R.id.button_send_QR);

        // fetch event from intent extras
        Intent intent = getIntent();
        currentEvent = (Event) intent.getSerializableExtra("event");

        // connect to database and collections
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");

        // set adapters
        checkedAttendees = new ArrayList<>();
        if (currentEvent.getCheckedInAttendees() != null) {
            populateCheckedAttendees();
        }
        attendeeAdapter = new CheckedInAttendeeAdapter(checkedAttendees);
        checkedInAttendeesView = findViewById(R.id.recycler_view_checked_attendees);
        checkedInAttendeesView.setLayoutManager(new LinearLayoutManager(this));
        checkedInAttendeesView.setAdapter(attendeeAdapter);

        // fetch current user build number
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // fetch current user data from Firestore
        usersRef.document(androidId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

        // send notifications (announcements)
//        createAnnouncementButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent notificationIntent = new Intent(getApplicationContext(), CreateNotificationActivity.class);
//                notificationIntent.putExtra("event", currentEvent);
//                startActivity(notificationIntent);
//            }
//        });

        // share QR code
        sendQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQROptionsDialog();
            }
        });
    }

    public void populateCheckedAttendees() {
        ArrayList<String> attendeesData = currentEvent.getCheckedInAttendees();
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (attendeesData.contains(user.getUserId())) {
                            checkedAttendees.add(user.getUserId());
                            Log.d("Brayden", "Added: " + user.getUserId());
                        }
                    }
                    attendeeAdapter.notifyDataSetChanged();
                    Log.d("Brayden", "updated adapter");
                    // update number of check-ins
                    checkedAttendeesHeader.setText(checkedAttendees.size() + " " + checkedAttendeesHeader.getText());
                } else {
                    Log.d("Firestore", "Error getting docuemnts: ", task.getException());
                }
            }
        });
    }

    private void showQROptionsDialog() {
        final CharSequence[] options = {"Promotional QR", "Check-in QR"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Which QR Code would you like to share?");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // "Promotional QR" was clicked
                Bitmap qrCodeBitmap = QRCodeGenerator.generatePromotionalQRCode(currentEvent, 200);
                shareBitmap(qrCodeBitmap);
            } else if (which == 1) {
                // "Check-in QR" was clicked
                // Generate and show the QR code if it's not already visible
                Bitmap qrCodeBitmap = QRCodeGenerator.generateCheckInQRCode(currentEvent, 200);
                shareBitmap(qrCodeBitmap);
            }
        });
        builder.show();
    }

    private void shareBitmap(Bitmap bitmap) {
        try {
            // Save Bitmap to cache directory
            File cachePath = new File(getExternalCacheDir(), "images");
            cachePath.mkdirs(); // Make sure directory exists
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // Share Bitmap using FileProvider
            File imagePath = new File(getExternalCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");

            // Dynamically retrieve the authority
            String authority = getPackageName() + ".fileprovider";

            Uri contentUri = FileProvider.getUriForFile(this, authority, newFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error sharing QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
