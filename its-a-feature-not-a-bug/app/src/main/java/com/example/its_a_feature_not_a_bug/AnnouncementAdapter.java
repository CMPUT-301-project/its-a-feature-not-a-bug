// This source code file implements the adapter for a recycler view to be populated with data,
// specifically announcements.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * An adapter for the RecyclerView in the AnnouncementActivity.
 * This adapter is used to display the list of announcements.
 */
public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private List<Announcement> announcementList;

    /**
     * This is a constructor for the class.
     * @param announcementList the list of announcements
     */
    public AnnouncementAdapter(List<Announcement> announcementList) {
        this.announcementList = announcementList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.title.setText(announcement.getTitle());
        holder.description.setText(announcement.getDescription());
    }

    /**
     * This returns the number of items in the list of announcements.
     * @return the number of announcements
     */
    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    /**
     * This class implements the view holder needed for adapting to a recycler view.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;

        /**
         * This is the constructor for the class.
         * @param view the view
         */
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.announcementTitle);
            description = view.findViewById(R.id.announcementDescription);
        }
    }
}