package syncshack2024.sydney.edu.au.demoapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import syncshack2024.sydney.edu.au.demoapp.model.Room;
import syncshack2024.sydney.edu.au.demoapp.util.FirestoreUtil;


// RoomDetailsActivity
public class RoomDetailsActivity extends AppCompatActivity {

    private TextView Title;
    private TextView Description;
    private TextView StartDate;
    private TextView EndDate;
    private TextView SportCategory;
    private Button JoinButton;
    private FirebaseFirestore mFirestore;
    private Room room;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);
//        String title = getIntent().getStringExtra("title");
        String title = "Test 3";

        Title = findViewById(R.id.Title);
        StartDate = findViewById(R.id.StartDate);
        EndDate = findViewById(R.id.EndDate);
        Description = findViewById(R.id.Description);
        SportCategory = findViewById(R.id.SportCategory);
        JoinButton = findViewById(R.id.btnAddNew);
//        JoinButton.setVisibility(View.INVISIBLE);

        mFirestore = FirestoreUtil.getFirestoreInstance();

        room = new Room();
        readItemsFromDatabase(title);

}
    public void onCancelClick(View v) {
        finish(); // Close the activity, pass data to parent
    }



    public void onSubmit(View v) {

        Intent data = new Intent();
        data.putExtra("roomObject", room);

        // Activity finishes OK, return the data
        setResult(RESULT_OK, data); // Set result code and bundle data for response
        finish(); // Close the activity, pass data to parent
    }


    private void readItemsFromDatabase(String title) {
        //Use asynchronous task to run query on the background and wait for result
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //read items from database
                    CollectionReference rooms = mFirestore.collection("rooms_test");
//                    DocumentReference docRef = rooms.document(uid);
                    Query qry = rooms.whereEqualTo("title", title); //GET w/ filter
                    qry.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        room = document.toObject(Room.class);
                                        if (room != null) {

                                            Title.setText(room.getTitle());

                                            Date startDate = room.getStartDate();
                                            String startDateString = formatter.format(startDate);
                                            StartDate.setText(startDateString);

                                            Date endDate = room.getEndDate();
                                            String endDateString = formatter.format(endDate);
                                            EndDate.setText(endDateString);

                                            String sportsCategory = room.getSportsCategory().toString();
                                            SportCategory.setText(sportsCategory);

                                            String description = room.getDescription();
                                            Description.setText(description != null ? description : "No description for this event");

                                            ArrayList<String> users = room.getParticipants();

                                        }
                                        // adapter notify change
                                        Log.d("GET - docref", document.getId() + " => " + room.toString());
                                    } else {
                                        Log.d("GET - docref", "No such document");
                                    }
                                }
                            } else {
                                Log.d("GET - docref", "get failed with ", task.getException());
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

}