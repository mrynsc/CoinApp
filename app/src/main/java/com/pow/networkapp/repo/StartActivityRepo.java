package com.pow.networkapp.repo;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.model.User;
import com.squareup.picasso.Picasso;

public class StartActivityRepo {


    public StartActivityRepo(){
    }



    public void getUserInfo(String userId, ActivityStartBinding binding){
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);


                            if (user != null) {
                                Picasso.get().load(user.getImage()).into(binding.userImage);
                                binding.mainProfile.username.setText(user.getUsername());
                                binding.mainProfile.coinText.setText(new StringBuilder().append("").append(user.getClaimed()).toString());
                                binding.mainProfile.referral.setText(new StringBuilder().append("").append(user.getReferral()).toString());

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
