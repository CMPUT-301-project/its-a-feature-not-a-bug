package com.example.its_a_feature_not_a_bug;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference profilesRef;
    private EditText editTextName;
    private EditText editTextHomepage;
    private EditText editTextContact;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editTextName = findViewById(R.id.editTextName);
        editTextHomepage = findViewById(R.id.editTextHomepage);
        editTextContact = findViewById(R.id.editTextContact);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                profilesRef = db.collection("profiles");

                Map<String,Object> data = new HashMap<>();
                data.put("name", editTextName.getText().toString());
                data.put("homepage", editTextHomepage.getText().toString());
                data.put("contact", editTextContact.getText().toString());

                profilesRef.document(editTextName.getText().toString()).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UpdateProfileActivity.this, "Submit successful", Toast.LENGTH_SHORT).show();
                            }
                        });

                finish();

//                String name = editTextName.getText().toString();
//                String homepage = editTextHomepage.getText().toString();
//                String contact = editTextContact.getText().toString();
//                Toast.makeText(UpdateProfileActivity.this, "Submit successful", Toast.LENGTH_SHORT).show();
//
//
//                // Use these values to update the user's profile
//                 //This will depend on how your user profiles are stored
//                // For example, if you are using Firebase, you would do something like:
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                        .setDisplayName(name)
//                        .setPhotoUri(Uri.parse(homepage))
//                        .build();
            }
        });
    }

    public static class QRCodeGenerator {

        /**
         * Generates a QR code bitmap for given content.
         *
         * @param content The content to be encoded in the QR code.
         * @param size    The width and height of the QR code.
         * @return The generated QR code bitmap.
         */
        public static Bitmap generateQR(String content, int size) {
            Bitmap bitmap = null;
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, size, size);
            } catch (WriterException e) {
                Log.e("QRCodeGenerator", e.getMessage());
            }
            return bitmap;
        }

        /**
         * Generates a promotional QR code for an event.
         * This can include a URL or deep link to the event within the app.
         *
         * @param event The event to generate a promotional QR code for.
         * @param size  The size of the QR code.
         * @return The generated QR code bitmap.
         */
        public static Bitmap generatePromotionalQRCode(Event event, int size) {
            // Example deep link or URL that directs users to the event details in the app
            String content = "app://events/" + event.getTitle(); // Adjust based on your URL scheme
            return generateQR(content, size);
        }

        /**
         * Generates a unique QR code for attendee check-ins at an event.
         *
         * @param event The event to generate a check-in QR code for.
         * @param size  The size of the QR code.
         * @return The generated QR code bitmap.
         */
        public static Bitmap generateCheckInQRCode(Event event, int size) {
            // Unique identifier for the event to facilitate check-ins
            String content = "checkIn://events/" + event.getTitle(); // Adjust based on your scheme
            return generateQR(content, size);
        }
    }
}
