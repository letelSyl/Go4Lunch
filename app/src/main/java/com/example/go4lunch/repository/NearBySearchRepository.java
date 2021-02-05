package com.example.go4lunch.repository;

import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.httpRequest.NearbySearchServices;
import com.example.go4lunch.httpRequest.RetrofitBuilder;
import com.example.go4lunch.models.nearbySearch.NearbySearch;
import com.example.go4lunch.models.nearbySearch.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.go4lunch.httpRequest.RetrofitBuilder.logging;

public class NearBySearchRepository {


    private MutableLiveData<List<Result>> results = new MutableLiveData<>();
    private static NearBySearchRepository nearBySearchRepository;
    private static FirestoreRepository firestoreRepository;

    private static NearbySearchServices myInterface;


    private List<Result> resultsList = new ArrayList<>();

    public static NearBySearchRepository getInstance() {
        if (nearBySearchRepository == null) {
            nearBySearchRepository = new NearBySearchRepository();
        }
        return nearBySearchRepository;
    }

    public NearBySearchRepository() {
        myInterface = RetrofitBuilder.retrofit.create(NearbySearchServices.class);
    }

    public void increaseResultsNumUsers(String currentRestId){
        for (Result result : resultsList){
            if (result.getPlaceId().equals(currentRestId)){
                result.setNumUsers(result.getNumUsers() + 1);
            }
        }
        results.setValue(resultsList);
    }

    public void decreaseResultsNumUsers(String currentRestId){

        for (Result result : resultsList){
            if (result.getPlaceId().equals(currentRestId)){
                result.setNumUsers(result.getNumUsers() - 1);
            }
        }
        results.setValue(resultsList);

    }

    public MutableLiveData<List<Result>> getRestaurantsList(double latitude, double longitude) {

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        String latLng = latitude + "," + longitude;


        Call<NearbySearch> resultsOut = myInterface.getResults(latLng);
        resultsOut.enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearch> call, @NonNull Response<NearbySearch> response) {

                if (response.body() != null) {
                    for (Result result : response.body().getResults()) {
                        Task<QuerySnapshot> querySnapshotTask = UserHelper.getUsersWithRestname(result.getName()).get();
                        querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    result.setNumUsers(task.getResult().size());
                                    resultsList.add(result);
                                    if (resultsList.size() == response.body().getResults().size()) {
                                        results.setValue(resultsList);
                                    }
                                }
                            }
                        });

                    }

                }
            }


            @Override
            public void onFailure(@NonNull Call<NearbySearch> call, @NonNull Throwable t) {
                results.postValue(null);
            }


        });

        return results;
    }


}
