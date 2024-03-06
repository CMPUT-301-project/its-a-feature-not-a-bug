//package its-a-feature-not-a-bug.app.src.main.java.com;
//
//import android.os.Bundle;
//import android.widget.TextView;
//import androidx.fragment.app.Fragment;
//
//import android.widget.ImageView;
//
//
//public class EventInfo {
//
//    private Event event;
//    private TextView name;
//    private TextView date;
//    private TextView location;
//    private TextView description;
//    private ImageView qrCode;
//
//    public EventInfo(Event event) {
//        this.event = event;
//    }
//    public void OnCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_event_info);
//        name = findViewById(R.id.eventName);
//        date = findViewById(R.id.eventDate);
//        location = findViewById(R.id.eventLocation);
//        description = findViewById(R.id.eventDescription);
//        qrCode = findViewById(R.id.qrCode);
//        displayInfo();
//    }
//
//    public void displayInfo() {
//        name.setText(event.getEventName());
//        date.setText(event.getEventDate());
//        location.setText(event.getEventLocation());
//        description.setText(event.getEventDescription());
//        qrCode.setImageBitmap(QRCodeGenerator.generateQRCode(event));
//    }
//}
//
