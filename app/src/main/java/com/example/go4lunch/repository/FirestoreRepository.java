package com.example.go4lunch.repository;

import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.models.User.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class FirestoreRepository {

    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    public MutableLiveData<List<User>> getUserList(){
        CollectionReference usersCollection = UserHelper.getUsersCollection();
        usersCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> list = new ArrayList<>();
                if(task.getResult() != null) {
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        list.add(documentSnapshot.toObject(User.class));


                    }
                    users.setValue(list);
                }
            }
        });
        return users;


    }

}
