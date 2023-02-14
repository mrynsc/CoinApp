package com.pow.networkapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.R;
import com.pow.networkapp.adapter.UserAdapter;
import com.pow.networkapp.databinding.ActivityInviteBinding;
import com.pow.networkapp.interfaces.OnClick;
import com.pow.networkapp.model.User;
import com.pow.networkapp.viewmodel.ReferralViewModel;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class InviteActivity extends AppCompatActivity implements OnClick {

    private ActivityInviteBinding binding;

    private FirebaseUser firebaseUser;
    private ArrayList<User> userArrayList;
    private UserAdapter referralAdapter;

    private ReferralViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInviteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        viewModel = new ViewModelProvider(this).get(ReferralViewModel.class);

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
        getReferralLink();
        binding.checkBtn.setOnClickListener(view -> {
            getUser();
        });

        binding.copyBtn.setOnClickListener(view -> {
            String shareText = new StringBuilder().append("Come to POW Network App and Earn POW Coin Use my referral link. ")

                    .append("https://play.google.com/store/apps/details?id=com.pow.networkapp&referrer=").append(firebaseUser.getUid()).toString();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(intent, "Share Via"));


        });


    }

    private void getReferralLink(){
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);

                            if (user != null) {
                                binding.referralLink.setText(user.getReferralLink());

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void initRecycler(){
        userArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        referralAdapter = new UserAdapter(userArrayList,this,this);
        binding.recyclerView.setAdapter(referralAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getUser(){
        viewModel.getReferral(binding.referralEt.getText().toString());
        viewModel.getUser().observe(this, posts -> {
            userArrayList.clear();
            userArrayList.addAll(posts);
            referralAdapter.notifyDataSetChanged();

        });

    }



    private void saveUsers(int position){

        if (!binding.referralLink.getText().toString().equals(userArrayList.get(position).getReferralLink())){
            Query query = FirebaseDatabase.getInstance().getReference().child("Referrals")
                    .child(userArrayList.get(position).getUserId())
                    .orderByChild("receiverId").equalTo(firebaseUser.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount()>0){
                        StyleableToast.makeText(InviteActivity.this,"You have used before", R.style.customToast).show();
                    }else {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("inviterId",userArrayList.get(position).getUserId());
                        hashMap.put("receiverId",firebaseUser.getUid());

                        FirebaseDatabase.getInstance()
                                .getReference().child("Referrals").child(userArrayList.get(position).getUserId()).child(firebaseUser.getUid())
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        sendPoint(userArrayList.get(position).getBalance(),userArrayList.get(position).getReferral(),userArrayList.get(position).getUserId());

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            StyleableToast.makeText(InviteActivity.this,"You can't use your own link :)",R.style.customToast).show();
        }

    }



    private void sendPoint(int balance,int referral,String userId){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("balance",balance + 50);
        hashMap.put("referral",referral + 50);

        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(userId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        StyleableToast.makeText(InviteActivity.this,"Successfully Sent!", R.style.customToast).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InviteActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onClick(int position) {

        saveUsers(position);
    }



}