package com.example.its_a_feature_not_a_bug;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class EditEventActivity extends AppCompatActivity {
    private Event currentEvent;
    private EditText editTextEventTitle, editTextEventDescription, editTextAttendeeLimit;
    private Switch switchAttendeeLimit;
    private DatePicker datePickerEventDate;
    private TimePicker timePickerEventTime;

    private Uri selectedImageUri = null;
    private ImageView imageViewEventPoster;
    private Button buttonChangePoster, buttonSaveEventDetails;

//    private static final int EDIT_EVENT_REQUEST_CODE = 1;
//    private static final int IMAGE_PICKER_REQUEST_CODE = 2; // Choose a unique request code

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Initialize your fields
        editTextEventTitle = findViewById(R.id.editTextEventTitle);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        editTextAttendeeLimit = findViewById(R.id.editTextAttendeeLimit);
        switchAttendeeLimit = findViewById(R.id.switchAttendeeLimit);
        datePickerEventDate = findViewById(R.id.datePickerEventDate);
        timePickerEventTime = findViewById(R.id.timePickerEventTime);
        imageViewEventPoster = findViewById(R.id.imageViewEventPoster);
        buttonSaveEventDetails = findViewById(R.id.buttonSaveEventDetails);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imageViewEventPoster.setImageURI(selectedImageUri);
                    }
                });

        imageViewEventPoster.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });



        // Get the current event passed from OrganizerEventDetailsActivity
        currentEvent = (Event) getIntent().getSerializableExtra("event");
        populateFields(currentEvent);



        buttonSaveEventDetails.setOnClickListener(view -> saveEventDetails());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "EDIT EVENT DETAILS" + "</b></font>"));

        }
    }


    private void uploadImageToFirebaseStorage(Uri imageUri, OnImageUploadListener uploadListener) {
        String filename = "events/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = FirebaseStorage.getInstance().getReference(filename);

        fileRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return fileRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String imageUrl = downloadUri.toString();
                        uploadListener.onImageUploadSuccess(imageUrl); // Trigger callback with URL
                    } else {
                        Toast.makeText(EditEventActivity.this, "Image upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    interface OnImageUploadListener {
        void onImageUploadSuccess(String imageURL);
    }




    private void populateFields(Event event) {
        editTextEventTitle.setText(event.getTitle());
        editTextEventDescription.setText(event.getDescription());
        // Attendee limit and switch logic
        if (event.getAttendeeLimit() != null) {
            switchAttendeeLimit.setChecked(true);
            editTextAttendeeLimit.setVisibility(View.VISIBLE);
            editTextAttendeeLimit.setText(String.valueOf(event.getAttendeeLimit()));
        } else {
            switchAttendeeLimit.setChecked(false);
            editTextAttendeeLimit.setVisibility(View.INVISIBLE);
        }

        switchAttendeeLimit.setOnCheckedChangeListener((compoundButton, b) -> editTextAttendeeLimit.setVisibility(b ? View.VISIBLE : View.INVISIBLE));

        // Date and Time - Initialize with current event date/time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.getDate());
        datePickerEventDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerEventTime.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePickerEventTime.setCurrentMinute(calendar.get(Calendar.MINUTE));

        // Update poster image if necessary. You may want to use Glide or another image loading library
        // Glide.with(this).load(currentEvent.getImageUrl()).into(imageViewEventPoster);
    }

    private void saveEventDetails() {
        // Extract edited fields, validate them and construct a new Event object or update currentEvent
        String title = editTextEventTitle.getText().toString();
        String description = editTextEventDescription.getText().toString();
        Integer attendeeLimit = switchAttendeeLimit.isChecked() ? Integer.valueOf(editTextAttendeeLimit.getText().toString()) : null;

        // Combine date and time from DatePicker and TimePicker into a Date object
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePickerEventDate.getYear(), datePickerEventDate.getMonth(), datePickerEventDate.getDayOfMonth(), timePickerEventTime.getCurrentHour(), timePickerEventTime.getCurrentMinute());
        Date eventDate = calendar.getTime();

        // Update the event object
        currentEvent.setTitle(title);
        currentEvent.setDescription(description);
        currentEvent.setDate(eventDate);
        currentEvent.setAttendeeLimit(attendeeLimit);
        // Note: You'll also need to handle the event poster update logic

        if (selectedImageUri != null) {
            uploadImageToFirebaseStorage(selectedImageUri, imageUrl -> {
                // This callback will be triggered after image upload is successful
                currentEvent.setImageId(imageUrl);
                updateEventInFirestore(currentEvent); // Update Firestore with the complete event including the new image URL
            });
        } else {
            updateEventInFirestore(currentEvent); // No new image, just update the event details
        }

        // Return the modified event
        Intent returnIntent = new Intent();
        returnIntent.putExtra("editedEvent", currentEvent);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    private void updateEventInFirestore(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Assuming you use a unique ID for each event document. Adjust according to your Firestore structure.
        db.collection("events").document(event.getTitle())
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditEventActivity.this, "Event updated successfully.", Toast.LENGTH_SHORT).show();
                    // Optionally, finish activity or update UI as needed
                })
                .addOnFailureListener(e -> Toast.makeText(EditEventActivity.this, "Error updating event: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
}


