// This source code file implements the adapter for a recycler view to be populated with profile data.
// No outstanding issues.

package com.example.its_a_feature_not_a_bug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * An adapter for the RecyclerView in the ProfileActivity.
 * This adapter is used to display the list of profiles.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    public interface OnProfileClickListener {
        void onProfileClick(Profile profile);
    }
    private List<Profile> profiles;
    private Context context;
    private OnProfileClickListener clickListener;

    public ProfileAdapter(Context context, List<Profile> profiles, OnProfileClickListener clickListener) {
        this.context = context;
        this.profiles = profiles;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Profile profile = profiles.get(position);
        holder.profileFullName.setText(profile.getFullName());
        if (profile.getProfilePicId() != null) {
            Glide.with(context)
                    .load(profile.getProfilePicId())
                    .placeholder(R.drawable.default_profile_pic)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.default_profile_pic);
        }

        // Set click listener for the entire profile item view
        holder.itemView.setOnClickListener(v -> clickListener.onProfileClick(profile));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView profileFullName;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            profileFullName = itemView.findViewById(R.id.profileFullName);
        }
    }
}