package com.example.its_a_feature_not_a_bug;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import androidx.appcompat.app.AppCompatActivity;



public class AttendeesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        RecyclerView attendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        ArrayList<User> attendees = (ArrayList<User>) getIntent().getSerializableExtra("attendees");
        Event event = (Event) getIntent().getSerializableExtra("event");

        AttendeeAdapter attendeeAdapter = new AttendeeAdapter(attendees, event);
        attendeesRecyclerView.setAdapter(attendeeAdapter);


    }
}