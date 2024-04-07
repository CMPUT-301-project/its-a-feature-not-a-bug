// This is the entry point into the program. It acts as a startup screen for the user.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.its_a_feature_not_a_bug.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * The main activity of the app.
 * This activity extends AppCompatActivity to inherit its basic functionalities.
 */
public class MainActivity extends AppCompatActivity {
    // Firebase attributes
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    // View attributes
    private ActivityMainBinding binding;
    private Button adminButton;
    private Button attendeeButton;
    private Button organizerButton;

    // Local device attributes
    private String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("DeviceID", "Device ID: " + androidId);

        // Set views
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adminButton = findViewById(R.id.button_admin_login);
        attendeeButton = findViewById(R.id.button_attendee_login);
        organizerButton = findViewById(R.id.button_organizer_login);

        // Set action bar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "ItsAFeatureNotABug" + "</b></font>"));
        }

        // Request camera permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }

        // connect to Firebase
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        // Fetch android ID and log it
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Fetch user IDs
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> userIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // fetch ID, log it, and store it in array
                        String userId = document.getId();
                        userIds.add(userId);
                    }

                    if (!userIds.contains(androidId)) { // this is a new user
                        Intent newUserIntent = new Intent(MainActivity.this, NewUserActivity.class);
                        startActivity(newUserIntent);
                    }
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

        // set button listeners
//        adminButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent adminIntent = new Intent(MainActivity.this, AdminDashboardActivity.class);
//                startActivity(adminIntent);
//            }
//        });

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminIntent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                startActivity(adminIntent);
            }
        });


        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendeeIntent = new Intent(MainActivity.this, AttendeeBrowseEventsActivity.class);
                startActivity(attendeeIntent);
            }
        });

        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent organizerIntent = new Intent(MainActivity.this, OrganizerBrowseEventsActivity.class);
                startActivity(organizerIntent);
            }
        });
    }
}