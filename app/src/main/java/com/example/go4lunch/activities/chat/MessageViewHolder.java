package com.example.go4lunch.activities.chat;

import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityChatItemBinding;
import com.example.go4lunch.models.Message;
import com.example.go4lunch.models.User.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {



    //FOR DATA
    private final int colorCurrentUser;
    private final int colorRemoteUser;

    public MessageViewHolder(View itemView) {
        super(itemView);
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.personal_grey);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
    }

    public void updateWithMessage(Message message, User currentUser, RequestManager glide){

        // Check if current user is the sender
        Boolean isCurrentUser = message.getUserSenderName().equals(currentUser.getUsername());

        // Update message TextView
        ActivityChatItemBinding.bind(itemView).activityChatItemMessageContainerTextMessageContainerTextView.setText(message.getMessage());
        ActivityChatItemBinding.bind(itemView).activityChatItemMessageContainerTextMessageContainerTextView.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        // Update date TextView
        if (message.getDateCreated() != null) ActivityChatItemBinding.bind(itemView).activityChatItemMessageDate
                .setText(this.convertDateToHour(message.getDateCreated()));


                // Update profile picture ImageView
        if (message.getUserSenderPic() != null)
            glide.load(message.getUserSenderPic())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ActivityChatItemBinding.bind(itemView).activityChatItemProfileContainerProfileImage);


        //Update Message Bubble Color Background
        ActivityChatItemBinding.bind(itemView).activityChatItemMessageContainer.setBackgroundColor(isCurrentUser ? colorCurrentUser : colorRemoteUser);

        // Update all views alignment depending is current user or not
        this.updateDesignDependingUser(isCurrentUser);
    }

    private void updateDesignDependingUser(Boolean isSender){

        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        ActivityChatItemBinding.bind(itemView).activityChatItemProfileContainer.setLayoutParams(paramsLayoutHeader);

        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.activity_chat_item_profile_container);
        ActivityChatItemBinding.bind(itemView).activityChatItemMessageContainer.setLayoutParams(paramsLayoutContent);

        // CARDVIEW IMAGE SEND
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT, R.id.activity_chat_item_message_container_text_message_container);
//        ActivityChatItemBinding.bind(itemView).activityChatItemMessageContainerImageSentCardview.setLayoutParams(paramsImageView);

        ActivityChatItemBinding.bind(itemView).activityChatItemRootView.requestLayout();
    }

    // ---

    private String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dfTime.format(date);
    }
}