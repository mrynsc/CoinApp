package com.pow.networkapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.pow.networkapp.model.Transaction;
import com.pow.networkapp.repo.TransactionsActivityRepo;

import java.util.List;

public class TransactionsActivityViewModel extends ViewModel {

    private TransactionsActivityRepo repo;
    private LiveData<String> errorMessage;
    private LiveData<List<Transaction>> liveData;


    public TransactionsActivityViewModel(){
        repo = new TransactionsActivityRepo();
        errorMessage= repo.getError();
        liveData = repo.getAllTransactions();
    }

    public void getTransactions(String userId){
        repo.getTransactions(userId);
    }

    public LiveData<List<Transaction>> getAllTransactions() {

        return liveData;
    }


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }


}
