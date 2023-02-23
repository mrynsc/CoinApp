package com.pow.networkapp.repo;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.model.Referral;
import com.pow.networkapp.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReferralRepo {

    private MutableLiveData<String> errorMessage;
    private MutableLiveData<List<Referral>> mutableLiveData;
    private List<Referral> referralList;

    public ReferralRepo(){
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        referralList = new ArrayList<>();

    }

    public void getInviters(String myId){
        try {
            FirebaseDatabase.getInstance().getReference().child("Referrals").child(myId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                                if (snapshot.exists()){
                                    Referral referral = dataSnapshot.getValue(Referral.class);
                                    if (referral!=null){
                                        referralList.add(referral);
                                    }
                                }
                            }
                            mutableLiveData.postValue(referralList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }catch (DatabaseException e){
            e.printStackTrace();
        }

    }



    public LiveData<List<Referral>> getUsers() {
        return mutableLiveData;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }


}
