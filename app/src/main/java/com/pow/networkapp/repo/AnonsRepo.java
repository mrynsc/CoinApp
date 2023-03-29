package com.pow.networkapp.repo;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pow.networkapp.model.Anons;
import com.pow.networkapp.model.Referral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnonsRepo {

    private MutableLiveData<String> errorMessage;
    private MutableLiveData<List<Anons>> mutableLiveData;
    private List<Anons> anonsList;


    public AnonsRepo(){
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        anonsList = new ArrayList<>();

    }

    public void getAnnouncements(){
        FirebaseFirestore.getInstance().collection("Announcements").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Anons anons = d.toObject(Anons.class);
                                anonsList.add(anons);
                            }
                            mutableLiveData.postValue(anonsList);
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }


    public LiveData<List<Anons>> getAllAnnouncements() {
        return mutableLiveData;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }


}
