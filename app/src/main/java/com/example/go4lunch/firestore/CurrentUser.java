package com.example.go4lunch.firestore;

import com.example.go4lunch.models.User.User;
import com.google.common.base.MoreObjects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class CurrentUser extends User {

    private static User _instance = null;


    //TODO : setvalue  instance = current user
    public static void set_instance(User currentUser){
        _instance = currentUser;
    }

    public static User getInstance(){


        return _instance;
    }

}
