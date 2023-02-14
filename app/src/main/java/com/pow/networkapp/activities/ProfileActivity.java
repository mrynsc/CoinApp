package com.pow.networkapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pow.networkapp.R;
import com.pow.networkapp.databinding.ActivityProfileBinding;
import com.pow.networkapp.viewmodel.ProfileActivityViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileActivityViewModel viewModel;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewModel = new ViewModelProvider(this).get(ProfileActivityViewModel.class);
        viewModel.getUserInfo(firebaseUser.getUid(),binding);


    }

}