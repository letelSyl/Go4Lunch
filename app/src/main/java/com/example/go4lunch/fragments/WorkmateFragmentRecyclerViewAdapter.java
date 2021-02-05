package com.example.go4lunch.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.models.User.User;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class WorkmateFragmentRecyclerViewAdapter extends RecyclerView.Adapter<WorkmateFragmentViewHolder> {

    private final List<User> mValues;
    private Context context;

    public WorkmateFragmentRecyclerViewAdapter(List<User> items) {
        super();
        mValues = items;
    }



    @Override
    public WorkmateFragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workmate_fragment, parent, false);
        return new WorkmateFragmentViewHolder(view);
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void onBindViewHolder(final WorkmateFragmentViewHolder viewHolder, int position) {
        final User user = this.mValues.get(position);

        viewHolder.updateWithUser(user);

    }
}
