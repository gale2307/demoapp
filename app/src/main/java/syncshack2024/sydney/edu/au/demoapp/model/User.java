package syncshack2024.sydney.edu.au.demoapp.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * User POJO.
 */
@IgnoreExtraProperties
public class User {
    private String uid;
    private ArrayList<String> rooms;

    public User() {}

    public User(String uid, ArrayList<String> rooms) {
        this.uid = uid;
        this.rooms = rooms;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<String> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", rooms=" + rooms +
                '}';
    }
}
