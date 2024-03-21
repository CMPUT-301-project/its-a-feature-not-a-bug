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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import java.util.UUID;
import com.google.zxing.WriterException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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
        EditText editEventHost = view.findViewById(R.id.edit_tex_event_host);
        DatePicker editEventDate = view.findViewById(R.id.date_picker_event_date);
        EditText editEventDescription = view.findViewById(R.id.edit_text_event_description);
        Switch switchAttendeeLimit = view.findViewById(R.id.switch_attendee_limit);
        EditText editEventLimit = view.findViewById(R.id.edit_text_limit);
        eventPoster = view.findViewById(R.id.image_view_event_image);
        Button uploadButton = view.findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(v -> {
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
                String host = editEventHost.getText().toString();
                int year = editEventDate.getYear();
                int month = editEventDate.getMonth();
                int dayOfMonth = editEventDate.getDayOfMonth();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Date date = calendar.getTime();
                String description = editEventDescription.getText().toString();
                int attendeeLimit = 0;

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

                // Create Event object with appropriate constructor based on Switch state
                if (switchAttendeeLimit.isChecked()) {
                    uploadImageToFirebaseStorage(title, host, date, description, attendeeLimit);
                } else {
                    uploadImageToFirebaseStorage(title, host, date, description, 0);
                }

                alertDialog.dismiss();
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
     * @param title the title of the new Event
     * @param host the organizer of the new Event
     * @param date the date of the new event
     * @param description the description of the new event
     * @param attendeeLimit the max number of attendees for the new event
     */
    private void uploadImageToFirebaseStorage(String title, String host, Date date, String description, int attendeeLimit) {
        if (selectedImageUri != null) {
            StorageReference storageReference = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");
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
                    Event newEvent; // Declare the variable here
                    if (attendeeLimit != 0) {
                        newEvent = new Event(selectedImageURL, title, host, date, description, attendeeLimit);
                    } else {
                        newEvent = new Event(selectedImageURL, title, host, date, description);
                    }
                    listener.addEvent(newEvent); // Ensure this is correctly adding the event

                    // Add the event to Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("events").add(newEvent)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("Firestore", "Event added with ID: " + documentReference.getId());
                                Bitmap qrCodeBitmap = QRCodeGenerator.generatePromotionalQRCode(newEvent, 512);
                                displayQRCode(qrCodeBitmap); // Call a method to handle QR code display
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error adding event", e));
                } else {
                    Log.e("TAG", "Failed to upload image to Firebase Storage: " + task.getException());
                }
            });
        }
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
}