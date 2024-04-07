// This source code file adds the user to the database if they do not exist upon
// running the application.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * This is the activity that creates a new user profile.
 * This activity extends AppCompatActivity to inherit its basic functionalities.
 */
public class NewUserActivity extends AppCompatActivity {
    // Firebase attributes
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // Local device attributes
    private String androidId;

    // View attributes
    private EditText editName;
    private Button submitButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set views
        setContentView(R.layout.activity_new_user);
        editName = findViewById(R.id.edit_text_new_user_name);
        submitButton = findViewById(R.id.button_add_new_user);

        // Set action bar title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "QRCHECKIN" + "</b></font>"));
        }

        // connect to Firebase
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // get android id
        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // set button listeners
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = editName.getText().toString();
                addUserToDatabase(userName);
            }
        });

    }

    /**
     * This adds the new user to the Firebase Firestore.
     * @param userName the name of the new user
     */
    public void addUserToDatabase(String userName) {
        // put data in hash map
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", userName);
        data.put("userId", androidId);

        // generate profile picture based on user's name
        Bitmap profilePicture = generateProfilePicture(userName);

        // upload image to Firebase
        uploadImageToFirebaseStorage(profilePicture, new OnImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String imageURL) {
                data.put("imageId", imageURL);
                usersRef.document(androidId)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Firestore", "DocumentSnapshot successfully written");
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", "Failed to upload event", e);
                            }
                        });
            }

            @Override
            public void onImageUploadFailure(String errorMessage) {
                // handle
            }
        });
    }

    /**
     * This generates the Bitmap for the user's deterministically generated profile picture.
     * @param userName the name of the new user
     * @return the Bitmap representing the profile picture
     */
    public Bitmap generateProfilePicture(String userName) {
        // Create a bitmap with desired width and height
        int width = 200;
        int height = 200;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a canvas to draw on bitmap
        Canvas canvas = new Canvas(bitmap);

        // Fill the background with a color
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(255, 255, 255));
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // Draw initials on the profile picture
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Calculate text position
        Rect textBounds = new Rect();
        textPaint.getTextBounds(userName, 0, 1, textBounds);
        float x = width / 2f;
        float y = height / 2f + textBounds.height() / 2f;

        // Draw initials on the canvas
        canvas.drawText(userName.substring(0, 1).toUpperCase(), x, y, textPaint);

        return bitmap;
    }

    /**
     * This uploads the user's profile picture to Firebase.
     * @param profilePicture the Bitmap of the profile picture
     * @param uploadListener the image upload listener
     */
    private void uploadImageToFirebaseStorage(Bitmap profilePicture, NewUserActivity.OnImageUploadListener uploadListener) {
        StorageReference storageReference = storageRef.child("profile_pics/" + UUID.randomUUID().toString() + ".jpg");

        // convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // start upload task
        UploadTask uploadTask = storageReference.putBytes(imageData);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String selectedImageURL = task.getResult().toString();
                uploadListener.onImageUploadSuccess(selectedImageURL); // Callback with URL
            } else {
                uploadListener.onImageUploadFailure(task.getException().getMessage());
            }
        });
    }

    /**
     * This interface implements the image upload listener.
     */
    interface OnImageUploadListener {
        void onImageUploadSuccess(String imageURL);
        void onImageUploadFailure(String errorMessage);
    }
}
