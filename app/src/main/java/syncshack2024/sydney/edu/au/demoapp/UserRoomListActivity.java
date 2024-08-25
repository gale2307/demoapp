package syncshack2024.sydney.edu.au.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import syncshack2024.sydney.edu.au.demoapp.adapter.RoomAdapter;
import syncshack2024.sydney.edu.au.demoapp.model.Room;
import syncshack2024.sydney.edu.au.demoapp.model.User;
import syncshack2024.sydney.edu.au.demoapp.util.FirestoreUtil;

public class UserRoomListActivity extends AppCompatActivity {
    // Define variables
    ListView listView;
    ArrayList<Room> rooms;
    RoomAdapter roomsAdapter;
    private FirebaseFirestore mFirestore;
    ActivityResultLauncher<Intent> addNewLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_room_list);
        listView = (ListView) findViewById(R.id.lstView);

        // Init db
        mFirestore = FirestoreUtil.getFirestoreInstance();

        rooms = new ArrayList<>();
        readRoomsFromDatabase();

        // Create an adapter for the list view using Android's built-in item layout
        roomsAdapter = new RoomAdapter(this, rooms);
        listView.setAdapter(roomsAdapter);

        setupListViewListener();
    }

    public void onReturnClick(View view) {
        finish();
    }

    private void setupListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room updateRoom = (Room) roomsAdapter.getItem(position);
                Log.i("MainActivity", "Clicked item " + position + ": " + updateRoom);
                Intent intent = new Intent(UserRoomListActivity.this, RoomDetailsActivity.class);
                if (intent != null) {
                    // put "extras" into the bundle for access in the edit activity
                    intent.putExtra("uid", updateRoom.getUid());
                    intent.putExtra("title", updateRoom.getTitle());
                    intent.putExtra("position", position);
                    // brings up the second activity
                    UserRoomListActivity.this.startActivity(intent);
                    //mLauncher.launch(intent);
                }
            }

        });
    }

    private void readRoomsFromDatabase() {
        //Use asynchronous task to run query on the background and wait for result
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //read items from database
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    CollectionReference users = mFirestore.collection("users_test");
                    //DocumentReference docRef = users.document(userId);
                    Query qry = users.whereEqualTo("uid", userId);
                    qry.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    User user = document.toObject(User.class);
                                    print_room(user.getRooms());
                                    Log.d("GET", document.getId() + " => " + user.toString());
                                }
                            } else {
                                Log.d("GET", "Error getting documents: ", task.getException());
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

    public void print_room(List<String> roomIds) {
        mFirestore.collection("rooms_test")
                .whereIn(FieldPath.documentId(), roomIds)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Convert the document to a custom object, if needed
                                Room room = document.toObject(Room.class);  // Room is a custom class
                                room.setUid(document.getId());
                                roomsAdapter.add(room);
                                // Do something with the retrieved data
                                Log.d("Firebase", "Room data: " + room);
                            }
                        } else {
                            Log.d("Firebase", "Query failed with ", task.getException());
                        }
                    }
                });
    }
}