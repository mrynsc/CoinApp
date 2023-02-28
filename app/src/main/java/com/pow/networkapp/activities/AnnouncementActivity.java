package com.pow.networkapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.pow.networkapp.R;
import com.pow.networkapp.adapter.AnonsAdapter;
import com.pow.networkapp.databinding.ActivityAnnouncementBinding;
import com.pow.networkapp.model.Anons;
import com.pow.networkapp.viewmodel.AnonsViewModel;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {

    private ActivityAnnouncementBinding binding;
    private List<Anons> anonsList;
    private AnonsAdapter anonsAdapter;
    private AnonsViewModel viewModel;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        pd = new ProgressDialog(this,R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();


        viewModel = new ViewModelProvider(this).get(AnonsViewModel.class);

        initRecycler();
        getAnnouncements();
        
        loadAppodealBanner();

    }

    private void loadAppodealBanner() {
        Appodeal.initialize(this, getResources().getString(R.string.appodeal_app_id), Appodeal.BANNER);

        Appodeal.setBannerViewId(R.id.bannerAds);
        Appodeal.show(this,Appodeal.BANNER);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int i, boolean b) {
                Log.d("==ban","loaded");
            }

            @Override
            public void onBannerFailedToLoad() {
                Log.d("==ban","not loaded");
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
    }


    private void initRecycler(){
        anonsList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        anonsAdapter = new AnonsAdapter(anonsList,this);
        binding.recyclerView.setAdapter(anonsAdapter);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getAnnouncements(){
        viewModel.getAnnouncements();
        viewModel.getAllAnnouncements().observe((LifecycleOwner) this, posts -> {
            pd.dismiss();
            anonsList.addAll(posts);
            anonsAdapter.notifyDataSetChanged();
            if (anonsList.size()==0){
                binding.lottieAnimation.setVisibility(View.VISIBLE);
            }else {
                binding.lottieAnimation.setVisibility(View.GONE);
            }

        });
        viewModel.getErrorMessage().observe(this, error -> pd.dismiss());
    }


}