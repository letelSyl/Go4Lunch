package com.example.go4lunch.fragments.mapFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.activities.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.activities.RestaurantDetailsActivity;
import com.example.go4lunch.eventBus.MessageEvent;
import com.example.go4lunch.viewModels.RestaurantsViewModel;
import com.example.go4lunch.models.nearbySearch.Result;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;

    LocationManager locationManager = null;
    FusedLocationProviderClient providerClient;

    private LocationListener locationListener;
    private double latitude;
    private double longitude;

    private RestaurantsViewModel restaurantsViewModel;

    private List<Result> mResults = new ArrayList<>();
    private Map<Marker, Result> allMarkersMap = new HashMap<Marker, Result>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        providerClient = LocationServices.getFusedLocationProviderClient(getContext());


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.hungry));

        restaurantsViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsViewModel.class);


        mView = inflater.inflate(R.layout.map_fragment, container, false);

        mMapView = mView.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        locationManager = (LocationManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            initMap();
        } else {
            String locationProviders = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (locationProviders == null || locationProviders.equals("")) {
                alert();
            }
        }

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMap();


    }


    public void initMap() {
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    public void alert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
        builder.setTitle(R.string.alert)
                .setMessage(R.string.auth_gps)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
// do nothing
                })
                .show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;


        latitude = MainActivity.getLatitude();
        longitude = MainActivity.getLongitude();


        locationListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng l = new LatLng(latitude, longitude);
                float zoom = googleMap.getCameraPosition().zoom;
                if (zoom < 15) {
                    zoom = 15;
                }
                CameraPosition cameraPosition = new CameraPosition.Builder().target(l).zoom(zoom).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        };

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5, locationListener);


        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

       restaurantsViewModel.getListOfRestaurants().observe(this, results -> {

            if (results != null) {
                mGoogleMap.clear();
                for (Result result : results) {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()))
                            .title(result.getName()));
                            marker.setIcon(BitmapDescriptorFactory.fromResource(result.getNumUsers() > 0 ? R.drawable.green_dot : R.drawable.red_dot));
                    allMarkersMap.put(marker, result);
                }
            }
        });

        mGoogleMap.setOnMarkerClickListener(this);

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Result result = allMarkersMap.get(marker);
        String markerId = result.getPlaceId();
        Intent details = new Intent(getContext(), RestaurantDetailsActivity.class);
        details.putExtra("placeId", markerId);
        getContext().startActivity(details);

        return false;
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event){
        Toast.makeText(getActivity(), event.message.getName(), Toast.LENGTH_SHORT).show();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(event.message.getLatLng())
                .zoom(20)
                .build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
