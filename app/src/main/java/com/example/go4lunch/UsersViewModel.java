package com.example.go4lunch;

import com.example.go4lunch.models.User.User;
import com.example.go4lunch.repository.FirestoreRepository;
import com.google.firebase.firestore.CollectionReference;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UsersViewModel extends ViewModel {

    private final FirestoreRepository repository;

    private MutableLiveData<List<User>> listOfUsers = new MutableLiveData<>();




    public UsersViewModel(){
        super();
        repository = new FirestoreRepository();
    }

    public MutableLiveData<List<User>> getUserRepository(){
        listOfUsers = loadUserData();
        return listOfUsers;
    }

    private MutableLiveData<List<User>> loadUserData(){

        return repository.getUserList();
    }
}
