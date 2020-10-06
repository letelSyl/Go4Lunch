package com.example.go4lunch.fragments;

import android.view.View;

import com.bumptech.glide.Glide;
import com.example.go4lunch.databinding.WorkmateFragmentBinding;
import com.example.go4lunch.models.User.User;

import androidx.recyclerview.widget.RecyclerView;

public class WorkmateFragmentViewHolder extends RecyclerView.ViewHolder {
    public final View mView;

    private String picUrl;

    public WorkmateFragmentViewHolder(View view) {
        super(view);
        mView = view;
    }

    public void updateWithUser(User user){

        String uid = user.getUid();
        String name = user.getUsername();

        if(name != null){
            WorkmateFragmentBinding.bind(itemView).info.setText(name);
        }
        if (user.getUrlPicture() != null){
            this.picUrl = user.getUrlPicture();
            Glide.with(itemView.getContext()).load(this.picUrl).centerCrop().override(250, 250).into(WorkmateFragmentBinding.bind(itemView).fragmentPageItemPicture);
        }

    }

    @Override
    public String toString() {
        return super.toString();


    }
}