package com.example.go4lunch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.UsersViewModel;
import com.example.go4lunch.models.User.User;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 */
public class WorkmateFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;


    private UsersViewModel usersViewModel;

    private MutableLiveData<CollectionReference> mutableLiveData;

    private List<User> mUsers = new ArrayList<>();

    private WorkmateFragmentRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkmateFragment() {
    }



    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static WorkmateFragment newInstance(int columnCount) {
        WorkmateFragment fragment = new WorkmateFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workmate_fragment_item_list, container, false);

        getActivity().setTitle(getString(R.string.available_workmate));

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            this.mAdapter = new WorkmateFragmentRecyclerViewAdapter(this.mUsers);
            recyclerView.setAdapter(this.mAdapter);
        }

        usersViewModel = new ViewModelProvider(this).get(UsersViewModel.class);

        usersViewModel.getListOfUser().observe(getViewLifecycleOwner(), users ->{

            if (users != null){
                updateUIWithUsers(users);
            }
        });


        return view;
    }

    public void updateUIWithUsers(List<User> users) {
        this.mUsers.clear();
        this.mUsers.addAll(users);
        this.mAdapter.notifyDataSetChanged();
    }
}
