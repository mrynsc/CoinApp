package com.pow.mining.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pow.mining.model.Referral;
import com.pow.mining.repo.ReferralRepo;

import java.util.List;


public class ReferralViewModel extends ViewModel {

    private final ReferralRepo referralRepo;
    private final LiveData<String> errorMessage;
    private final LiveData<List<Referral>> liveData;


    public ReferralViewModel() {
        referralRepo = new ReferralRepo();
        errorMessage = referralRepo.getError();
        liveData = referralRepo.getUsers();
    }

    public void getInviters(String myId) {
        referralRepo.getInviters(myId);
    }


    public LiveData<List<Referral>> getUsers() {

        return liveData;
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}
