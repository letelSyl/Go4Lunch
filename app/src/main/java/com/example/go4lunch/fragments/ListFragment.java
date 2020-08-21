package com.example.go4lunch.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ListFragmentBinding;
import com.example.go4lunch.databinding.ListFragmentItemListBinding;
import com.example.go4lunch.httpRequest.NearbySearchStream;
import com.example.go4lunch.models.NearbySearch;
import com.example.go4lunch.models.Result;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.example.go4lunch.httpRequest.RetrofitBuilder.logging;

/**
 * A fragment representing a list of Items.
 */
public class ListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private ListFragmentBinding listFragmentBinding;
    private ListFragmentItemListBinding itemListBinding;

    // Create keys for our Bundle
    private static final String KEY_POSITION = "position";
    private int position;

    private Disposable disposable;

    private List<Result> mResults = new ArrayList<>();
    private ListFragmentRecyclerViewAdapter mAdapter;

    private double latitude;
    private double longitude;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ListFragment newInstance(int columnCount) {

        ListFragment fragment = new ListFragment();


       Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        this.listFragmentBinding = ListFragmentBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.list_fragment_item_list, container, false);

      // position= getArguments().getInt(KEY_POSITION,-1);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        this.executeHttpRequest();
        //this.configureRecyclerView();+


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
           } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.mAdapter = new ListFragmentRecyclerViewAdapter(this.mResults, this.latitude, this.longitude);
            recyclerView.setAdapter(this.mAdapter);
        }




        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }
/*
    private void configureRecyclerView(){

            this.mAdapter = new ListFragmentRecyclerViewAdapter(this.mResults);
         itemListBinding.list.setAdapter(this.mAdapter);
        }
*/

    private void executeHttpRequest(){

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        LocationManager  locationManager = (LocationManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);



        String latLng;

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            int REQUEST_LOCATION = 1;
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {

            Criteria criteria = new Criteria();

            assert locationManager != null;
            Location location = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager
                    .getBestProvider(criteria, false)));

            assert location != null;
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            latLng = latitude+","+longitude;


            this.disposable = NearbySearchStream.streamFetchNearbySearch(latLng)
                    .subscribeWith(new DisposableObserver<NearbySearch>() {

                        @Override
                        public void onNext(NearbySearch nearbySearch) {
                            Log.e("TAG", "On Next");
                            updateUIWithNearbySearch(nearbySearch.getResults());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG", "On Error" + Log.getStackTraceString(e));
                        }

                        @Override
                        public void onComplete() {
                            Log.e("TAG", "On Complete !!");
                        }

                    });
        }
    }


    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }


    private void updateUIWithNearbySearch(List<Result> results){
        this.mResults.addAll(results);
        this.mAdapter.notifyDataSetChanged();
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

}
