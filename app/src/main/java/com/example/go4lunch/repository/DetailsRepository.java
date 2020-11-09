package com.example.go4lunch.repository;

import com.example.go4lunch.httpRequest.DetailsServices;
import com.example.go4lunch.httpRequest.RetrofitBuilder;
import com.example.go4lunch.models.details.Details;
import com.example.go4lunch.models.details.Result;

import static com.example.go4lunch.httpRequest.RetrofitBuilder.logging;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsRepository{

    private MutableLiveData<Result> results = new MutableLiveData<Result>();
    private static DetailsRepository detailsRepository;
    private static DetailsServices myInterface;

    public DetailsRepository(){
        myInterface = RetrofitBuilder.retrofit.create(DetailsServices.class);
    }

    public static DetailsRepository getInstance(){
        if (detailsRepository == null){
            detailsRepository = new DetailsRepository();
        }
        return detailsRepository;
    }

    public MutableLiveData<Result> getDetails(String placeId){

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        Call<Details> resultsOut = myInterface.getResult(placeId);
        resultsOut.enqueue(new Callback<Details>() {
            @Override
            public void onResponse(@NonNull Call<Details> call, @NonNull Response<Details> response) {
                assert response.body() != null;
                results.setValue(response.body().getResult());
            }
            @Override
            public void onFailure(Call<Details> call, Throwable t) {
                results.postValue(null);
            }
        });
        return results;
    }



}
