package com.example.its_a_feature_not_a_bug;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerMapActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;

    private ItemizedOverlay<OverlayItem> mapOverlay;

    private Event currentEvent;

    private Button backButton;

    private ArrayList<User> checkedAttendees;

    private Map<String, List<Double>> attendeeLocations;

    private FirebaseFirestore db;

    private CollectionReference locationsRef;

    private CollectionReference usersRef;

    private RotationGestureOverlay rotationGestureOverlay;

    private IMapController mapController;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_organizer_map);

        requestPermissionsIfNecessary(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        // Initialize views
        mapView = findViewById(R.id.map);
        backButton = findViewById(R.id.back_button_map);

        //map calibration
        mapView.setTilesScaledToDpi(true);
        rotationGestureOverlay = new RotationGestureOverlay(mapView);
        rotationGestureOverlay.setEnabled(true);
        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(rotationGestureOverlay);

        // Fetch current event
        currentEvent = (Event) getIntent().getSerializableExtra("event");

        //database connections
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        locationsRef = db.collection("events")
                .document(currentEvent.getTitle())
                .collection("Attendee Locations");

        checkedAttendees = new ArrayList<>();
        attendeeLocations = new HashMap<>();

        //only populate map if attendees have checked in
        if (currentEvent.getNumberCheckIns() > 0) {
            populateCheckedAttendees();
            populateAttendeeLocations();
            populateMapWithMarkers();
        }

        //Center map around organizer's location
        mapController = mapView.getController();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        centerMapLocation();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void populateCheckedAttendees() {
        ArrayList<String> attendeesData = currentEvent.getCheckedInAttendees();
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (attendeesData.contains(user.getUserId())) {
                            checkedAttendees.add(user);
                            Log.d("Brayden", user.getUserId());
                        }
                    }
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void populateAttendeeLocations() {
        locationsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot attendeeLocationsSnapshot = task.getResult();
                    if (attendeeLocationsSnapshot != null && !attendeeLocationsSnapshot.isEmpty()) {
                        for (DocumentSnapshot document : attendeeLocationsSnapshot) {
                            AttendeeLocationInformation locationInformation = document.toObject(AttendeeLocationInformation.class);
                            locationInformation.setTitle(document.getId());
                            Map<String, Object> locationData = document.getData();
                            if (locationData != null && locationData.containsKey("latitude") && locationData.containsKey("longitude")) {
                                Double latitude = Double.valueOf(locationInformation.getLatitude());
                                Double longitude = Double.valueOf(locationInformation.getLongitude());

                                List<Double> gpsCoordinates = Arrays.asList(latitude, longitude);
                                attendeeLocations.put(locationInformation.getTitle(), gpsCoordinates);
                            }
                        }
                    }
                } else {
                    Log.e("Firestore", "Error getting attendee locations: ", task.getException());
                }
            }
        });
    }

    private void populateMapWithMarkers() {
        final ArrayList<OverlayItem> items = new ArrayList<>();

        for (User user : checkedAttendees) {
            if (attendeeLocations.containsKey(user.getUserId())) {
                List<Double> coordinates = attendeeLocations.get(user.getUserId());
                double latitude = coordinates.get(0);
                double longitude = coordinates.get(1);

                // Create OverlayItem with user's information
                OverlayItem overlayItem = new OverlayItem(user.getFullName(), user.getEmail(),
                        new GeoPoint(latitude, longitude));
                items.add(overlayItem);
            }
        }

        // Create the ItemizedOverlay
        this.mapOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        // Display user's name and email on single tap
                        Toast.makeText(OrganizerMapActivity.this,
                                "Name: " + item.getTitle() + "\nEmail: " + item.getSnippet(),
                                Toast.LENGTH_LONG).show();
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        // Display user's name, email, and phone number on long press
                        Toast.makeText(OrganizerMapActivity.this,
                                "Name: " + item.getTitle() + "\nEmail: " + item.getSnippet()
                                        + "\nPhone: " + getUserPhoneNumber(item.getTitle()),
                                Toast.LENGTH_LONG).show();
                        return true;
                    }
                }, getApplicationContext());

        // Add the ItemizedOverlay to the MapView's overlays
        this.mapView.getOverlays().add(this.mapOverlay);
    }

    private String getUserPhoneNumber(String fullName) {
        for (User user : checkedAttendees) {
            if (user.getFullName().equals(fullName)) {
                return user.getPhoneNumber();
            }
        }
        return "";
    }

    private void centerMapLocation() {
        // Check for location permissions again before getting last known location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Get last known location
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<android.location.Location>() {
                        @Override
                        public void onComplete(@NonNull Task<android.location.Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                // Get the last known location
                                Location lastLocation = task.getResult();
                                Log.d("OrganizerMapActivity", "got location");
                                // Create a GeoPoint object with the obtained coordinates
                                GeoPoint userLocation = new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
                                mapController.setZoom(5.); //zoom in a bit
                                // Set the center of the map to the user's location
                                mapController.setCenter(userLocation);
                            }
                        }
                    });
        }

    }
}
