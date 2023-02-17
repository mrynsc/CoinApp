package com.pow.networkapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.R;
import com.pow.networkapp.databinding.ActivityWalletBinding;
import com.pow.networkapp.model.User;
import com.pow.networkapp.util.NetworkChangeListener;

import java.util.HashMap;

public class WalletActivity extends AppCompatActivity {

    private ActivityWalletBinding binding;
    private FirebaseUser firebaseUser;
    private final int minWithdrawal = 15000;
    private int myTotalBalance;
    private int myRequest;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

        getBalanceInfo();

        if (binding.formLayout.getVisibility()==View.VISIBLE){
            binding.confirmBtn.setOnClickListener(view -> {
                if (binding.walletAddressEt.getText().toString().trim().length()>1 && binding.withdrawalEt.getText().toString().trim().length()>0){
                    String address = binding.walletAddressEt.getText().toString().trim();
                    try {
                        myTotalBalance = Integer.parseInt(binding.totalBalance.getText().toString());
                        myRequest = Integer.parseInt(binding.withdrawalEt.getText().toString());
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                    if (myTotalBalance >= myRequest){

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        String id = reference.push().getKey();
                        long time = System.currentTimeMillis();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("address",address);
                        map.put("userId",firebaseUser.getUid());
                        map.put("time",time);
                        map.put("withdrawal",myRequest);
                        map.put("id",id);

                        if (id != null) {
                            reference.child("MyTransactions").child(firebaseUser.getUid()).child(id)
                                    .setValue(map).addOnSuccessListener(unused -> {
                                        lostBalance(myRequest);
                                        HashMap<String,Object> hashMap = new HashMap<>();
                                        hashMap.put("address",address);
                                        hashMap.put("userId",firebaseUser.getUid());
                                        hashMap.put("withdrawal",myRequest);
                                        hashMap.put("time",time);
                                        hashMap.put("id",id);
                                        reference.child("Transactions").child(id).setValue(hashMap);
                                        finish();

                                    }).addOnFailureListener(e -> Toast.makeText(WalletActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show());
                        }





                    }





                }
            });


        }

    }

    private void lostBalance(int lostValue){
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            HashMap<String,Object> map = new HashMap<>();
                            if (user != null) {
                                map.put("balance",user.getBalance() - lostValue);
                            }
                            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).updateChildren(map);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }



    private void getBalanceInfo(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            binding.claimedBalance.setText(new StringBuilder().append("").append(user.getClaimed()).toString());
                            binding.referralBalance.setText(new StringBuilder().append("").append(user.getReferral()).toString());

                            binding.totalBalance.setText(new StringBuilder().append(user.getBalance()).toString());

                            binding.currentBalance.setText(new StringBuilder().append(user.getBalance()).toString());


                            int a = (int) (user.getBalance() * 100 / minWithdrawal);
                            binding.earningProgress.setProgress((Math.min(a, 100)));

                            binding.earningPercent.setText(new StringBuilder().append(Math.min(a, 100)).append("%").toString());

                            if (user.getBalance() >= minWithdrawal){
                                binding.formLayout.setVisibility(View.VISIBLE);
                            }else {
                                binding.formLayout.setVisibility(View.GONE);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

}