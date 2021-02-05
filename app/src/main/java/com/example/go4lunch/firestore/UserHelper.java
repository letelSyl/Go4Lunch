package com.example.go4lunch.firestore;


import com.example.go4lunch.models.User.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class UserHelper {

    private static final String COLLECTION_NAME = "Workmates";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getUsersWithRestname(String restName){
        return getUsersCollection().whereEqualTo("restName", restName);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String name, String urlPicture, String restName, String restId, ArrayList likedRestaurants) {
        User userToCreate = new User(uid, name,  urlPicture, restName, restId, likedRestaurants);
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

    public static Task<Void> updateRestName(String restName, String uid){
        return  UserHelper.getUsersCollection().document(uid).update("restName", restName);
    }

    public static Task<Void> updateRestId(String restId, String uid){
        return  UserHelper.getUsersCollection().document(uid).update("restId", restId);
    }
    public static Task<Void> updateLikedRestaurants(String likedRestaurants, String uid){
        return UserHelper.getUsersCollection().document(uid).update("likedRestaurants", FieldValue.arrayUnion(likedRestaurants));






    }



    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
