package com.example.its_a_feature_not_a_bug;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BrowseEventsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        // connect to database
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        // listview
        eventList = findViewById(R.id.list_view_events_list);
        eventDataList = new ArrayList<>();

        // adapter
        eventAdapter = new EventAdapter(this, eventDataList);
        eventList.setAdapter(eventAdapter);

        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventDataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String eventId = doc.getId();
                        String host = doc.getString("Host");
                        Date date = doc.getDate("Date");
                        String description = doc.getString("Description");
                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventId, host));
                        eventDataList.add(new Event(eventId, host, date, description));
                    }
                    eventAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
