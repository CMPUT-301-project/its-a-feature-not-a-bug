package com.example.its_a_feature_not_a_bug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class EventAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        }

        Event event = events.get(position);

        TextView eventID = view.findViewById(R.id.text_view_event_name);
        TextView hostName = view.findViewById(R.id.text_view_event_host);
        TextView date = view.findViewById(R.id.text_view_event_date);
        ImageView poster = view.findViewById(R.id.image_view_event_poster);


        eventID.setText(event.getTitle());
        hostName.setText(event.getHost());
        date.setText(event.getDate().toString());

        if (event.getImageId() != null) {
            Glide.with(context)
                    .load(event.getImageId())
                    .into(poster);
        } else {
            // Set a placeholder image if no image is available
            poster.setImageResource(R.drawable.default_poster);
        }

        return view;
    }
}