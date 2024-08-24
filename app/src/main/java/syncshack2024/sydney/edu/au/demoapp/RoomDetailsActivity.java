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

import java.text.SimpleDateFormat;
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
    private FirebaseFirestore mFirestore;
    private Room room;

    Button join_event;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);
//        String uid = getIntent().getStringExtra("uid");
        String uid = "CwM6TVfjyyTaXElIaUgy";

        Title = findViewById(R.id.Title);
        StartDate = findViewById(R.id.StartDate);
        EndDate = findViewById(R.id.EndDate);
        Description = findViewById(R.id.Description);
        SportCategory = findViewById(R.id.SportCategory);

        mFirestore = FirestoreUtil.getFirestoreInstance();

        room = new Room();
        readItemsFromDatabase(uid);

//        join_event.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                update_user();
//            }
//        });
//    }
}

    public void onSubmit(View v) {
//        etItem = (EditText) findViewById(R.id.etEditItem);
//        spinnerCategory = findViewById(R.id.spinnerCategory);

        // Prepare data intent for sending it back
        Intent data = new Intent();

//        if (item == null) {
//            item = new Item(0, etItem.getText().toString(), Category.valueOf(spinnerCategory.getSelectedItem().toString()), calendar.getTime(), Boolean.FALSE);
//        }

//        item.setName(etItem.getText().toString());
//        item.setCategory(Category.valueOf(spinnerCategory.getSelectedItem().toString()));
//        item.setDueDate(calendar.getTime());

//        data.putExtra("item", item);
//        data.putExtra("position", position);
//        data.putExtra("title", Title.getText().toString());
//        data.putExtra("latitude", room.getLat());
//        data.putExtra("title", room.getLng());
        data.putExtra("roomObject", room);

        // Activity finishes OK, return the data
        setResult(RESULT_OK, data); // Set result code and bundle data for response
        finish(); // Close the activity, pass data to parent

        // lat lng Room title
    }



    private void readItemsFromDatabase(String uid) {
        //Use asynchronous task to run query on the background and wait for result
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //read items from database
                    CollectionReference rooms = mFirestore.collection("rooms_test");
                    DocumentReference docRef = rooms.document(uid);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
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

                                    }
                                    // adapter notify change
                                    Log.d("GET - docref", document.getId() + " => " + room.toString());
                                } else {
                                    Log.d("GET - docref", "No such document");
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

//    private void displayRoomDetails(Room room) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//
//        String htmlContent = "<html><body style='padding: 20px;'>" +
//                "<h1>" + room.getTitle() + "</h1>" +
//                "<p><strong>Description:</strong> " + room.getDescription() + "</p>" +
//                "<p><strong>Sport Category:</strong> " + room.getSportsCategory() + "</p>" +
//                "<p><strong>Latitude:</strong> " + room.getLat() + "</p>" +
//                "<p><strong>Longitude:</strong> " + room.getLng() + "</p>" +
//                "<p><strong>Room ID:</strong> " + room.getUid() + "</p>" +
//                "</body></html>";
//
//        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
//    }
}