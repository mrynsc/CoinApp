package com.pow.networkapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pow.networkapp.R;
import com.pow.networkapp.adapter.TransactionsAdapter;
import com.pow.networkapp.databinding.ActivityTransactionsBinding;
import com.pow.networkapp.model.Transaction;
import com.pow.networkapp.util.NetworkChangeListener;
import com.pow.networkapp.viewmodel.TransactionsActivityViewModel;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity {

    private ActivityTransactionsBinding binding;
    private ArrayList<Transaction> transactionArrayList;
    private TransactionsActivityViewModel viewModel;
    private TransactionsAdapter transactionsAdapter;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        viewModel = new ViewModelProvider(this).get(TransactionsActivityViewModel.class);

        pd = new ProgressDialog(this,R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        initRecycler();
        getTransactions();

    }


    private void initRecycler(){
        transactionArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        transactionsAdapter = new TransactionsAdapter(transactionArrayList,this);
        binding.recyclerView.setAdapter(transactionsAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getTransactions() {

        viewModel.getTransactions(firebaseUser.getUid());
        viewModel.getAllTransactions().observe((LifecycleOwner) this, posts -> {
            pd.dismiss();
            transactionArrayList.addAll(posts);
            transactionsAdapter.notifyDataSetChanged();
            if (transactionArrayList.size()==0){
                binding.transactionInfoLay.setVisibility(View.VISIBLE);
            }else {
                binding.transactionInfoLay.setVisibility(View.GONE);
            }

        });
        viewModel.getErrorMessage().observe(this, error -> pd.dismiss());
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }


}