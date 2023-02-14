package com.pow.networkapp.repo;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.databinding.ActivityProfileBinding;
import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.model.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivityRepo {

    public ProfileActivityRepo(){
    }



    public void getUserInfo(String userId, ActivityProfileBinding binding){
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);


                            if (user != null) {
                                Picasso.get().load(user.getImage()).into(binding.profileImage);
                                binding.username.setText(user.getUsername());
                                binding.acCreated.setText(convertTime(user.getRegisterDate()));
                                binding.profileEmail.setText(user.getEmail());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String convertTime(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }



}
