package com.pow.mining.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pow.mining.R;
import com.pow.mining.adapter.TransactionsAdapter;
import com.pow.mining.databinding.ActivityTransactionsBinding;
import com.pow.mining.model.Transaction;
import com.pow.mining.util.NetworkChangeListener;
import com.pow.mining.viewmodel.TransactionsActivityViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class TransactionsActivity extends AppCompatActivity {

    private ActivityTransactionsBinding binding;
    private ArrayList<Transaction> transactionArrayList;
    private TransactionsActivityViewModel viewModel;
    private TransactionsAdapter transactionsAdapter;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        viewModel = new ViewModelProvider(this).get(TransactionsActivityViewModel.class);

        pd = new ProgressDialog(this, R.style.CustomDialog);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        initRecycler();
        getTransactions();
        loadBanner();

    }

    private void loadBanner() {
        MobileAds.initialize(TransactionsActivity.this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

    }

    private void initRecycler() {
        transactionArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        transactionsAdapter = new TransactionsAdapter(transactionArrayList);
        binding.recyclerView.setAdapter(transactionsAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getTransactions() {

        viewModel.getTransactions(firebaseUser.getUid());
        viewModel.getAllTransactions().observe(this, posts -> {
            pd.dismiss();
            transactionArrayList.addAll(posts);
            transactionsAdapter.notifyDataSetChanged();
            if (transactionArrayList.isEmpty()) {
                binding.transactionInfoLay.setVisibility(View.VISIBLE);
            } else {
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