package com.example.its_a_feature_not_a_bug;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewUserActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    private String androidId;

    private EditText editName;
    private Button submitButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set views
        setContentView(R.layout.activity_new_user);
        editName = findViewById(R.id.edit_text_new_user_name);
        submitButton = findViewById(R.id.button_add_new_user);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = editName.getText().toString();
                addUserToDatabase(userName);
            }
        });

    }

    public void addUserToDatabase(String userName) {
        // get android id
        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // connect to database
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        // put data in hash map
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", userName);
        data.put("userId", androidId);

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
}
