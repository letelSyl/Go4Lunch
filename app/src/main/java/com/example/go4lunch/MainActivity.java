package com.example.go4lunch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.firestore.CurrentUser;
import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.fragments.ListFragment;
import com.example.go4lunch.fragments.MapFragment;
import com.example.go4lunch.fragments.WorkmateFragment;
import com.example.go4lunch.httpRequest.NearbySearchServices;
import com.example.go4lunch.models.User.User;
import com.example.go4lunch.models.nearbySearch.NearbySearch;
import com.example.go4lunch.models.nearbySearch.Result;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import retrofit2.Call;
import retrofit2.http.QueryMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;




    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    private MaterialToolbar toolbar;

    private BottomNavigationView bottomNavigationView;

    private  RestaurantsViewModel restaurantsViewModel;



    private DrawerLayout drawerLayout;


    // 1 - Identifier for Sign-in Activity
    private static final int RC_SIGN_IN = 123;

    private static final int SIGN_OUT_TASK = 10;

    private MapFragment mMapFragment = new MapFragment();
    private ListFragment mListFragment = new ListFragment();
    private WorkmateFragment mWorkmateFragment = new WorkmateFragment();

    private static double latitude;//
                                     // à metre dans le view model*/
    private static double longitude;//

    private static double latLng;
    private MutableLiveData<List<Result>> listOfRestaurants;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(isCurrentUserLogged()){

            UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    CurrentUser.set_instance(currentUser);
                }
            });
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();

            setContentView(view);

            this.configureToolbar();

            restaurantsViewModel = new ViewModelProvider(this).get(RestaurantsViewModel.class);


            this.configureDrawerLayout();

            this.configureNavigationView();

            this.updateUIWhenStarting();

            this.configureBottomView();

            getCurrentLocation();

            setupRestaurantList(latitude, longitude);

            this.configureFragment();
        } else{
            this.startSignInActivity();

        }
    }

    public void setupRestaurantList(double latitude, double longitude){
       restaurantsViewModel.
               getNearBySearchRepository(latitude, longitude);
    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void configureToolbar(){

        this.toolbar = binding.toolbar.toolbar;
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //1 - inflate the menu and add it to the toolbar
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //2 - Handle actions on menu items
        if (itemSwitch(item.getItemId())) {

            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void configureFragment(){

        // Initialize the SDK
        Places.initialize(getApplicationContext(),BuildConfig.API_KEY);

        // Create a new PlacesClient instance
      //  PlacesClient placesClient = Places.createClient(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mMapFragment).commit();
    }

    // 2 - Configure BottomNavigationView Listener
    private void configureBottomView(){
        bottomNavigationView = binding.activityMainBottomNavigation;
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return MainActivity.this.updateMainFragment(item.getItemId());
            }
        });

    }


    @Override
    public void onBackPressed() {
    // Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        itemSwitch(item.getItemId());

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = binding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();




    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
       // this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);

     binding.activityMainNavView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

    }

    //----------------------------------
    // UI
    //----------------------------------

    private boolean itemSwitch(int itemId) {

        switch (itemId) {
            case R.id.search:

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ID);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


                return true;
            case R.id.your_lunch:
                Toast.makeText(getApplicationContext(), R.string.lunch_pressed,  Toast.LENGTH_LONG).show();
                return true;

            case R.id.settings:
                Toast.makeText(getApplicationContext(), R.string.setting_pressed,  Toast.LENGTH_LONG).show();
                return true;

            case R.id.logout:
                signOutUserFromFirebase();
                this.startSignInActivity();
                return true;

            default:
                return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Intent details = new Intent (this, RestaurantDetailsActivity.class);
                details.putExtra("placeId", place.getId());
                this.startActivity(details);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean updateMainFragment(Integer integer){
        switch (integer){
            case R.id.action_map_view:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mMapFragment).commit();

                break;
            case R.id.action_list_view:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mListFragment).commit();

                break;
            case R.id.action_workmates:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mWorkmateFragment).commit();
                break;

        }
        return true;
    }

    private void updateUIWhenStarting(){

       NavigationView navView =  binding.activityMainNavView;
       ImageView img =navView.getHeaderView(0).findViewById(R.id.profile_img);


        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(img);
            } else{
                Glide.with(this)
                        .load(R.drawable.ic_person_black_24dp)
                        .apply(RequestOptions.circleCropTransform())
                        .into(img);

            }

            //Get email & username from Firebase
            TextView userEmail = navView.getHeaderView(0).findViewById(R.id.usermail);
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            TextView userName = navView.getHeaderView(0).findViewById(R.id.username);
            String username =  TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            userName.setText(username);
            userEmail.setText(email);

           createUserInFirestore();
        }


    }

    //-----------------
    //NAVIGATION
    //-----------------

    // Launch Sign-In Activity
    private void startSignInActivity() {

        List<AuthUI.IdpConfig> providers =Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.logo_go4lunch)
                        .build(),
                RC_SIGN_IN);

    }



    // --------------------
    // UTILS
    // --------------------

    @Nullable
    protected static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }



    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),getString(R.string.error_unknown_error),Toast.LENGTH_LONG).show();
            }
        };
    }

    private void createUserInFirestore(){

      //  if (getCurrentUser() != null){
        if (UserHelper.getUser(getCurrentUser().getUid()) == null){
            String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String name = getCurrentUser().getDisplayName();
            String uid = getCurrentUser().getUid();
            String restName="";
            String restId="";
            ArrayList<String> likedRestaurant = new ArrayList<>();

            UserHelper.createUser(uid, name,  urlPicture, restName,restId, likedRestaurant).addOnFailureListener(this.onFailureListener());
        }
    }



    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    // h - Hiding Progress bar after request completed

                    case SIGN_OUT_TASK:
                        onRestart();
                        break;

                    default:
                        break;
                }
            }
        };
    }


    public void getCurrentLocation() {

        LocationManager  locationManager = (LocationManager)
                Objects.requireNonNull(this).getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_LOCATION = 1;
            ActivityCompat.requestPermissions(Objects.requireNonNull(this),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
// For showing a move to my location button

            Criteria criteria = new Criteria();
            android.location.Location location = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager
                    .getBestProvider(criteria, false)));
            if (location !=  null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }



    }




//-----------------TODO:Passer par le view model----------

    public static double getLatitude(){
        return latitude;
    }

    public static double getLongitude(){

        return longitude;
    }
/*
    public static MutableLiveData<List<Result>> getRestaurantsList(){
        return restaurantsViewModel.getListOfRestaurants();
    }
    public static RestaurantsViewModel getRestaurantsViewModel(){ return restaurantsViewModel; }

 */
}
