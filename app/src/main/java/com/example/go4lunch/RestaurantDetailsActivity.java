package com.example.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.fragments.WorkmateFragmentRecyclerViewAdapter;
import com.example.go4lunch.models.User.User;
import com.example.go4lunch.models.details.Details;
import com.example.go4lunch.models.details.Result;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsActivity extends AppCompatActivity {


    private ActivityRestaurantDetailsBinding binding;

    private Disposable disposable;

    private DetailsViewModel detailsViewModel;
    private MutableLiveData<Details> mutableLiveData;
    private UsersViewModel usersViewModel;



    private View mView;

    private String picUrl;
    private String restName;


    private RecyclerView mRecyclerView;

    public static final String PREFS= "PREFS";
    private static String IS_CLICKED = "IS_CLICKED";
    private static String IS_LIKED = "IS_LIKED";

    private static String RESTAURANT_NAME = "RESTAURANT_NAME";
    private static String RESTAURANT_ID = "RESTAURANT_ID";
    SharedPreferences prefs;

    private List<User> mUsers = new ArrayList<>();

    private FirebaseFirestore firebaseFirestore;
    @NonNull
    private WorkmateFragmentRecyclerViewAdapter adapter;
    private int mColumnCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        mView = binding.getRoot();

        setContentView(mView);

        prefs = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        mRecyclerView = findViewById(R.id.list);

        Intent intent = getIntent();

        String placeId = intent.getStringExtra("placeId");

        detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
        detailsViewModel.getDetails(placeId).observe(this, result -> {

            if (result != null) {
                updateWithRestaurantDetail(result);

                usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);
                usersViewModel.getFilteredListOfUsers(restName).observe(this, users ->{

                    if (users != null){
                        updateUIWithUsers(users);
                    }
                });
            }
        });

        // Set the adapter
      if (mRecyclerView != null) {
            Context context = mView.getContext();
            RecyclerView recyclerView = mRecyclerView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.adapter = new WorkmateFragmentRecyclerViewAdapter(this.mUsers);
            recyclerView.setAdapter(this.adapter);
        }

        assert mRecyclerView != null;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
        firebaseFirestore = FirebaseFirestore.getInstance();



    }

    @Override
    protected void onStop() {
        super.onStop();
        //   adapter.stopListening();
    }

    private void initCallButton(Result result){

        binding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (result.getInternationalPhoneNumber() != null) {

                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + result.getInternationalPhoneNumber())));
                } else {
                    Toast.makeText(RestaurantDetailsActivity.this, R.string.no_phone_number, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void initLikeButton(Result result){
        binding.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserHelper.updateLikedRestaurants(result.getPlaceId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                Toast.makeText(RestaurantDetailsActivity.this, R.string.add_a_like, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void initWebsiteButton(Result result){
        binding.websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.getWebsite() != null) {

                    Intent websiteView = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getWebsite()));

                    startActivity(websiteView);
                } else {
                    Toast.makeText(RestaurantDetailsActivity.this, R.string.no_website, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initFloatingActionButton(Result result){
        FloatingActionButton floatingActionButton = binding.floatingActionButton;
        boolean isClicked;
        if (prefs.contains(IS_CLICKED))
        {
            isClicked = prefs.getBoolean("IS_CLICKED", false);
        }else {
            isClicked = false;
        }
        if (isClicked && prefs.getString(RESTAURANT_NAME, "").equals(result.getName()))
        {
            floatingActionButton.setImageResource(R.drawable.ic_offline_pin_green_24dp);
        }else{
            floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
        }



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (!isClicked) {
                    floatingActionButton.setImageResource(R.drawable.ic_offline_pin_green_24dp);
                    UserHelper.updateRestName(result.getName(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                    UserHelper.updateRestId(result.getPlaceId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                    prefs.edit().putBoolean("IS_CLICKED", true).apply();
                    prefs.edit().putString("RESTAURANT_NAME", result.getName()).apply();
                    prefs.edit().putString("RESTAURANT_ID", result.getPlaceId()).apply();


                }else {
                    floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
                    UserHelper.updateRestName("", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    UserHelper.updateRestId("", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    prefs.edit().putBoolean("IS_CLICKED", false).apply();
                    prefs.edit().putString("RESTAURANT_NAME", "").apply();
                    prefs.edit().putString("RESTAURANT_ID", "").apply();

                }
            }});
    }

    private void updateWithRestaurantDetail(Result result) {


        restName = result.getName();

        ActivityRestaurantDetailsBinding.bind(mView).name.setText(result.getName());
        ActivityRestaurantDetailsBinding.bind(mView).ratingBar2.setRating(result.getRating().byteValue() * 3 / 5);
        ActivityRestaurantDetailsBinding.bind(mView).address.setText(result.getFormattedAddress());
        if (result.getPhotos() != null && result.getPhotos().size() != 0) {
            this.picUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + result.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY;

            Glide.with(mView.getContext()).load(picUrl).into(binding.bind(mView).photo);


        }

        initCallButton(result);

        initLikeButton(result);

        initWebsiteButton(result);

        initFloatingActionButton(result);

    }



    public void updateUIWithUsers(List<User> users) {
        this.mUsers.clear();
        this.mUsers.addAll(users);
        this.adapter.notifyDataSetChanged();
    }
}
