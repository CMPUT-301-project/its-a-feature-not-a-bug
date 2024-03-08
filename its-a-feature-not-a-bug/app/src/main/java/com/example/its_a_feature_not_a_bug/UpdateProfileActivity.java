// This source code file implements the functionality for a user to update their profile data.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * An activity that allows users to update their profile.
 * This activity extends AppCompatActivity to inherit its basic functionalities.
 */
public class UpdateProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference profilesRef;
    private EditText editTextContactInfo;
    private EditText editTextFullName;
    private Button buttonSubmit;
    private Switch switchGeolocation; // Add Switch reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editTextContactInfo = findViewById(R.id.editTextContactInfo);
        editTextFullName = findViewById(R.id.editTextFullName);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        switchGeolocation = findViewById(R.id.switchGeolocation); // Initialize Switch reference

        // Database initialization
        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("profiles");

        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and return to the previous one
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from EditText fields
                String contactInfo = editTextContactInfo.getText().toString();
                String fullName = editTextFullName.getText().toString();
                boolean geolocationDisabled = switchGeolocation.isChecked(); // Get Switch state

                // Create a map to store data
                Map<String, Object> data = new HashMap<>();
                data.put("contactInfo", contactInfo);
                data.put("fullName", fullName);
                data.put("geolocationDisabled", geolocationDisabled); // Store Switch state

                // Add data to the database
                profilesRef.document(fullName).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UpdateProfileActivity.this, "Submit successful", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateProfileActivity.this, "Failed to submit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
