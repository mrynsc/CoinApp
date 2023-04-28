package com.pow.mining.activities;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.pow.mining.R;
import com.pow.mining.adapter.AnonsAdapter;
import com.pow.mining.databinding.ActivityAnnouncementBinding;
import com.pow.mining.model.Anons;
import com.pow.mining.viewmodel.AnonsViewModel;

import java.util.List;
import java.util.Objects;

public class AnnouncementActivity extends AppCompatActivity {

    private ActivityAnnouncementBinding binding;
    private List<Anons> anonsList;
    private AnonsAdapter anonsAdapter;
    private AnonsViewModel viewModel;
    private ProgressDialog pd;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        pd = new ProgressDialog(this, R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();


        viewModel = new ViewModelProvider(this).get(AnonsViewModel.class);


        loadBanner();
        loadAds();
    }


    private void loadAds() {
        MobileAds.initialize(AnnouncementActivity.this, initializationStatus -> {

        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(AnnouncementActivity.this, getString(R.string.intersId), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                //Toast.makeText(WatchAdsActivity.this, "tıklandı", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                //Toast.makeText(WatchAdsActivity.this, "kapandı", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                //Toast.makeText(WatchAdsActivity.this, "tıklandı2", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                                //Toast.makeText(getContext(), "gösteriyor", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                //Toast.makeText(getContext(), "full", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                });
    }


    private void loadBanner() {
        MobileAds.initialize(AnnouncementActivity.this, initializationStatus -> {
            pd.dismiss();
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

    }


//    private void initRecycler(){
//        anonsList = new ArrayList<>();
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        binding.recyclerView.setHasFixedSize(true);
//        anonsAdapter = new AnonsAdapter(anonsList,this);
//        binding.recyclerView.setAdapter(anonsAdapter);
//    }
//
//
//    @SuppressLint("NotifyDataSetChanged")
//    private void getAnnouncements(){
//        viewModel.getAnnouncements();
//        viewModel.getAllAnnouncements().observe((LifecycleOwner) this, posts -> {
//            pd.dismiss();
//            anonsList.addAll(posts);
//            anonsAdapter.notifyDataSetChanged();
//            if (anonsList.size()==0){
//                binding.lottieAnimation.setVisibility(View.VISIBLE);
//            }else {
//                binding.lottieAnimation.setVisibility(View.GONE);
//            }
//
//        });
//        viewModel.getErrorMessage().observe(this, error -> pd.dismiss());
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }

    }

}