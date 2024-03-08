package com.example.its_a_feature_not_a_bug;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Images.Media.getBitmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.URL;
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
        EditText editEventHost = view.findViewById(R.id.edit_tex_event_host);
        DatePicker editEventDate = view.findViewById(R.id.date_picker_event_date);
        EditText editEventDescription = view.findViewById(R.id.edit_text_event_description);
        eventPoster = view.findViewById(R.id.image_view_event_image);
        Button uploadButton = view.findViewById(R.id.upload_button);

        uploadButton.setOnClickListener(v -> {
//            imagePickerLauncher.launch("image/*");
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
//                    if (result != null) {
//                        // Set the selected image URI
//                        selectedImageUri = result;
//                        // Set the selected image to ImageView
//                        eventPoster.setImageURI(result);
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        eventPoster.setImageURI(selectedImageUri);
                    }
                });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Event")
                .setNegativeButton("cancel", null)
                .setPositiveButton("ok", (dialog, which) -> {
                    String title = editEventId.getText().toString();
                    String host = editEventHost.getText().toString();
                    int year = editEventDate.getYear();
                    int month = editEventDate.getMonth();
                    int dayOfMonth = editEventDate.getDayOfMonth();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    Date date = calendar.getTime();
                    String description = editEventDescription.getText().toString();

                    uploadImageToFirebaseStorage(title, host, date, description);
                })
                .create();
    }

    /**
     * this uploads an image the Firebase Storage.
     */
    private void uploadImageToFirebaseStorage(String title, String host, Date date, String description) {
        if (selectedImageUri != null) {
            StorageReference storageReference = storageRef.child("images/" + UUID.randomUUID().toString() + ".jpg");
            eventPoster.setDrawingCacheEnabled(true);
            eventPoster.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) eventPoster.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.continueWithTask( task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener( task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String selectedImageURL = downloadUri.toString();
                    Log.d("TAG", "Image URL: " + selectedImageURL);
                    listener.addEvent(new Event(selectedImageURL, title, host, date, description));
                } else {
                    // Handle failures
                    Log.e("TAG", "Failed to upload image to Firebase Storage: " + task.getException());
                }
            });
        }
    }
}