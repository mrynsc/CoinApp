package com.pow.networkapp.viewmodel;

import android.app.Activity;

import androidx.lifecycle.ViewModel;

import com.google.android.material.navigation.NavigationView;
import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.repo.StartActivityRepo;

public class StartActivityViewModel extends ViewModel {

    private final StartActivityRepo repo;

    public StartActivityViewModel() {
        repo = new StartActivityRepo();
    }

    public void getUserInfo(Activity activity, String userId, ActivityStartBinding binding, NavigationView navigationView) {
        repo.getUserInfo(activity, userId, binding, navigationView);
    }

    public void getTotalUsers(ActivityStartBinding binding) {
        repo.getTotalUsers(binding);
    }

    public long getNow(Activity activity) {
        return repo.getNow(activity);
    }

    public void updateLastSeen(String myId) {
        repo.updateLastSeen(myId);
    }

    public void updateBalance(String myId) {
        repo.updateBalance(myId);
    }

    public void getMainAnons(Activity activity, ActivityStartBinding binding) {
        repo.getMainAnons(activity, binding);
    }

}
