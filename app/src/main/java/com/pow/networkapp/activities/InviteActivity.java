package com.pow.networkapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.R;
import com.pow.networkapp.adapter.ReferralAdapter;
import com.pow.networkapp.adapter.UserAdapter;
import com.pow.networkapp.databinding.ActivityInviteBinding;
import com.pow.networkapp.interfaces.OnClick;
import com.pow.networkapp.model.Referral;
import com.pow.networkapp.model.User;
import com.pow.networkapp.util.NetworkChangeListener;
import com.pow.networkapp.viewmodel.ReferralViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class InviteActivity extends AppCompatActivity {

    private ActivityInviteBinding binding;

    private FirebaseUser firebaseUser;
    private ArrayList<Referral> userArrayList;
    private ReferralAdapter referralAdapter;

    private ReferralViewModel viewModel;
    private ProgressDialog pd;

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());


        pd = new ProgressDialog(this,R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewModel = new ViewModelProvider(this).get(ReferralViewModel.class);


        initRecycler();
        getInviters();
        loadBanner();
        loadAds();

        binding.copyBtn.setOnClickListener(view -> {
            String shareText = new StringBuilder().append("Come to POW Network App and Earn POW Coin. Use my referral link. ")

                    .append("https://play.google.com/store/apps/details?id=com.pow.networkapp&referrer=").append(firebaseUser.getUid()).toString();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(intent, "Share Via"));


        });


    }


    private void loadAds(){
        MobileAds.initialize(InviteActivity.this, initializationStatus -> {

        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(InviteActivity.this, getString(R.string.intersId), adRequest,
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
    private void loadBanner(){
//        MobileAds.initialize(StartActivity.this, initializationStatus -> {
//            pd.dismiss();
//        });
//        AdRequest adRequest = new AdRequest.Builder().build();
//        binding.mainProfile.adView.loadAd(adRequest);

        Appodeal.initialize(this,getString(R.string.appodeal_app_id),Appodeal.BANNER);
        Appodeal.show(this,Appodeal.BANNER);
        Appodeal.isLoaded(Appodeal.BANNER);
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




    }


    private void initRecycler(){
        userArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        binding.recyclerView.setHasFixedSize(true);
        referralAdapter = new ReferralAdapter(userArrayList,this);
        binding.recyclerView.setAdapter(referralAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getInviters(){
        userArrayList.clear();
        viewModel.getInviters(firebaseUser.getUid());
        viewModel.getUsers().observe(this, posts -> {
            userArrayList.addAll(posts);
            pd.dismiss();
            referralAdapter.notifyDataSetChanged();
            if (userArrayList.size()==0){
                pd.dismiss();
                binding.nothingLay.setVisibility(View.VISIBLE);
                binding.headerText.setVisibility(View.GONE);
            }else{
                binding.nothingLay.setVisibility(View.GONE);
                binding.headerText.setVisibility(View.VISIBLE);
                binding.headerText.setText(new StringBuilder().append("My Referrals (").append(userArrayList.size()).append(")").toString());

            }

        });
        viewModel.getErrorMessage().observe(this, System.out::println);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInterstitialAd!=null){
            mInterstitialAd.show(this);
        }

    }

}