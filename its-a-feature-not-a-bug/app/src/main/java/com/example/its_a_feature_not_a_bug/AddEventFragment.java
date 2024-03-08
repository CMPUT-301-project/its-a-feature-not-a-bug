package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import java.net.URI;
import java.util.Calendar;
import java.util.Date;

/**
 * A DialogFragment that allows the user to add a new event.
 * The user can input the event title, host, date, description, and an optional attendee limit.
 * The user can also upload an image for the event.
 *
 * <p>This class implements the following functionality:</p>
 * <ul>
 *   <li>Image upload: The user can upload an image for the event. The image is selected using an image picker.</li>
 *   <li>Attendee limit: The user can optionally set a limit on the number of attendees for the event.</li>
 * </ul>
 *
 * <p>The dialog has two buttons:</p>
 * <ul>
 *   <li>OK: When the user clicks this button, the input data is validated and a new Event object is created.</li>
 *   <li>Cancel: This button dismisses the dialog without creating a new event.</li>
 * </ul>
 *
 * <p>The AddEventFragment must be attached to an activity that implements the AddEventDialogueListener interface.</p>
 *
 * @see AddEventDialogueListener
 */
public class AddEventFragment extends DialogFragment {
    private AddEventDialogueListener listener;
    private static final int IMAGE_PICK_REQUEST = 1;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri selectedImageUri;

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

        EditText editEventId = view.findViewById(R.id.edit_text_event_title);
        EditText editEventHost = view.findViewById(R.id.edit_tex_event_host);
        DatePicker editEventDate = view.findViewById(R.id.date_picker_event_date);
        EditText editEventDescription = view.findViewById(R.id.edit_text_event_description);
        Switch switchAttendeeLimit = view.findViewById(R.id.switch_attendee_limit);
        EditText editEventLimit = view.findViewById(R.id.edit_text_limit);
        ImageView eventPoster = view.findViewById(R.id.image_view_event_image);
        Button uploadButton = view.findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        // Set the selected image URI
                        selectedImageUri = result;
                        // Set the selected image to ImageView
                        eventPoster.setImageURI(result);
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
                    listener.addEvent(new Event(selectedImageUri, title, host, date, description, attendeeLimit));
                } else {
                    listener.addEvent(new Event(selectedImageUri, title, host, date, description));
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

}