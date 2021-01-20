package com.example.go4lunch;

import com.example.go4lunch.models.User.User;
import com.example.go4lunch.models.nearbySearch.Result;
import com.example.go4lunch.repository.FirestoreRepository;
import com.example.go4lunch.repository.NearBySearchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RestaurantsViewModel extends ViewModel implements LifecycleObserver {

    private final NearBySearchRepository nbsRepository;

    private final FirestoreRepository firestoreRepository;

    private MutableLiveData<List<Result>> listOfRestaurants;


    public RestaurantsViewModel() {
        super();
        nbsRepository = new NearBySearchRepository();
        firestoreRepository = new FirestoreRepository();
        listOfRestaurants = new MutableLiveData<>();
    }

    public void getNearBySearchRepository(double latitude, double longitude) {
        listOfRestaurants = nbsRepository.getRestaurantsList(latitude, longitude);
    }

    public MutableLiveData<List<User>> getListOfUsers(String restName) {
        return firestoreRepository.getFilteredUserList(restName);
    }

    public MutableLiveData<List<Result>> getListOfRestaurants() {
        return listOfRestaurants;
    }


}
