package com.example.its_a_feature_not_a_bug;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class CheckedInAttendeeAdapter extends RecyclerView.Adapter<CheckedInAttendeeAdapter.AttendeeViewHolder>{
    private ArrayList<String> attendeeList;

    public CheckedInAttendeeAdapter(ArrayList<String> attendeeList) {
        if (attendeeList == null) {
            this.attendeeList = new ArrayList<>();
        } else {
            this.attendeeList = attendeeList;
        }
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checked_attendee_item, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        String userId = (String) attendeeList.get(position);
        holder.bind(userId);
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameTextView;
        private TextView checkInCountTextView;

        private FirebaseFirestore db;
        private CollectionReference usersRef;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.text_view_attendee_name);
        }

        public void bind(String userId) {
            db = FirebaseFirestore.getInstance();
            usersRef = db.collection("users");

            // Fetch user data from Firestore using userId and set the user's name
            usersRef.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserRefactored user = documentSnapshot.toObject(UserRefactored.class);
                    String fullName = user.getFullName(); // Implement this function to fetch user's name
                    userNameTextView.setText(fullName);
                }
            });
        }
    }
}
