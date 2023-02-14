package com.pow.networkapp.viewmodel;

import androidx.lifecycle.ViewModel;

import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.repo.StartActivityRepo;

public class StartActivityViewModel extends ViewModel {

    private final StartActivityRepo repo;

    public StartActivityViewModel(){
        repo = new StartActivityRepo();
    }

    public void getUserInfo(String userId, ActivityStartBinding binding){
        repo.getUserInfo(userId,binding);
    }

}
