package com.pow.networkapp.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class ReferralRepo {

    private MutableLiveData<String> errorMessage;
    private MutableLiveData<List<User>> mutableLiveData;
    private List<User> userList;

    public ReferralRepo(){
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        userList = new ArrayList<>();

    }

    public void getReferral(String link){
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("referralLink").equalTo(link);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()){
                        User user = dataSnapshot.getValue(User.class);

                        if (user!=null){
                            userList.add(user);
                        }

                    }

                }
                mutableLiveData.postValue(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    public LiveData<List<User>> getUser() {
        return mutableLiveData;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }


}
