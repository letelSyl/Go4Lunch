package com.example.go4lunch.httpRequest;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.example.go4lunch.models.NearbySearch;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import io.reactivex.Observable;

import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NearbySearchServices {

    @GET("nearbysearch/json?radius=5000&type=restaurant&keyword=cruise&key=AIzaSyAkMT8gS5CHox_UV6NpVJ7NQa2q9R00qFw")
    Observable<NearbySearch> getResults(@Query("location") String latLng);

    Retrofit retrofit = RetrofitBuilder.retrofit;
}
