package com.example.go4lunch.fragments;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.annotations.NonNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.R;
//import com.example.go4lunch.fragments.dummy.ListFragmentDummyContent.DummyItem;
import com.example.go4lunch.models.nearbySearch.Result;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ListFragmentRecyclerViewAdapter extends RecyclerView.Adapter<ListFragmentViewHolder> {

    private List<Result> results;
    private Context context;
    private double curLat;
    private double curLng;

    public ListFragmentRecyclerViewAdapter(List<Result> results, double curLat, double curLng) {
        this.curLat = curLat;
        this.curLng = curLng;
        this.results = results;
    }

    @NotNull
    @Override
    public ListFragmentViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_fragment, parent, false);

        return new ListFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListFragmentViewHolder viewHolder, int position) {
        final Result result = this.results.get(position);

        viewHolder.updateWithNearbySearch(result, curLat, curLng);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "CLICK !!!",  Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return this.results.size();
    }


}
