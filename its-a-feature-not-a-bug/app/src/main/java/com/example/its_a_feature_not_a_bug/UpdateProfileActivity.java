// This source code file implements the functionality for a user to update their profile data.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An activity that allows users to update their profile.
 * This activity extends AppCompatActivity to inherit its basic functionalities.
 */
public class UpdateProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference profilesRef;
    private StorageReference storageRef;
//    private EditText editTextContactInfo;
    private ImageView profilePicture;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private EditText editTextEmail;
    private EditText editTextFullName;
    private EditText editTextPhoneNumber;
    private Button buttonSubmit;
    private Switch switchGeolocation; // Add Switch reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "UPDATE PROFILE" + "</b></font>"));

        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        profilePicture = findViewById(R.id.image_view_profile_picture);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextFullName = findViewById(R.id.editTextFullName);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        switchGeolocation = findViewById(R.id.switchGeolocation); // Initialize Switch reference

        // Database initialization
        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("profiles");

        profilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        profilePicture.setImageURI(selectedImageUri);
                    }
                });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get values from EditText fields
                String email = editTextEmail.getText().toString();
                String phoneNumber = editTextPhoneNumber.getText().toString();
                String fullName = editTextFullName.getText().toString();
                boolean geolocationDisabled = switchGeolocation.isChecked(); // Get Switch state

                uploadImageToFirebaseStorage(email, phoneNumber, fullName, geolocationDisabled);
            }
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

    private void uploadImageToFirebaseStorage(String email, String phoneNumber, String fullName, boolean geolocationDisabled) {
        if (selectedImageUri != null) {
            StorageReference storageReference = storageRef.child("profile_pics/" + UUID.randomUUID().toString() + ".jpg");
            profilePicture.setDrawingCacheEnabled(true);
            profilePicture.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] image_data = baos.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(image_data);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String selectedImageURL = downloadUri.toString();
                    addProfile(email, phoneNumber, fullName, geolocationDisabled, selectedImageURL);
                } else {
                    Log.e("TAG", "Failed to upload image to Firebase Storage: " + task.getException());
                }
            });
        } else {
            String selectedImageURL = Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" + R.drawable.default_poster).toString();
            addProfile(email, phoneNumber, fullName, geolocationDisabled, selectedImageURL);
        }
    }

    public void addProfile(String email, String phoneNumber, String fullName, boolean geolocationDisabled, String profilePic) {
        // Create a map to store data
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", fullName);
        data.put("email", email);
        data.put("phoneNumber", phoneNumber);
        data.put("geolocationDisabled", geolocationDisabled); // Store Switch state
        data.put("profilePic", profilePic);

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
}