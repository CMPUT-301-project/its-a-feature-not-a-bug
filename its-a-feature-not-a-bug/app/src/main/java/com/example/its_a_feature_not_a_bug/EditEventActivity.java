package com.example.its_a_feature_not_a_bug;

import android.app.Activity;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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


    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

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

        if (event.getAttendeeLimit() != null) {
            switchAttendeeLimit.setChecked(true);
            editTextAttendeeLimit.setVisibility(View.VISIBLE);
            editTextAttendeeLimit.setText(String.valueOf(event.getAttendeeLimit()));
        } else {
            switchAttendeeLimit.setChecked(false);
            editTextAttendeeLimit.setVisibility(View.INVISIBLE);
        }

        switchAttendeeLimit.setOnCheckedChangeListener((compoundButton, b) -> editTextAttendeeLimit.setVisibility(b ? View.VISIBLE : View.INVISIBLE));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(event.getDate());
        datePickerEventDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerEventTime.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePickerEventTime.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    private void saveEventDetails() {
        String title = editTextEventTitle.getText().toString();
        String description = editTextEventDescription.getText().toString();
        Integer attendeeLimit = switchAttendeeLimit.isChecked() ? Integer.valueOf(editTextAttendeeLimit.getText().toString()) : null;

        Calendar calendar = Calendar.getInstance();
        calendar.set(datePickerEventDate.getYear(), datePickerEventDate.getMonth(), datePickerEventDate.getDayOfMonth(), timePickerEventTime.getCurrentHour(), timePickerEventTime.getCurrentMinute());
        Date eventDate = calendar.getTime();

        currentEvent.setTitle(title);
        currentEvent.setDescription(description);
        currentEvent.setDate(eventDate);
        currentEvent.setAttendeeLimit(attendeeLimit);

        if (selectedImageUri != null) {
            uploadImageToFirebaseStorage(selectedImageUri, imageUrl -> {
                currentEvent.setImageId(imageUrl);
                updateEventInFirestore(currentEvent);
            });
        } else {
            updateEventInFirestore(currentEvent);
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("editedEvent", currentEvent);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    private void updateEventInFirestore(Event event) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(event.getTitle())
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditEventActivity.this, "Event updated successfully.", Toast.LENGTH_SHORT).show();
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


