package com.example.its_a_feature_not_a_bug;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    private List<Announcement> announcementList;

    public AnnouncementAdapter(List<Announcement> announcementList) {
        this.announcementList = announcementList;
        /// populate with dummy data
        this.announcementList.add(new Announcement("Announcement 1", "This is the first announcement"));
        this.announcementList.add(new Announcement("Announcement 2", "This is the second announcement"));
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

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.announcementTitle);
            description = view.findViewById(R.id.announcementDescription);
        }
    }
}