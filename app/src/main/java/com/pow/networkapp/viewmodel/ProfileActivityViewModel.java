package com.pow.networkapp.viewmodel;

import android.app.Activity;

import androidx.lifecycle.ViewModel;

import com.pow.networkapp.databinding.ActivityProfileBinding;
import com.pow.networkapp.repo.ProfileActivityRepo;

public class ProfileActivityViewModel extends ViewModel {

    private final ProfileActivityRepo repo;

    public ProfileActivityViewModel() {
        repo = new ProfileActivityRepo();
    }

    public void getUserInfo(String userId, ActivityProfileBinding binding, Activity activity) {
        repo.getUserInfo(userId, binding, activity);
    }

}
