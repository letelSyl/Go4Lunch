package com.example.go4lunch;

import android.view.View;

import com.bumptech.glide.Glide;
import com.example.go4lunch.databinding.WorkmateFragmentBinding;
import com.example.go4lunch.models.User.User;

import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public final View mView;

    public UserViewHolder(View view) {
        super(view);
        this.mView = view;
    }

    public void updateWithUser(User user) {

        String picUrl = user.getUrlPicture();
        String name = user.getUsername();
        String setName = name + itemView.getContext().getResources().getString(R.string.is_joining);

        if (name != null) {
            WorkmateFragmentBinding.bind(itemView).info.setText(setName);

            if (user.getUrlPicture() != null) {
                picUrl = user.getUrlPicture();
                Glide.with(itemView.getContext()).load(picUrl).circleCrop().override(250, 250).into(WorkmateFragmentBinding.bind(itemView).fragmentPageItemPicture);
            }

        }
    }
}