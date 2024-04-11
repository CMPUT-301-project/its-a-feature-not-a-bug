// This source code file implements the functionality to display events that the current
// user has signed up for.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * This displays the events that a user is signed up for.
 */
public class MyEventsActivity extends AppCompatActivity {

    private ListView myEventsListView;
    private Button backButton;
    private TextView noEventsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        myEventsListView = findViewById(R.id.list_view_my_events);
        noEventsTextView = findViewById(R.id.text_view_no_events);

        // Enable the action bar and display the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_arrow);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#368C6E"));
            actionBar.setBackgroundDrawable(colorDrawable);
            actionBar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\"><b>" + "MY EVENTS" + "</b></font>"));
        }

        // Retrieve myEventsList from intent
        Intent intent = getIntent();
        ArrayList<Event> myEventsList = (ArrayList<Event>) intent.getSerializableExtra("myEventsList");

        if (myEventsList == null || myEventsList.isEmpty()) {
            // No events signed up for, show the TextView
            myEventsListView.setVisibility(View.INVISIBLE);
            noEventsTextView.setVisibility(View.VISIBLE);
        } else {
            // Create and set the adapter
            EventAdapter adapter = new EventAdapter(this, myEventsList);
            myEventsListView.setAdapter(adapter);

            // Set click listener for ListView items
            myEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Start EventDetailsActivity and pass event object for the clicked item
                    Event clickedEvent = myEventsList.get(position);
                    Intent intent = new Intent(MyEventsActivity.this, AttendeeEventDetailsActivity.class);
                    intent.putExtra("event", clickedEvent);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * This implements the back button functionality for the action bar.
     * @param item The menu item that was selected
     * @return whether the back button was selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
