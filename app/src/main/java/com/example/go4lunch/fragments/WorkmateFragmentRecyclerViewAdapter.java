package com.example.go4lunch.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.fragments.dummy.WorkmateFragmentDummyContent.DummyItem;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


/**
 TODO: Replace the implementation with code for your data type.
 */
public class WorkmateFragmentRecyclerViewAdapter extends RecyclerView.Adapter<WorkmateViewHolder> {

    private final List<DummyItem> mValues;

    public WorkmateFragmentRecyclerViewAdapter(List<DummyItem> items) {
        mValues = items;
    }

    @Override
    public WorkmateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workmate_fragment, parent, false);
        return new WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WorkmateViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


}
