package com.pow.mining.activities;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pow.mining.R;
import com.pow.mining.databinding.ActivityWalletBinding;
import com.pow.mining.model.User;
import com.pow.mining.util.NetworkChangeListener;

import java.util.HashMap;

public class WalletActivity extends AppCompatActivity {

    private ActivityWalletBinding binding;
    private FirebaseUser firebaseUser;
    private final int MIN_WITHDRAW = 15000;
    private int myTotalBalance;
    private int myRequest;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        loadBanner();
        loadAds();
        getBalanceInfo();

        if (binding.formLayout.getVisibility() == View.VISIBLE) {
            binding.confirmBtn.setOnClickListener(view -> {
                if (binding.walletAddressEt.getText().toString().trim().length() > 1 && binding.withdrawalEt.getText().toString().trim().length() > 0) {
                    String address = binding.walletAddressEt.getText().toString().trim();
                    try {
                        myTotalBalance = Integer.parseInt(binding.totalBalance.getText().toString());
                        myRequest = Integer.parseInt(binding.withdrawalEt.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (myTotalBalance >= myRequest) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        String id = reference.push().getKey();
                        long time = System.currentTimeMillis();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("address", address);
                        map.put("userId", firebaseUser.getUid());
                        map.put("time", time);
                        map.put("withdrawal", myRequest);
                        map.put("id", id);

                        if (id != null) {
                            reference.child("MyTransactions").child(firebaseUser.getUid()).child(id)
                                    .setValue(map).addOnSuccessListener(unused -> {
                                        lostBalance(myRequest);
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("address", address);
                                        hashMap.put("userId", firebaseUser.getUid());
                                        hashMap.put("withdrawal", myRequest);
                                        hashMap.put("time", time);
                                        hashMap.put("id", id);
                                        reference.child("Transactions").child(id).setValue(hashMap);
                                        finish();

                                    }).addOnFailureListener(e -> Toast.makeText(WalletActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show());
                        }


                    }


                }
            });


        }

    }


    private void loadBanner() {
        MobileAds.initialize(WalletActivity.this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

    }

    private void lostBalance(int lostValue) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            HashMap<String, Object> map = new HashMap<>();
                            if (user != null) {
                                map.put("balance", user.getBalance() - lostValue);
                            }
                            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void getBalanceInfo() {
        FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getUid())
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            binding.claimedBalance.setText("" + user.getClaimed());
                            binding.referralBalance.setText("" + user.getReferral());

                            binding.totalBalance.setText(String.valueOf(user.getBalance()));

                            binding.currentBalance.setText(String.valueOf(user.getBalance()));


                            int a = (user.getBalance() * 100 / MIN_WITHDRAW);
                            binding.earningProgress.setProgress((Math.min(a, 100)));

                            binding.earningPercent.setText(Math.min(a, 100) + "%");
                        }
                    }
                });


    }


    private void loadAds() {
        MobileAds.initialize(WalletActivity.this, initializationStatus -> {

        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(WalletActivity.this, getString(R.string.intersId), adRequest,
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
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }

}