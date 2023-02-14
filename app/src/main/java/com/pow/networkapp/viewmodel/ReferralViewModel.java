package com.pow.networkapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pow.networkapp.model.User;
import com.pow.networkapp.repo.ReferralRepo;

import java.util.List;


public class ReferralViewModel extends ViewModel {

    private ReferralRepo referralRepo;
    private LiveData<String> errorMessage;
    private LiveData<List<User>> liveData;


    public ReferralViewModel(){
        referralRepo = new ReferralRepo();
        errorMessage= referralRepo.getError();
        liveData = referralRepo.getUser();
    }

    public void getReferral(String link){
        referralRepo.getReferral(link);
    }


    public LiveData<List<User>> getUser() {

        return liveData;
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}
