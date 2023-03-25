package com.pow.networkapp.repo;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.model.Anons;
import com.pow.networkapp.model.Referral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnonsRepo {

    private MutableLiveData<String> errorMessage;
    private MutableLiveData<List<Anons>> mutableLiveData;
    private List<Anons> anonsList;
    private FirebaseDatabase firebaseDatabase;


    public AnonsRepo(){
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        anonsList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    public void getAnnouncements(){
        firebaseDatabase.getReference().child("Announcements").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    if (snapshot.exists()){
                        Anons anons = ds.getValue(Anons.class);

                        if (anons!=null && anons.getStatus()==0){
                            anonsList.add(anons);
                        }
                    }
                }
                mutableLiveData.postValue(anonsList);
                Collections.reverse(anonsList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
