// This source code file implements the functionality of adding an event to a list of events
// for the user to browse and upload it to the database.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

import java.util.UUID;

/**
 * This class is an extension of a DialogFragment and allows the user to create an event by entering details.
 */
public class AddEventFragment extends DialogFragment {
    private AddEventDialogueListener listener;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private StorageReference storageRef;
    private ImageView eventPoster;

    public AddEventFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogueListener) {
            listener = (AddEventDialogueListener) context;
        } else {
            throw new RuntimeException(context + "must implement AddEventDialogueListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_event, null);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        EditText editEventId = view.findViewById(R.id.edit_text_event_title);
        DatePicker editEventDate = view.findViewById(R.id.date_picker_event_date);
        TimePicker editEventTime = (TimePicker) view.findViewById(R.id.time_picker_event_time);
        EditText editEventDescription = view.findViewById(R.id.edit_text_event_description);
        Switch switchAttendeeLimit = view.findViewById(R.id.switch_attendee_limit);
        EditText editEventLimit = view.findViewById(R.id.edit_text_limit);
        eventPoster = view.findViewById(R.id.image_view_event_image);

        eventPoster.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        eventPoster.setImageURI(selectedImageUri);
                    }
                });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder
                .setView(view)
                .setTitle("Add Event")
                .setNegativeButton("cancel", null)
                .setPositiveButton("ok", null) // Delaying positive button action to handle Switch change
                .create();

        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String title = editEventId.getText().toString();
                int year = editEventDate.getYear();
                int month = editEventDate.getMonth();
                int dayOfMonth = editEventDate.getDayOfMonth();

                // Initialize Calendar instance to combine date and time
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                // Handling time from TimePicker
                int hour, minute;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = editEventTime.getHour();
                    minute = editEventTime.getMinute();
                } else {
                    hour = editEventTime.getCurrentHour(); // Deprecated in API 23
                    minute = editEventTime.getCurrentMinute(); // Deprecated in API 23
                }
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                // This Date object now has both the correct date and time
                Date combinedDate = calendar.getTime();

                String description = editEventDescription.getText().toString();
                Integer attendeeLimit = 0;

                // Check if Switch is checked
                if (switchAttendeeLimit.isChecked()) {
                    // Parse attendee limit from EditText
                    try {
                        attendeeLimit = Integer.parseInt(editEventLimit.getText().toString());
                    } catch (NumberFormatException e) {
                        // Handle invalid input
                        editEventLimit.setError("Invalid attendee limit");
                        return;
                    }
                }

                // create new event
                newEvent.setTitle(title);
                newEvent.setDate(combinedDate);
                newEvent.setHost(Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
                newEvent.setDescription(description);
                newEvent.setAttendeeLimit(attendeeLimit);

                if (selectedImageUri != null) {
                    uploadImageToFirebaseStorage(newEvent, new OnImageUploadListener() {
                        @Override
                        public void onImageUploadSuccess(String imageURL) {
                            newEvent.setImageId(imageURL);
                            listener.addEvent(newEvent);
                            alertDialog.dismiss();
                        }

                        @Override
                        public void onImageUploadFailure(String errorMessage) {
                            // Handle upload failure with error message
                        }
                    });
                } else {
                    listener.addEvent(newEvent);
                    alertDialog.dismiss();
                }

            });
        });

        // Toggle visibility of attendee limit EditText based on Switch state
        switchAttendeeLimit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editEventLimit.setVisibility(View.VISIBLE);
            } else {
                editEventLimit.setVisibility(View.INVISIBLE);
            }
        });

        return alertDialog;
    }

    /**
     * This uploads an image to the Firestore
     * @param newEvent the event that the image belongs to
     */
    private void uploadImageToFirebaseStorage(Event newEvent, OnImageUploadListener uploadListener) {
        StorageReference storageReference = storageRef.child("event_posters/" + UUID.randomUUID().toString() + ".jpg");
        eventPoster.setDrawingCacheEnabled(true);
        eventPoster.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) eventPoster.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String selectedImageURL = downloadUri.toString();
                uploadListener.onImageUploadSuccess(selectedImageURL); // Callback with URL
            } else {
                uploadListener.onImageUploadFailure(task.getException().getMessage());
            }
        });
    }

    private void displayQRCode(Bitmap qrCodeBitmap) {
        getActivity().runOnUiThread(() -> {
            ImageView qrCodeImageView = getActivity().findViewById(R.id.qrCodeImageView);
            if (qrCodeImageView != null) {
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
                qrCodeImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    interface OnImageUploadListener {
        void onImageUploadSuccess(String imageURL);
        void onImageUploadFailure(String errorMessage);
    }
}