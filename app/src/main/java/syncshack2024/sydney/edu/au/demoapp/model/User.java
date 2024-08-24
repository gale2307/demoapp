package syncshack2024.sydney.edu.au.demoapp.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

/**
 * User POJO.
 */
@IgnoreExtraProperties
public class User {
    private String uid;
    private List<String> rooms;

    public User() {}

    public User(String uid, List<String> rooms) {
        this.uid = uid;
        this.rooms = rooms;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
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
