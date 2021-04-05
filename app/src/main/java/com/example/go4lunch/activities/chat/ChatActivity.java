package com.example.go4lunch.activities.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityChatBinding;
import com.example.go4lunch.databinding.ToolbarBinding;
import com.example.go4lunch.firestore.CurrentUser;
import com.example.go4lunch.firestore.MessageHelper;
import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.models.Message;
import com.example.go4lunch.models.User.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.Listener {



    private ActivityChatBinding binding;
    private Toolbar toolbar;

    private View view;
    // FOR DATA
    // 2 - Declaring Adapter and data
    private ChatAdapter chatAdapter;
    @Nullable
    private User modelCurrentUser;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        setContentView(view);

        this.modelCurrentUser = CurrentUser.getInstance();
        this.configureRecyclerView();
        this.configureToolbar();
        //this.getCurrentUserFromFirestore();
        onClickSendMessage();

    }
    private void configureToolbar() {
        //Set the toolbar
        this.toolbar = binding.toolbar.toolbar;
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    // --------------------
    // ACTIONS
    // --------------------


    public void onClickSendMessage() {
        binding.activityChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 1 - Check if text field is not empty and current user properly downloaded from Firestore
                if (!TextUtils.isEmpty(ActivityChatBinding.bind(view).activityChatMessageEditText.getText()) && modelCurrentUser != null) {
                    // 2 - Create a new Message to Firestore
                    MessageHelper.createMessageForChat(ActivityChatBinding.bind(view).activityChatMessageEditText.getText().toString(), modelCurrentUser).addOnFailureListener(onFailureListener());
                    // 3 - Reset text field
                    ActivityChatBinding.bind(view).activityChatMessageEditText.setText("");
                }
            }
        });
      }

        // --------------------
        // ERROR HANDLER
        // --------------------

        protected OnFailureListener onFailureListener (){
            return new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
                    }
                };
        }






    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from Firestore
    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(CurrentUser.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(){

        //Configure Adapter & RecyclerView
        this.chatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat()), Glide.with(this), this, modelCurrentUser);
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.activityChatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        binding.activityChatRecyclerView.setAdapter(this.chatAdapter);
        binding.activityChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        binding.activityChatTextViewRecyclerViewEmpty.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

}

