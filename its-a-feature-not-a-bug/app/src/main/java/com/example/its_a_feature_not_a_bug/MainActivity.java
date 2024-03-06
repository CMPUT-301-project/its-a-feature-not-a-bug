package com.example.its_a_feature_not_a_bug;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    public static final Database db = new Database();
    public static AttendeeList.Attendee attendee =null;
    public static Event event = null;
    private Button adminButton;
    private Button attendeeButton;
    private Button scanButton;
    private Button generateButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request camera permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }

    }

    public void scanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Use the scanned QR code to check the attendee into the event
                String scannedQRCode = result.getContents();
                checkInAttendee(scannedQRCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkInAttendee(String qrCode) {
        // Use the QR code to find the corresponding event and check the attendee in
        // This will depend on how your events and attendees are stored
    }
    private static final int PICK_IMAGE = 1;

    public void uploadProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            // Upload the image to a server and get the image URL
            String imageUrl = uploadImage(imageUri);
            // Set the image URL as the profile picture of the attendee
            attendee.setProfilePicture(imageUrl);
        } else {
            // Handle other activity results
        }
    }

    private String uploadImage(Uri imageUri) {
        // This method should handle the image upload and return the image URL
        // The implementation will depend on your server and how you handle file uploads
        return null;
    }

}