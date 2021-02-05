package com.example.go4lunch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.RestaurantsViewModel;
import com.example.go4lunch.databinding.ListFragmentBinding;
import com.example.go4lunch.databinding.ListFragmentItemListBinding;
import com.example.go4lunch.models.User.User;
import com.example.go4lunch.models.nearbySearch.Result;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import java.util.Observer;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.example.go4lunch.httpRequest.RetrofitBuilder.logging;

/**
 * A fragment representing a list of Items.
 */
public class ListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    // Create keys for our Bundle
    private static final String KEY_POSITION = "position";
    private static double latitude;
    private static double longitude;
    private int mColumnCount = 1;
    private ListFragmentBinding listFragmentBinding;
    private ListFragmentItemListBinding itemListBinding;
    private int position;
    private Disposable disposable;
    private List<Result> mResults = new ArrayList<>();
    private ListFragmentRecyclerViewAdapter mAdapter;

    private List<User> users = new ArrayList<>();
    private RestaurantsViewModel restaurantsViewModel;

    private int numUsers;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

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
        getActivity().setTitle(getString(R.string.hungry));

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        restaurantsViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsViewModel.class);
      /*  if(restaurantsViewModel.getListOfRestaurants() == null) {
            getLifecycle().addObserver(restaurantsViewModel);*/
        restaurantsViewModel.getListOfRestaurants().observe(this, this::updateUI);
        //}
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        this.listFragmentBinding = ListFragmentBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.list_fragment_item_list, container, false);

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        latitude = MainActivity.getLatitude();
        longitude = MainActivity.getLongitude();

        // Set the adapter

        if (view instanceof RecyclerView) {

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.mAdapter = new ListFragmentRecyclerViewAdapter(this.mResults, latitude, longitude);

            recyclerView.setAdapter(this.mAdapter);
        }



        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }


    private void updateUI(List<Result> results) {
        this.mResults.clear();
        this.mResults.addAll(results);
        this.mAdapter.notifyDataSetChanged();
    }
}


