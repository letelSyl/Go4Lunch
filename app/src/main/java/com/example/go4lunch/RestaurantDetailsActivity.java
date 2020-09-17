package com.example.go4lunch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.logging.HttpLoggingInterceptor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.example.go4lunch.fragments.WorkmateFragmentRecyclerViewAdapter;
import com.example.go4lunch.fragments.dummy.WorkmateFragmentDummyContent;
import com.example.go4lunch.httpRequest.DetailsStream;
import com.example.go4lunch.models.details.Details;
import com.example.go4lunch.models.details.Result;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

                if (result.getInternationalPhoneNumber() != null) {

                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + result.getInternationalPhoneNumber())));
                } else {
                    Toast.makeText(RestaurantDetailsActivity.this, "No phone number registered !", Toast.LENGTH_LONG).show();

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
            FloatingActionButton floatingActionButton = binding.floatingActionButton;
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
            boolean isClicked = false;
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (!isClicked) {
                    floatingActionButton.setImageResource(R.drawable.ic_offline_pin_green_24dp);
                    isClicked = true;
                }else {
                    floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                    isClicked = false;
                }
            }
        });
    }


}
