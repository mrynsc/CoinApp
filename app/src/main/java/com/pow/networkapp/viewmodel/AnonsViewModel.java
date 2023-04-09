package com.pow.networkapp.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pow.networkapp.model.Anons;
import com.pow.networkapp.repo.AnonsRepo;

import java.util.List;

public class AnonsViewModel extends ViewModel {

    private final AnonsRepo repo;
    private final LiveData<String> errorMessage;
    private final LiveData<List<Anons>> liveData;


    public AnonsViewModel() {
        repo = new AnonsRepo();
        errorMessage = repo.getError();
        liveData = repo.getAllAnnouncements();
    }

    public void getAnnouncements() {
        repo.getAnnouncements();
    }


    public LiveData<List<Anons>> getAllAnnouncements() {

        return liveData;
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }


}
