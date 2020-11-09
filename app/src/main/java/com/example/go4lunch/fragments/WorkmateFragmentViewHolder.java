package com.example.go4lunch.fragments;

import android.view.View;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
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
        String restName = user.getRestName();

        if(name != null){
            if (restName != null && !restName.equals(""))
                WorkmateFragmentBinding.bind(itemView).info.setText(name + " " + itemView.getContext().getResources().getString(R.string.is_eating_at) + " " + restName);
            else {
                WorkmateFragmentBinding.bind(itemView).info.setText(name + " " + itemView.getContext().getResources().getString(R.string.hasnt_decided));
                WorkmateFragmentBinding.bind(itemView).info.setTextColor(itemView.getContext().getResources().getColor(R.color.personal_grey));

            }
        }
        if (user.getUrlPicture() != null){
            this.picUrl = user.getUrlPicture();
            Glide.with(itemView.getContext()).load(this.picUrl). circleCrop().override(250, 250).into(WorkmateFragmentBinding.bind(itemView).fragmentPageItemPicture);
        }

    }

    @Override
    public String toString() {
        return super.toString();


    }
}