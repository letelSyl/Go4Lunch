package com.example.go4lunch.httpRequest;

import com.example.go4lunch.models.details.Details;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailsStream {

    public static Observable<Details> streamFetchDetails(String placeId) {

    DetailsServices detailsServices = DetailsServices.retrofit.create(DetailsServices.class);
    return detailsServices.getResult(placeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(10, TimeUnit.SECONDS);

}
}
