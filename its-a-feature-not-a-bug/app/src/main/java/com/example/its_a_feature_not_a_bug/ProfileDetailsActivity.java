package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private RecyclerView profilesRecyclerView;
    private ProfileAdapter profileAdapter;
    private User profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_details);

        db = FirebaseFirestore.getInstance();

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "PROFILE DETAILS" + "</b></font>"));

        }

        // Get the Profile object from the intent extra

        profile = (User) getIntent().getSerializableExtra("profile");


        // Initialize views
        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView fullNameTextView = findViewById(R.id.fullNameTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView phoneNumberTextView = findViewById(R.id.phoneNumberTextView);

        // Populate views with profile data
        if (profile != null) {
            // Load profile picture using Glide
            if (profile.getImageId() != null) {
                Glide.with(this)
                        .load(profile.getImageId())
                        .placeholder(R.drawable.default_profile_pic)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.default_profile_pic);
            }

            fullNameTextView.setText(profile.getFullName());
            emailTextView.setText(profile.getEmail());
            phoneNumberTextView.setText(profile.getPhoneNumber());
        }

        // Get reference to the delete profile button
        ImageView deleteProfileButton = findViewById(R.id.deleteProfileButton);

        // Set click listener for the delete profile button
//        deleteProfileButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        deleteProfileButton.setOnClickListener(v -> showDeleteOptionsDialog());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void showDeleteConfirmationDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Delete Profile")
//                .setMessage("Are you sure you want to delete this profile?")
//                .setPositiveButton("OK", (dialog, which) -> deleteProfile())
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
//                .show();
//    }

    private void showDeleteOptionsDialog() {
        final CharSequence[] options = {"Profile Picture", "Profile"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What would you like to delete?");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // "Profile Picture" was clicked
                showConfirmDeleteProfilePictureDialog();
            } else if (which == 1) {
                // "Profile" was clicked
                showConfirmDeleteProfileDialog();
            }
        });
        builder.show();
    }

    private void showConfirmDeleteProfilePictureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile Picture")
                .setMessage("Are you sure you want to delete this profile picture?")
                .setPositiveButton("OK", (dialog, which) -> removeProfilePicture())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showConfirmDeleteProfileDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("OK", (dialog, which) -> deleteProfile())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void removeProfilePicture() {
        // Assuming you're setting the profile picture to a default in your database,
        // add the logic here. For now, we'll just update the ImageView.
        ImageView profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setImageResource(R.drawable.default_profile_pic);

        // Remove the profile picture from Firebase Storage
        if (profile.getImageId() != null && !profile.getImageId().isEmpty()) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(profile.getImageId());
            photoRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("ProfileDetailsActivity", "Profile picture deleted successfully.");
                // Update the user's profile in Firestore to reflect the removal of the profile picture.
                db.collection("users").document(profile.getUserId())
                        .update("imageId", null)
                        .addOnSuccessListener(aVoid1 -> Log.d("ProfileDetailsActivity", "User profile updated."))
                        .addOnFailureListener(e -> Log.e("ProfileDetailsActivity", "Error updating user profile.", e));
            }).addOnFailureListener(e -> Log.e("ProfileDetailsActivity", "Error deleting profile picture.", e));
        }
    }


    private void deleteProfile() {
        String profileIdentifier = profile.getUserId();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (profile.getImageId() != null) {
            String imageToDelete = profile.getImageId();
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
        }

        db.collection("users").document(profileIdentifier)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle successful deletion
                    Toast.makeText(ProfileDetailsActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, navigate the user away from the current activity, back to the profile list or previous activity
                    finish(); // Close the current activity
                })
                .addOnFailureListener(e -> {
                    // Handle any errors during deletion
                    Toast.makeText(ProfileDetailsActivity.this, "Error deleting profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}