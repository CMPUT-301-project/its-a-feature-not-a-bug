package com.example.its_a_feature_not_a_bug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        // Assuming profilePicId is a URI or URL. If it's not, you'll need to adjust this.
        if (profile.getProfilePicId() != null) {
            Glide.with(context)
                    .load(profile.getProfilePicId())
                    .placeholder(R.drawable.default_profile_pic)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.default_profile_pic);
        }
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView profileFullName;
        Button removeButton;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            profileFullName = itemView.findViewById(R.id.profileFullName);
            removeButton = itemView.findViewById(R.id.removeButton);

            // Set onClickListener for remove button
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Pass the position to the clickListener interface
                        clickListener.onRemoveProfile(position);
                    }
                }
            });
        }
    }

    // Interface for click listener
    public interface OnProfileClickListener {
        void onRemoveProfile(int position);
        void onProfileClick(int position, Profile profile);
    }
}
