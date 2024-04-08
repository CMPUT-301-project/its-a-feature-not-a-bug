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
import android.provider.Settings;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    private Button buttonRemovePicture;
    private User currentUser;
    private String androidId;

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

        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        profilePicture = findViewById(R.id.image_view_profile_picture);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextFullName = findViewById(R.id.editTextFullName);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonRemovePicture = findViewById(R.id.button_remove_profile_picture);

        // Database initialization
        db = FirebaseFirestore.getInstance();
        profilesRef = db.collection("users");

        // fetch current user's data
        profilesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (androidId.equals(document.getId())) {
                            currentUser = document.toObject(User.class);
                            Log.d("Brayden", "currentUser: " + currentUser.getFullName());
                            break;
                        }
                    }

                    populateData();

                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });

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

        buttonRemovePicture.setOnClickListener(v -> showDeleteConfirmationDialog());


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get values from EditText fields
                String email = editTextEmail.getText().toString();
                String phoneNumber = editTextPhoneNumber.getText().toString();
                String fullName = editTextFullName.getText().toString();
                // create new user
                User newUser = new User();
                newUser.setFullName(fullName);
                newUser.setEmail(email);
                newUser.setPhoneNumber(phoneNumber);
                newUser.setGeoLocationDisabled(Boolean.FALSE);

                if (selectedImageUri != null) {
                    uploadImageToFirebaseStorage(newUser, new OnImageUploadListener() {
                        @Override
                        public void onImageUploadSuccess(String imageURL) {
                            newUser.setImageId(imageURL);
                            updateProfile(newUser);
                        }

                        @Override
                        public void onImageUploadFailure(String errorMessage) {
                            // handle upload failure with error message
                        }
                    });
                } else {
                    newUser.setImageId(currentUser.getImageId());
                    updateProfile(newUser);
                }
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

    private void uploadImageToFirebaseStorage(User newUser, OnImageUploadListener uploadListener) {
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
                uploadListener.onImageUploadSuccess(selectedImageURL);
            } else {
                Log.e("TAG", "Failed to upload image to Firebase Storage: " + task.getException());
                uploadListener.onImageUploadFailure(task.getException().getMessage());
            }
        });
    }

    public void updateProfile(User newUser) {
        // Create a map to store data
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", newUser.getFullName());
        data.put("email", newUser.getEmail());
        data.put("phoneNumber", newUser.getPhoneNumber());
        data.put("geoLocationDisabled", newUser.isGeoLocationDisabled()); // Store Switch state
        data.put("imageId", newUser.getImageId());

        // Add data to the database
        profilesRef.document(androidId).update(data)
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

    public void populateData() {
        // fill views with user's data
        if (currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()) {
            editTextFullName.setText(currentUser.getFullName());
        }
        if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
            editTextEmail.setText(currentUser.getEmail());
        }
        if (currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().isEmpty()) {
            editTextPhoneNumber.setText(currentUser.getPhoneNumber());
        }
        if (currentUser.getImageId() == null) {
            Log.d("Brayden", "no image");
        }

        if (currentUser.getImageId() != null && !currentUser.getImageId().isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(currentUser.getImageId())
                    .centerCrop()
                    .into(profilePicture);
        }

        Log.d("Firestore", "Fetched user data");
    }

    private void showDeleteConfirmationDialog() {
        if (currentUser.getImageId() != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Profile Picture")
                    .setMessage("Are you sure you want to delete this profile picture?")
                    .setPositiveButton("OK", ((dialog, which) -> removeProfilePicture()))
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                    .show();
        }
    }

    public void removeProfilePicture() {
        // remove profile picture from image view
        selectedImageUri = null;
        Glide.with(getApplicationContext())
                .load(R.drawable.default_profile_pic)
                .centerCrop()
                .into(profilePicture);

        // remove image from database storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String imageToDelete = currentUser.getImageId();
        storageRef = storage.getReferenceFromUrl(imageToDelete);
        storageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Image Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Firestore", "Error deleting image", exception);
                    }
                });

        // remove image from document field
        Map<String, Object> data = new HashMap<>();
        data.put("imageId", null);
        profilesRef.document(currentUser.getUserId()).update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "Updated user data");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Failed to update user data", e);
                    }
                });
    }

    interface OnImageUploadListener {
        void onImageUploadSuccess(String imageURL);
        void onImageUploadFailure(String errorMessage);
    }
}