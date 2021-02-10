package com.example.go4lunch.viewModels;

import com.example.go4lunch.firestore.CurrentUser;
import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.models.User.User;
import com.example.go4lunch.models.details.Result;
import com.example.go4lunch.repository.DetailsRepository;
import com.example.go4lunch.repository.FirestoreRepository;
import com.example.go4lunch.repository.NearBySearchRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DetailsViewModel extends ViewModel {



    private final DetailsRepository detailsRepository;
    private final FirestoreRepository usersRepo;
    private final NearBySearchRepository nearBySearchRepository;

    private MutableLiveData<Result> details = new MutableLiveData<>();
    private MutableLiveData<List<User>> usersList = new MutableLiveData<>();




    public DetailsViewModel() {
        super();
        nearBySearchRepository = NearBySearchRepository.getInstance();
        detailsRepository = new DetailsRepository();
        usersRepo = new FirestoreRepository();
    }


    public MutableLiveData<Result> getDetails(String placeId){
        details = loadDetails(placeId);
        return details;
    }

    private MutableLiveData<Result> loadDetails(String placeId) {

        return detailsRepository.getDetails(placeId);
    }

    public MutableLiveData<List<User>> getUsersList(String restName){
        usersList = loadUsers(restName);
        return usersList;
    }

    public void updateCurrentUser(String restName, String restId, String uid){
        UserHelper.updateRestName(restName, uid);
        UserHelper.updateRestId(restId, uid);
        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                CurrentUser.set_instance(currentUser);
            }
        });
    }

    public void increaseResultsNumUsers(String currentRestId) {
        nearBySearchRepository.increaseResultsNumUsers(currentRestId);
    }

    public void decreaseResultsNumUsers(String currentRestId) {
        nearBySearchRepository.decreaseResultsNumUsers(currentRestId);
    }

        private MutableLiveData<List<User>> loadUsers(String restName){
        return usersRepo.getFilteredUserList(restName);
    }


}
