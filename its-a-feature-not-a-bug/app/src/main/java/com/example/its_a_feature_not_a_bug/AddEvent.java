package com.example.its_a_feature_not_a_bug;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddEvent extends AppCompatActivity {

    private EditText eventName, eventDate, eventLocation, eventDescription,eventTime, date, time;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventName = findViewById(R.id.eventName);
        eventDate = findViewById(R.id.eventDate);
        eventLocation = findViewById(R.id.eventLocation);
        eventDescription = findViewById(R.id.eventDescription);
        eventTime = findViewById(R.id.eventTime);
        createButton = findViewById(R.id.createButton);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }

    private void showDatePickerDialog() {
        // Implement the method to show a date picker dialog

    }

    private void showTimePickerDialog() {
        // Implement the method to show a time picker dialog

    }

    private void createEvent() {
        // Implement the method to create an event

    }
}