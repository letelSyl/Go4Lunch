package com.example.go4lunch;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.databinding.ListFragmentBinding;
import com.example.go4lunch.fragments.ListFragment;
import com.example.go4lunch.fragments.MapFragment;
import com.example.go4lunch.fragments.WorkmateFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;


    private MaterialToolbar toolbar;

    private BottomNavigationView bottomNavigationView;

    private FrameLayout mapFragmentBinding;
    private ListFragmentBinding listFragmentBinding;

//  private ActionBar ab;
    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;

    // 1 - Identifier for Sign-in Activity
    private static final int RC_SIGN_IN = 123;

    private static final int SIGN_OUT_TASK = 10;

    private MapFragment mMapFragment = new MapFragment();
    private ListFragment mListFragment = new ListFragment();
    private WorkmateFragment mWorkmateFragment = new WorkmateFragment();

    private static double latitude;
    private static double longitude;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
    @Override
    protected void onStart() {

        super.onStart();
        if(isCurrentUserLogged()){

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            View view = binding.getRoot();

            setContentView(view);

            this.configureToolbar();

            this.configureFragment();

            this.configureDrawerLayout();

            this.configureNavigationView();

            this.updateUIWhenStarting();

            this.configureBottomView();
        } else{
            this.startSignInActivity();

        }

        getCurrentLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void configureToolbar(){

        this.toolbar = binding.toolbar.toolbar;
        setSupportActionBar(toolbar);

    }

    protected void configureFragment(){

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyAkMT8gS5CHox_UV6NpVJ7NQa2q9R00qFw");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

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
            case R.id.your_lunch:
                Toast.makeText(getApplicationContext(), "YOUR LUNCH PRESSED !",  Toast.LENGTH_LONG).show();
                return true;

            case R.id.settings:
                Toast.makeText(getApplicationContext(), "SETTING PRESSED ! ",  Toast.LENGTH_LONG).show();
                return true;

            case R.id.logout:
                signOutUserFromFirebase();
                this.startSignInActivity();
                return true;

            default:
                return false;
        }
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
        }
    }

    //-----------------
    //NAVIGATION
    //-----------------

    // Launch Sign-In Activity
    private void startSignInActivity() {

        List<AuthUI.IdpConfig> providers =Arrays.asList(
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
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
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
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }



    }

    public static double getLatitude(){
        return latitude;
    }

    public static double getLongitude(){
        return longitude;
    }

}
