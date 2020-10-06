package com.example.go4lunch.firestore;


import com.example.go4lunch.models.User.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserHelper {

    private static final String COLLECTION_NAME = "Workmates";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String name, String uid, String urlPicture) {
        User userToCreate = new User(name, uid, urlPicture);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String name, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("name", name);
    }



    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
