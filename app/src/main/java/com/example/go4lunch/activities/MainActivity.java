package com.example.go4lunch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.activities.chat.ChatActivity;
import com.example.go4lunch.activities.notifications.NotificationsActivity;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.eventBus.MessageEvent;
import com.example.go4lunch.firestore.CurrentUser;
import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.fragments.listFragment.ListFragment;
import com.example.go4lunch.fragments.mapFragment.MapFragment;
import com.example.go4lunch.fragments.workmatesFragment.WorkmateFragment;
import com.example.go4lunch.models.User.User;
import com.example.go4lunch.viewModels.RestaurantsViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
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
import androidx.lifecycle.ViewModelProvider;


/**
 * <b>Represents the main screen of the application</b>
 * <p>Displays differents elements
 * <ul>
 *     <li>a toolbar</li>
 *     <li>a botton navigation bar</li>
 *     <li>the selected fragment</li>
 * </ul>
 * </p>
 *
 * @see MapFragment
 * @see ListFragment
 * @see WorkmateFragment
 *
 * @author letelSyl
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;


    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    private MaterialToolbar toolbar;

    private BottomNavigationView bottomNavigationView;

    private RestaurantsViewModel restaurantsViewModel;


    private DrawerLayout drawerLayout;


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
        if (isCurrentUserLogged()) {
            this.startActivity();

        } else {
            this.startSignInActivity();

        }
    }

    /**
     * Start MainActivity
     *
     * @see this.configureToolbar()
     * @see this.configureDrawerLayout()
     * @see this.configureNavigationView()
     * @see this.updateUIWhenStarting()
     * @see this.configureBottomView()
     * @see this.getCurrentLocation()
     * @see this.setupRestaurantList(double, double)
     * @see this.configureFragment()
     */
    public void startActivity() {
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
    }

    /**
     * Generates the list of restaurants based on current coordinates
     *
     * @param latitude
     *          the current latitude
     * @param longitude
     *          the current longitude
     *
     * @see RestaurantsViewModel#getNearBySearchRepository(double, double)
     */
    public void setupRestaurantList(double latitude, double longitude) {
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

    /**
     * configure toolbar
     *
     */
    protected void configureToolbar() {

        this.toolbar = binding.toolbar.toolbar;
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (itemSwitch(item.getItemId())) {

            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Configure the selected fragment
     *
     */
    protected void configureFragment() {

        // Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.API_KEY);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mMapFragment).commit();
    }

    /**
     * Configure BottomNavigationView Listener
     *
     */
    private void configureBottomView() {
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

    /**
     * Configure Drawer Layout
     */

    private void configureDrawerLayout() {
        this.drawerLayout = binding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    /**
     * Configure NavigationView
     */
    private void configureNavigationView() {

        binding.activityMainNavView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

    }


    /**
     * Configure the actions of drawer menu buttons
     * @param itemId
     *      selected drawer menu's id
     * @return boolean
     */
    private boolean itemSwitch(int itemId) {

        switch (itemId) {
            case R.id.search:

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.NAME,
                        Place.Field.ID,
                        Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


                return true;
            case R.id.your_lunch:

                String placeId = CurrentUser.getInstance().getRestId();

                Context context = getApplicationContext();

                if (!placeId.equals("")) {
                    Intent details = new Intent(context, RestaurantDetailsActivity.class);
                    details.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    details.putExtra("placeId", placeId);
                    context.startActivity(details);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.No_restaurant_selected_yet, Toast.LENGTH_LONG).show();

                }

                return true;

            case R.id.chat:
                Intent chatIntent = new Intent(this, ChatActivity.class);
                startActivity(chatIntent);
                return true;

            case R.id.settings:
                Intent notifIntent = new Intent(this, NotificationsActivity.class);
                startActivity(notifIntent);
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
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                this.startActivity();
            }
        }
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                EventBus.getDefault().post(new MessageEvent(place));
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * <b>Fragment selection</b>
     * <p>
     *     select wich fragment showing when user click on the icon on the bottom navigation bar
     * </p>
     *
     * @param integer
     *      Selected fragment's id
     * @return boolean
     */
    private boolean updateMainFragment(Integer integer) {
        switch (integer) {
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

    /**
     * Configure the drawer menu UI
     */
    private void updateUIWhenStarting() {

        NavigationView navView = binding.activityMainNavView;
        ImageView img = navView.getHeaderView(0).findViewById(R.id.profile_img);


        if (isCurrentUserLogged()) {

            //Get picture URL from Firebase
            if (getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(img);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_person_black_24dp)
                        .apply(RequestOptions.circleCropTransform())
                        .into(img);

            }

            //Get email & username from Firebase
            TextView userEmail = navView.getHeaderView(0).findViewById(R.id.usermail);
            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : getCurrentUser().getEmail();
            TextView userName = navView.getHeaderView(0).findViewById(R.id.username);
            String username = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : getCurrentUser().getDisplayName();

            //Update views with data
            userName.setText(username);
            userEmail.setText(email);

            createUserInFirestore();
        }


    }


    /**
     * Launch Sign-In Activity
     */
    private void startSignInActivity() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
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


    /**
     * get the current user
     * <p>
     *     get from Firebase the authenticated user
     * </p>
     * @return FirebaseUser
     */
    @Nullable
    protected static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * verify if a user is logged
     * @return
     */
    protected Boolean isCurrentUserLogged() {
        return (getCurrentUser() != null);
    }

    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

    private void createUserInFirestore() {
        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(!doc.exists()){
                        String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null;
                        String name = getCurrentUser().getDisplayName();
                        String uid = getCurrentUser().getUid();
                        String restName = "";
                        String restId = "";
                        ArrayList<String> likedRestaurant = new ArrayList<>();

                        UserHelper.createUser(uid, name, urlPicture, restName, restId, likedRestaurant);
                    }
                }
            }
        });

    }


    private void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
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

        LocationManager locationManager = (LocationManager)
                Objects.requireNonNull(this).getSystemService(Context.LOCATION_SERVICE);


            currentLoc(locationManager);

    }

    public void currentLoc(LocationManager locationManager) {

        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_LOCATION = 1;
            ActivityCompat.requestPermissions(Objects.requireNonNull(this),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
        android.location.Location location = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager
                .getBestProvider(criteria, false)));
        if (location !=  null) {
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
