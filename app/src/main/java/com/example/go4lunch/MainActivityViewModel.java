package com.example.go4lunch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;

import com.example.go4lunch.models.nearbySearch.Result;
import com.example.go4lunch.repository.NearBySearchRepository;

import java.util.List;
import java.util.Objects;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private final NearBySearchRepository repository;

    private MutableLiveData<List<Result>> listOfRestaurants = new MutableLiveData<>();




    public MainActivityViewModel(){
        super();
        repository = new NearBySearchRepository();
    }

    public MutableLiveData<List<Result>> getNearBySearchRepository(double latitude, double longitude){
        listOfRestaurants = loadRestaurantsData(latitude, longitude);
        return listOfRestaurants;
    }

    private MutableLiveData<List<Result>> loadRestaurantsData(double latitude, double longitude){
        return repository.getRestaurantsList(latitude, longitude);
    }

}
