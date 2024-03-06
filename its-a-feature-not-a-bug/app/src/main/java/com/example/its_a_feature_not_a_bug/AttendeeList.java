package com.example.its_a_feature_not_a_bug;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AttendeeList {
    RecyclerView eventRecyclerView;
    EventArrayAdapter eventArrayAdapter;
    ArrayList<Event> events;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference eventRef = db.collection("events");

    public AttendeeList() {
    }

    public void OnCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendee_list, container, false);

        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("events");


        return view;
    }
}
