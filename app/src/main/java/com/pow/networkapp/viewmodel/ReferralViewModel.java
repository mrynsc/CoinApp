package com.pow.networkapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pow.networkapp.model.Referral;
import com.pow.networkapp.model.User;
import com.pow.networkapp.repo.ReferralRepo;

import java.util.List;


public class ReferralViewModel extends ViewModel {

    private ReferralRepo referralRepo;
    private LiveData<String> errorMessage;
    private LiveData<List<Referral>> liveData;


    public ReferralViewModel(){
        referralRepo = new ReferralRepo();
        errorMessage= referralRepo.getError();
        liveData = referralRepo.getUsers();
    }

    public void getInviters(String myId){
        referralRepo.getInviters(myId);
    }


    public LiveData<List<Referral>> getUsers() {

        return liveData;
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}
