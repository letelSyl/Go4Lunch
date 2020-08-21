package com.example.go4lunch.httpRequest;

import com.example.go4lunch.models.NearbySearch;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NearbySearchStream {

    public static Observable<NearbySearch> streamFetchNearbySearch(String latLng) {

        NearbySearchServices nearbySearchServices = NearbySearchServices.retrofit.create(NearbySearchServices.class);
        return nearbySearchServices.getResults(latLng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);

    }
}
