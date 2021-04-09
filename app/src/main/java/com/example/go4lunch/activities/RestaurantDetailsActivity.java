package com.example.go4lunch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.example.go4lunch.firestore.CurrentUser;
import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.fragments.workmatesFragment.WorkmateFragmentRecyclerViewAdapter;
import com.example.go4lunch.models.User.User;
import com.example.go4lunch.models.details.Details;
import com.example.go4lunch.models.details.Result;
import com.example.go4lunch.viewModels.DetailsViewModel;
import com.example.go4lunch.viewModels.UsersViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;

/**
 * <b>Show details of selected restaurant</b>
 *
 * @see DetailsViewModel
 * @see UsersViewModel
 *
 * @author letelSyl
 * @version 1.0
 */
public class RestaurantDetailsActivity extends AppCompatActivity {


    private ActivityRestaurantDetailsBinding binding;

    private DetailsViewModel detailsViewModel;
    private UsersViewModel usersViewModel;



    private View mView;

    private String picUrl;
    private String restName;


    private RecyclerView mRecyclerView;

    private List<User> mUsers = new ArrayList<>();

    private MaterialToolbar toolbar;

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

        this.configureToolbar();

        //prefs = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
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

    private void configureToolbar() {
        //Set the toolbar
        this.toolbar = binding.toolbar.toolbar;
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseFirestore = FirebaseFirestore.getInstance();



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * <b>Configure call button</b>
     * <p>
     *     open call application of the device
     * </p>
     *
     * @see this#updateWithRestaurantDetail(Result)
     *
     * @param result
     *      selected restaurant
     */
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

    /**
     * <b>configure like button</b>
     * <p>
     *     add selected restaurant to the user liked restaurant arryList
     * </p>
     *
     * @see this#updateWithRestaurantDetail(Result)
     *
     * @param result
     *      selected restaurant
     */
    private void initLikeButton(Result result){
        binding.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserHelper.updateLikedRestaurants(result.getPlaceId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                Toast.makeText(RestaurantDetailsActivity.this, R.string.add_a_like, Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
     * <b>Configure website button</b>
     * <p>
     *     open the restaurant's website in the device's web navigator
     * </p>
     *
     * @see this#updateWithRestaurantDetail(Result)
     *
     * @param result
     *      selected restaurant
     *
     */
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

    /**
     * <b>Configure floatingAction button</b>
     * <p>
     *     set resId ad restName of the current user with selected restauranr id and name / set them empty
     *    
     * </p>
     *
     * @see this#updateWithRestaurantDetail(Result)
     *
     * @param result
     *      selected restaurant
     *
     */
    private void initFloatingActionButton(Result result){
        FloatingActionButton floatingActionButton = binding.floatingActionButton;

        User currentUser = CurrentUser.getInstance();
        if (result.getPlaceId().equals(currentUser.getRestId()))
        {
            floatingActionButton.setImageResource(R.drawable.ic_offline_pin_green_24dp);
        }else{
            floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
        }



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
         //   @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (!result.getPlaceId().equals(currentUser.getRestId()))
                 {

                    floatingActionButton.setImageResource(R.drawable.ic_offline_pin_green_24dp);

                    detailsViewModel.updateCurrentUser(result.getName(),
                                                        result.getPlaceId(),
                                                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                    detailsViewModel.increaseResultsNumUsers(result.getPlaceId());




                }else {
                    floatingActionButton.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);


                    detailsViewModel.updateCurrentUser("", "", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    detailsViewModel.decreaseResultsNumUsers(result.getPlaceId());


                }
            }});
    }

    /**
     * <b>update the view</b>
     * <p>
     *     show details of selected restaurant and initialise buttons
     * </p>
     *
     * @see this#initCallButton(Result) 
     * @see this#initLikeButton(Result)  
     * @see this#initWebsiteButton(Result)  
     * @see this#initFloatingActionButton(Result) 
     *
     * @param result
     *      selected restaurant
     *
     */
    private void updateWithRestaurantDetail(Result result) {


        restName = result.getName();

        ActivityRestaurantDetailsBinding.bind(mView).name.setText(restName);
        ActivityRestaurantDetailsBinding.bind(mView).ratingBar2.setRating(result.getRating() != null ? result.getRating().byteValue() * 3 / 5f : 0);
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


    /**
     * <b>complete selected restaurant list of users</b>
     * <p>
     *     update the list of users with filterd users list of the view model
     * </p>
     *
     * @see UsersViewModel#getFilteredListOfUsers(String)
     *
     * @param users
     *      list of the users booked in selected restaurant
     *
     */
    public void updateUIWithUsers(List<User> users) {
        this.mUsers.clear();
        this.mUsers.addAll(users);
        this.adapter.notifyDataSetChanged();
    }
}
