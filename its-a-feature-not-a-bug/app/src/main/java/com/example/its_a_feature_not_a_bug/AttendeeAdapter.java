package com.example.its_a_feature_not_a_bug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {

    private ArrayList<User> attendees;

    private Event event;

    public AttendeeAdapter(ArrayList<User> attendees, Event event) {
        this.event = event;
        if (attendees == null) {
            this.attendees = new ArrayList<User>();
            // populate with dummy data
            this.attendees.add(new User("Jing"));
            this.attendees.add(new User("Tanveer"));
        } else {
            this.attendees = attendees;
        }
    }


    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        User attendee = attendees.get(position);
        String name = attendee.getName();
        String stringNumTimesCheckIn = "Checked in: " + attendee.getNumTimesCheckedIn(this.event);
        String info = name + "\t" + stringNumTimesCheckIn;
        holder.attendeeInfo.setText(stringNumTimesCheckIn);
    }

    @Override
    public int getItemCount() {
        return attendees.size();
    }

    static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView attendeeInfo;
        AttendeeViewHolder(View itemView) {
            super(itemView);
            attendeeInfo = itemView.findViewById(android.R.id.text1);
        }
    }
}