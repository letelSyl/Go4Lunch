package com.example.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.logging.HttpLoggingInterceptor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.example.go4lunch.databinding.ListFragmentBinding;
import com.example.go4lunch.fragments.WorkmateFragmentRecyclerViewAdapter;
import com.example.go4lunch.fragments.dummy.WorkmateFragmentDummyContent;
import com.example.go4lunch.httpRequest.DetailsStream;
import com.example.go4lunch.httpRequest.NearbySearchStream;
import com.example.go4lunch.models.details.Details;
import com.example.go4lunch.models.details.Result;
import com.example.go4lunch.models.nearbySearch.NearbySearch;

import java.util.Objects;

import static com.example.go4lunch.httpRequest.RetrofitBuilder.logging;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private ActivityRestaurantDetailsBinding binding;

    private Disposable disposable;

    private ViewModel mViewModel;

    private View mView;

    private String picUrl;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        mView = binding.getRoot();

        setContentView(mView);


        mRecyclerView = findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set the adapter
        if (mRecyclerView instanceof RecyclerView) {
            Context context = mRecyclerView.getContext();
            RecyclerView recyclerView = mRecyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new WorkmateFragmentRecyclerViewAdapter(WorkmateFragmentDummyContent.ITEMS));
        }

        executeHttpRequest();

    }


    protected void executeHttpRequest() {

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Intent intent = getIntent();

        String placeId = intent.getStringExtra("placeId");


        this.disposable = DetailsStream.streamFetchDetails(placeId)
                .subscribeWith(new DisposableObserver<Details>() {

                    @Override
                    public void onNext(Details details) {
                        Log.e("TAG", "On Next");
                        updateWithRestaurantDetail(details.getResult());

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

    private void updateWithRestaurantDetail(Result result) {

        binding.bind(mView).name.setText(result.getName());
        binding.bind(mView).ratingBar2.setRating(result.getRating().byteValue() * 3 / 5);
        binding.bind(mView).address.setText(result.getFormattedAddress());
        if (result.getPhotos() != null && result.getPhotos().size() != 0) {
            this.picUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + result.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY;

            Glide.with(mView.getContext()).load(picUrl).into(binding.bind(mView).photo);


        }

        binding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                int REQUEST_CALL = 1;
                ActivityCompat.requestPermissions(Objects.requireNonNull(RestaurantDetailsActivity.this),
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CALL);
                }else {
                    if (result.getInternationalPhoneNumber() != null) {

                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + result.getInternationalPhoneNumber())));
                    } else {
                        Toast.makeText(RestaurantDetailsActivity.this, "No phone number registered !", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

        binding.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RestaurantDetailsActivity.this, "You've just add a like !", Toast.LENGTH_LONG).show();

            }
        });

        binding.websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.getWebsite() != null) {

                    Intent websiteView = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getWebsite()));

                    startActivity(websiteView);
                } else {
                    Toast.makeText(RestaurantDetailsActivity.this, "Website doesn't exist !", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RestaurantDetailsActivity.this, "You've choose this restaurant",Toast.LENGTH_LONG).show();
            }
        });
    }


}
