package com.pow.networkapp.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionsActivityRepo {

    private final FirebaseDatabase database;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<List<Transaction>> mutableLiveData;
    private final List<Transaction> transactionList;


    public TransactionsActivityRepo() {
        database = FirebaseDatabase.getInstance();
        errorMessage = new MutableLiveData<>();
        mutableLiveData = new MutableLiveData<>();
        transactionList = new ArrayList<>();
    }

    public void getTransactions(String userId) {
        try {
            database.getReference().child("MyTransactions").child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            transactionList.clear();
                            try {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (snapshot.exists()) {
                                        Transaction transaction = dataSnapshot.getValue(Transaction.class);

                                        if (transaction != null) {
                                            transactionList.add(transaction);
                                        }


                                    }


                                }
                                Collections.reverse(transactionList);
                                mutableLiveData.postValue(transactionList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            errorMessage.postValue(error.getMessage());
                        }
                    });
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return mutableLiveData;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }


}
