package com.pow.networkapp.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pow.networkapp.model.Anons;

import java.util.ArrayList;
import java.util.List;

public class AnonsRepo {

    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<List<Anons>> mutableLiveData;
    private final List<Anons> anonsList;


    public AnonsRepo() {
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        anonsList = new ArrayList<>();

    }

    public void getAnnouncements() {
        FirebaseFirestore.getInstance().collection("Announcements").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            Anons anons = d.toObject(Anons.class);
                            anonsList.add(anons);
                        }
                        mutableLiveData.postValue(anonsList);
                    }

                }).addOnFailureListener(e -> {
                });
    }


    public LiveData<List<Anons>> getAllAnnouncements() {
        return mutableLiveData;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }


}
