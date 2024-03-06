package com.example.its_a_feature_not_a_bug;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class EventInfo extends Fragment {

    private Event event;
    private TextView name;
    private TextView date;
    private TextView location;
    private TextView description;
    private ImageView qrCode;

    public EventInfo(Event event) {
        this.event = event;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_event_info, container, false);
        name = view.findViewById(R.id.eventName);
        date = view.findViewById(R.id.eventDate);
        location = view.findViewById(R.id.eventLocation);
        description = view.findViewById(R.id.eventDescription);
        qrCode = view.findViewById(R.id.qrCode);
        displayInfo();
        return view;
    }

    public void displayInfo() {
        name.setText(event.getEventName());
        date.setText(event.getEventDate());
        location.setText(event.getEventLocation());
        description.setText(event.getEventDescription());
        qrCode.setImageBitmap(stringToBitmap(QRCodeGenerator.generateQRCode(event)));
    }

    private Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}

