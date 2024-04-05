// This source code file implements the adapter for a recycler view to be populated with data,
// Specifically attendees.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * An adapter for the RecyclerView in the AttendeeListActivity.
 * This adapter is used to display the list of attendees for an event.
 */
public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {

    private ArrayList<UserRefactored> attendees;

    private Event event;

    /**
     * This is a constructor for the class.
     * @param attendees the list of attendees
     * @param event the event to be attended
     */
    public AttendeeAdapter(ArrayList<UserRefactored> attendees, Event event) {
        this.event = event;
        if (attendees == null) {
            this.attendees = new ArrayList<>();
        } else {
            this.attendees = attendees;
        }
    }


    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.activity_list_item, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        UserRefactored attendee = attendees.get(position);
        String name = attendee.getFullName();
//        String stringNumTimesCheckIn = "Checked in: " + attendee.getNumTimesCheckedIn(this.event);
//        String info = name + "\t" + stringNumTimesCheckIn;
        holder.attendeeInfo.setText(name);
    }

    /**
     * This returns the number of attendees of the event.
     * @return the number of attendees
     */
    @Override
    public int getItemCount() {
        return attendees.size();
    }

    /**
     * This class implements the view holder needed for adapting to a recycler view.
     */
    static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView attendeeInfo;

        /**
         * This is the constructor for this class.
         * @param itemView the view
         */
        AttendeeViewHolder(View itemView) {
            super(itemView);
            attendeeInfo = itemView.findViewById(android.R.id.text1);
        }
    }
}