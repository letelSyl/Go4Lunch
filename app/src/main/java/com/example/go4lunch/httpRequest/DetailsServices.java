package com.example.go4lunch.httpRequest;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.models.details.Details;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DetailsServices {
    @GET("details/json?categories=basic&key=" + BuildConfig.API_KEY)
    Call<Details> getResult(@Query("place_id") String placeId);

    Retrofit retrofit = RetrofitBuilder.retrofit;
}
