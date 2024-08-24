package syncshack2024.sydney.edu.au.demoapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.concurrent.CompletableFuture;

import syncshack2024.sydney.edu.au.demoapp.model.Room;
import syncshack2024.sydney.edu.au.demoapp.util.FirestoreUtil;


// RoomDetailsActivity
public class RoomDetailsActivity extends AppCompatActivity {

    private TextView textView;
    private FirebaseFirestore mFirestore;
    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        textView = findViewById(R.id.textView1);
        mFirestore = FirestoreUtil.getFirestoreInstance();

        room = new Room();
        readItemsFromDatabase();

//        Log.d("test shit", room.toString());

//        if (room != null) {
//            textView.setText(room.getTitle());
//        }

    }

    private void readItemsFromDatabase() {
        //Use asynchronous task to run query on the background and wait for result
        try {
            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    //read items from database
                    CollectionReference rooms = mFirestore.collection("rooms_test");
                    DocumentReference docRef = rooms.document("N9OOfTdgIp18O4xEtkcx");
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    room = document.toObject(Room.class);
                                    if (room != null) {
                                        textView.setText(room.getTitle());

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