package com.example.go4lunch.fragments;

import android.location.Location;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.databinding.ListFragmentBinding;
import com.example.go4lunch.models.nearbySearch.Result;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import androidx.recyclerview.widget.RecyclerView;

public class ListFragmentViewHolder extends RecyclerView.ViewHolder {
    public final View mView;

    private String picUrl;


    public ListFragmentViewHolder(View view) {
        super(view);
        mView = view;
    }




    public void updateWithNearbySearch(Result result, double curLat, double curLng){

        float[] results = new float[1];
        Location.distanceBetween(curLat, curLng, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), results);
        float distance = results[0];

        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String roundedDistance = df.format(distance);

        ListFragmentBinding.bind(itemView).fragmentPageItemDistance.setText(roundedDistance + "m");

        ListFragmentBinding.bind(itemView).fragmentPageItemName.setText(result.getName());
        ListFragmentBinding.bind(itemView).fragmentPageItemAddress.setText(result.getVicinity());
        if (result.getOpeningHours() == null) {
            ListFragmentBinding.bind(itemView).fragmentPageItemClosureHour.setText("N/A");
        }else{
            if (result.getOpeningHours().getOpenNow()) {
                ListFragmentBinding.bind(itemView).fragmentPageItemClosureHour.setText("Open");
            } else {
                ListFragmentBinding.bind(itemView).fragmentPageItemClosureHour.setText("Closed");

            }


        }
        ListFragmentBinding.bind(itemView).ratingBar.setRating(result.getRating().byteValue()*3/5);

        if (result.getPhotos() != null && result.getPhotos().size() != 0){
            this.picUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+result.getPhotos().get(0).getPhotoReference()+"&key=" + BuildConfig.API_KEY;

            Glide.with(itemView.getContext()).load(picUrl).centerCrop().override(250, 250).into(ListFragmentBinding.bind(itemView).fragmentPageItemPicture);
        }

    }

}
