package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MyEventsActivity extends AppCompatActivity {

    private ArrayList<String> eventNames;
    private ListView myEventsListView;
    private Button backButton;
    private TextView noEventsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        myEventsListView = findViewById(R.id.list_view_my_events);
        backButton = findViewById(R.id.back_button);
        noEventsTextView = findViewById(R.id.text_view_no_events);

        // Retrieve myEventsList from intent
        Intent intent = getIntent();
        ArrayList<Event> myEventsList = (ArrayList<Event>) intent.getSerializableExtra("myEventsList");

        if (myEventsList == null || myEventsList.isEmpty()) {
            // No events signed up for, show the TextView
            myEventsListView.setVisibility(View.GONE);
            noEventsTextView.setVisibility(View.VISIBLE);
        } else {
            // There are events signed up for, show the ListView
            myEventsListView.setVisibility(View.VISIBLE);
            noEventsTextView.setVisibility(View.GONE);

            // Create and set the adapter
            EventAdapter adapter = new EventAdapter(this, myEventsList);
            myEventsListView.setAdapter(adapter);

            // Set click listener for ListView items
            myEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Start EventDetailsActivity and pass event object for the clicked item
                    Event clickedEvent = myEventsList.get(position);
                    Intent intent = new Intent(MyEventsActivity.this, EventDetailsActivity.class);
                    intent.putExtra("event", clickedEvent);
                    startActivity(intent);
                }
            });
        }

        // Set click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
