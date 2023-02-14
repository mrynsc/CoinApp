package com.pow.networkapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pow.networkapp.R;
import com.pow.networkapp.adapter.TransactionsAdapter;
import com.pow.networkapp.databinding.ActivityTransactionsBinding;
import com.pow.networkapp.model.Transaction;
import com.pow.networkapp.viewmodel.TransactionsActivityViewModel;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity {

    private ActivityTransactionsBinding binding;
    private ArrayList<Transaction> transactionArrayList;
    private TransactionsActivityViewModel viewModel;
    private TransactionsAdapter transactionsAdapter;
    private FirebaseUser firebaseUser;
    private ProgressDialog pd;



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
        Appodeal.initialize(this, getResources().getString(R.string.appodeal_app_id), Appodeal.BANNER);

        Appodeal.setBannerViewId(R.id.bannerAds);
        Appodeal.show(this,Appodeal.BANNER);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int i, boolean b) {
            }

            @Override
            public void onBannerFailedToLoad() {
            }

            @Override
            public void onBannerShown() {

            }

            @Override
            public void onBannerShowFailed() {
            }

            @Override
            public void onBannerClicked() {

            }

            @Override
            public void onBannerExpired() {

            }
        });
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



}