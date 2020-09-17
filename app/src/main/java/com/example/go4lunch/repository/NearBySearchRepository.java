package com.example.go4lunch.repository;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.httpRequest.NearbySearchServices;
import com.example.go4lunch.httpRequest.RetrofitBuilder;
import com.example.go4lunch.models.nearbySearch.NearbySearch;
import com.example.go4lunch.models.nearbySearch.Result;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.go4lunch.httpRequest.RetrofitBuilder.logging;

public class NearBySearchRepository {


    private MutableLiveData<List<Result>>  results = new MutableLiveData<>();
    private static NearBySearchRepository newsRepository;

    private static NearbySearchServices myInterface;

    public static NearBySearchRepository getInstance(){
        if (newsRepository == null){
            newsRepository = new NearBySearchRepository();
        }
        return newsRepository;
    }
    public NearBySearchRepository(){
        myInterface = RetrofitBuilder.retrofit.create(NearbySearchServices.class);
    }


    public MutableLiveData<List<Result>> getRestaurantsList(double latitude, double longitude){

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);




        String latLng = latitude + "," + longitude;


        Call<NearbySearch> resultsOut = myInterface.getResults(latLng);
        resultsOut.enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearch> call, @NonNull Response<NearbySearch> response) {
                results.setValue(response.body().getResults());
            }
            @Override
            public void onFailure(@NonNull Call<NearbySearch> call, @NonNull Throwable t) {
                results.postValue(null);
            }
        });
        return results;
    }


}
