package com.example.go4lunch.firestore;




import com.example.go4lunch.models.Message;
import com.example.go4lunch.models.User.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MessageHelper {
    private static final String COLLECTION_NAME = "Messages";

    // --- GET ---

    public static Query getAllMessageForChat(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender){
        Message message = new Message(textMessage, userSender);
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).add(message);
    }


}

