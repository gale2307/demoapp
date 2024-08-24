package syncshack2024.sydney.edu.au.demoapp.util;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreUtil {
    // Static instance of FirebaseFirestore
    private static FirebaseFirestore mFirestore;

    // Static method to get the instance of Firestore
    public static FirebaseFirestore getFirestoreInstance() {
        if (mFirestore == null) {
            mFirestore = FirebaseFirestore.getInstance();
        }
        return mFirestore;
    }
}
