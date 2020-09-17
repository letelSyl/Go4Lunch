package com.example.go4lunch.httpRequest;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.models.nearbySearch.NearbySearch;
import com.example.go4lunch.models.nearbySearch.Result;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbySearchServices {

    @GET("nearbysearch/json?rankby=distance&type=restaurant&keyword=cruise&key=" + BuildConfig.API_KEY)
    Call<NearbySearch> getResults(@Query("location") String latLng);

    Retrofit retrofit = RetrofitBuilder.retrofit;
}
