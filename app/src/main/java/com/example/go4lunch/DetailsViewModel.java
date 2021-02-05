package com.example.go4lunch;

import com.example.go4lunch.firestore.UserHelper;
import com.example.go4lunch.models.User.User;
import com.example.go4lunch.models.details.Result;
import com.example.go4lunch.repository.DetailsRepository;
import com.example.go4lunch.repository.FirestoreRepository;
import com.example.go4lunch.repository.NearBySearchRepository;

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
//TODO : add list of user in repository
        UserHelper.updateRestName(restName, uid);
        UserHelper.updateRestId(restId, uid);

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
