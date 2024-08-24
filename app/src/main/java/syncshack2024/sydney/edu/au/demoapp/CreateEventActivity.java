package syncshack2024.sydney.edu.au.demoapp;

import static syncshack2024.sydney.edu.au.demoapp.jackUtils.sportToHue;
import static syncshack2024.sydney.edu.au.demoapp.jackUtils.stringToSports;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;

import syncshack2024.sydney.edu.au.demoapp.model.Room;
import syncshack2024.sydney.edu.au.demoapp.util.FirestoreUtil;

public class CreateEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner sportsSpinner;
    private EditText placeTypeEditText;
    private Button searchButton;
    private Button saveButton;

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng lastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final int RADIUS = 5000; // 5 km
    private Marker selectedMarker;
    private FirebaseFirestore mFirestore;

    private void postItemDatabase(Room room) {
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //read items from database
                    CollectionReference rooms = mFirestore.collection("rooms_test");
                    rooms.add(room);
                }
            });
            // Block and wait for the future to complete
            future.get();

        } catch (Exception ex) {
            Log.e("readItemsFromDatabase", ex.getStackTrace().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mFirestore = FirestoreUtil.getFirestoreInstance();

        // Initialize UI components
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        sportsSpinner = findViewById(R.id.sportsSpinner);
        placeTypeEditText = findViewById(R.id.placeTypeEditText);
        searchButton = findViewById(R.id.searchButton);
        saveButton = findViewById(R.id.saveButton);

        // Initialize the FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the sports spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportsSpinner.setAdapter(adapter);

        // Set up the search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNearbyPlaces();
            }
        });

        // Set up the save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Saving", "before entering saveSelectedPlace");
                saveSelectedPlace();
            }
        });

        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getDeviceLocation();

        // Set a listener for marker click.
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedMarker = marker;
                Toast.makeText(CreateEventActivity.this, "Selected: " + marker.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    // Method to search for nearby places based on user input
    private void searchNearbyPlaces() {
        String query = placeTypeEditText.getText().toString();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a place type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lastKnownLocation == null) {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%d&type=%s&key=%s",
                lastKnownLocation.latitude, lastKnownLocation.longitude, RADIUS, query, "AIzaSyAn5aA5g-1Lm87cxD_CelH3lJWFEomxiLs");

        Log.d("api_call", url);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        map.clear();
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject place = results.getJSONObject(i);
                                String placeName = place.getString("name");
                                JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                                LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                                map.addMarker(new MarkerOptions().position(latLng).title(placeName));
                            }
                        } catch (JSONException e) {
                            Log.e("CreateEventActivity", "JSON parsing error", e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CreateEventActivity", "Error fetching places", error);
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, DEFAULT_ZOOM));
                        Log.d("Location found", String.format("Current location : %f, %f", location.getLatitude(), location.getLongitude()));
                    } else {
                        Log.d("Location not found", "Current location is null. Using defaults.");
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.852, 151.211), DEFAULT_ZOOM));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    // Method to save the selected place and send it back to another activity
    private void saveSelectedPlace() {
        if (selectedMarker != null) {
            Room r = new Room(titleEditText.getText().toString(),
                    descriptionEditText.getText().toString());
            Log.d("Saving", "room created");
            LatLng pos = selectedMarker.getPosition();
            r.setSportsCategory(stringToSports(sportsSpinner.getSelectedItem().toString()));
            Log.d("Saving", "room 2");
            r.setLat(pos.latitude);
            Log.d("Saving", "room 3");
            r.setLng(pos.longitude);

            Log.d("Saving", "pre post database " + r.toString());
            postItemDatabase(r);
            Log.d("Saving", "post database ");

            Intent resultIntent = new Intent();

            resultIntent.putExtra("roomTitle", titleEditText.getText().toString());
            resultIntent.putExtra("roomDescription", descriptionEditText.getText().toString());
            resultIntent.putExtra("sportCategory", sportsSpinner.getSelectedItem().toString());
            resultIntent.putExtra("selectedPlaceName", selectedMarker.getTitle());
            resultIntent.putExtra("selectedPlaceLatLng", pos);
            setResult(RESULT_OK, resultIntent);
            Log.d("Saving", "all done");
            finish();
        } else {
            Log.d("Saving", "selected marker is null");
            Toast.makeText(this, "No place selected", Toast.LENGTH_SHORT).show();
        }
    }
}