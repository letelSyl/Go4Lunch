package com.example.go4lunch.viewModels;

import com.example.go4lunch.models.User.User;
import com.example.go4lunch.repository.FirestoreRepository;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UsersViewModel extends ViewModel {

    private final FirestoreRepository repository;

    private MutableLiveData<List<User>> listOfUsers = new MutableLiveData<>();




    public UsersViewModel(){
        super();
        repository = new FirestoreRepository();
    }

    public MutableLiveData<List<User>> getListOfUser(){
        listOfUsers = loadUserData();
        return listOfUsers;
    }

    public MutableLiveData<List<User>> getFilteredListOfUsers(String restName){
        return loadFilteredUserData(restName);
    }

    private MutableLiveData<List<User>> loadUserData(){

        return repository.getUserList();
    }

    private MutableLiveData<List<User>> loadFilteredUserData(String restName){

        return repository.getFilteredUserList(restName);
    }

}
