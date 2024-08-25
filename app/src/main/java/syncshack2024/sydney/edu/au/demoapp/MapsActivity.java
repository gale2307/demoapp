package syncshack2024.sydney.edu.au.demoapp;

import static syncshack2024.sydney.edu.au.demoapp.jackUtils.sportToHue;
import static syncshack2024.sydney.edu.au.demoapp.jackUtils.stringToSports;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import syncshack2024.sydney.edu.au.demoapp.model.Room;
import syncshack2024.sydney.edu.au.demoapp.model.User;
import syncshack2024.sydney.edu.au.demoapp.util.FirestoreUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final LatLng DEFAULT_LOCATION = new LatLng(-33.852, 151.211); // Default location (Sydney, Australia)
    private static final float DEFAULT_ZOOM = 15.0f;

    private ArrayList<Room> allRooms = new ArrayList<>();
    private ArrayList<Room> filteredRooms = new ArrayList<>();
    private User user = new User("test1", new ArrayList<>());
    private Map<Marker, Room> markerRoomMap = new HashMap<>();

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private FirebaseFirestore mFirestore;
    private static final int REQUEST_CODE_CREATE_EVENT = 1;
    private static final int REQUEST_CODE_ROOM_DETAILS = 2;
    private static final int REQUEST_CODE_SIGN_IN = 9001;

    private void readItemsFromDatabase() {
        //Use asynchronous task to run query on the background and wait for result
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //read items from database
                    CollectionReference rooms = mFirestore.collection("rooms_test");
                    Query qry = rooms; //GET all entries
                    qry.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("GET_item", "1");
                                    Room room = document.toObject(Room.class);
                                    Log.d("GET_item", room.toString());
                                    allRooms.add(room);
                                    Log.d("GET_item", "3");
                                    // uncomment below
//                                    addNewMarker(room.getLat(), room.getLng(), room.getTitle(),
//                                            sportToHue(room.getSportsCategory()));
                                    Marker marker = addNewMarker(room.getLat(), room.getLng(), room.getTitle(), 240.0f);
                                    markerRoomMap.put(marker, room);
                                    Log.d("GET_item", document.getId() + " => " + room.toString());
                                }
                                //
                            } else {
                                Log.d("GET_item", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }
            });
            // Block and wait for the future to complete
            future.get();

        }
        catch (Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFirestore = FirestoreUtil.getFirestoreInstance();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);

        readItemsFromDatabase();

        // Find the button by its ID
        Button createEventButton = findViewById(R.id.create_event_button);
        // Set an OnClickListener on the button
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, CreateEventActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CREATE_EVENT);
            }
        });

        // Find the button by its ID
        Button userRoomListActivity = findViewById(R.id.myEventsButton);
        // Set an OnClickListener on the button
        userRoomListActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, UserRoomListActivity.class);
                Log.d("my room", user.getRooms().toString());
                intent.putExtra("allRooms", user.getRooms());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // Start sign in if necessary
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startSignIn();
            return;
        }

        // Apply filters
        // onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        //if (mAdapter != null) {
        //    mAdapter.startListening();
        //}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREATE_EVENT && resultCode == RESULT_OK) {
            // Get the returned data from CreateEventActivity

            String roomTitle = data.getStringExtra("roomTitle");
            String roomDescription = data.getStringExtra("roomDescription");
            String sportCategory = data.getStringExtra("sportCategory");
//            String placeName = data.getStringExtra("selectedPlaceName");
            LatLng placeLatLng = data.getParcelableExtra("selectedPlaceLatLng");

            Marker marker = addNewMarker(placeLatLng.latitude, placeLatLng.longitude, roomTitle);

            // add room to all rooms
            Room r = new Room(roomTitle, roomDescription);
            r.setSportsCategory(stringToSports(sportCategory));
            r.setLat(placeLatLng.latitude);
            r.setLng(placeLatLng.longitude);
            allRooms.add(r);
            markerRoomMap.put(marker, r);
            user.getRooms().add(roomTitle);

        }

        if (requestCode == REQUEST_CODE_ROOM_DETAILS && resultCode == RESULT_OK) {
//            Log.d("didJoin", data.getStringExtra("didJoin"));
            if (data.getBooleanExtra("didJoin", false)) {
                user.getRooms().add(data.getStringExtra("title"));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        }

        // Set a listener for info window click
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Start a new activity
                Intent intent = new Intent(MapsActivity.this, RoomDetailsActivity.class);
                Log.d("changing window", "1");
                Room room = markerRoomMap.get(marker);
                Log.d("changing window", room.toString());
                intent.putExtra("title", room.getTitle());
                intent.putExtra("roomObject", room);
                startActivityForResult(intent, REQUEST_CODE_ROOM_DETAILS);
            }
        });
    }

    private Marker addNewMarker(double lat, double lng, String title) {
        return addNewMarker(lat, lng, title, BitmapDescriptorFactory.HUE_AZURE);
    }

    private Marker addNewMarker(double lat, double lng, String title, float hue) {
        return mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(hue)));
    }

    private void filterRooms() {
        filteredRooms = allRooms;
    }

    private void resetMarkers() {
        filterRooms();
        addAllMarkers();
    }

    private void addAllMarkers() {
        for (Room room : allRooms) {
            addNewMarker(room.getLat(), room.getLng(), room.getTitle(), sportToHue(room.getSportsCategory()));
        }
    }

    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d("MapsActivity", "Current location is null. Using defaults.");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getDeviceLocation();
                }
            }
        }
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, REQUEST_CODE_SIGN_IN);
        //mViewModel.setIsSigningIn(true);
    }

}