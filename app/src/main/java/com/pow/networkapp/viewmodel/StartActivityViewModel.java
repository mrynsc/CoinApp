package com.pow.networkapp.viewmodel;

import android.app.Activity;

import androidx.lifecycle.ViewModel;

import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.repo.StartActivityRepo;

public class StartActivityViewModel extends ViewModel {

    private final StartActivityRepo repo;

    public StartActivityViewModel(){
        repo = new StartActivityRepo();
    }

    public void getUserInfo(Activity activity,String userId, ActivityStartBinding binding){
        repo.getUserInfo(activity,userId,binding);
    }

    public void getTotalUsers(ActivityStartBinding binding){
        repo.getTotalUsers(binding);
    }

    public long getNow(Activity activity){
        return repo.getNow(activity);
    }

    public void updateLastSeen(String myId){
        repo.updateLastSeen(myId);
    }

    public void updateBalance(String myId){
        repo.updateBalance(myId);
    }

}
