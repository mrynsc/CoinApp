package com.pow.networkapp.repo;

import android.app.Activity;
import android.content.SharedPreferences;

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

    private FirebaseDatabase firebaseDatabase;

    public ProfileActivityRepo(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }



    public void getUserInfo(String userId, ActivityProfileBinding binding, Activity activity){
        firebaseDatabase.getReference().child("Users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            User user = snapshot.getValue(User.class);


                            if (user != null) {
//                                Picasso.get().load(user.getImage()).into(binding.profileImage);
//                                binding.username.setText(user.getUsername());
//                                binding.profileEmail.setText(user.getEmail());
                                binding.acCreated.setText(convertTime(user.getRegisterDate()));


                                SharedPreferences preferences=activity.getSharedPreferences("PREFS",0);
                                String username=preferences.getString("username","");
                                String image=preferences.getString("image","");
                                String email=preferences.getString("email","");

                                Picasso.get().load(image).into(binding.profileImage);
                                binding.username.setText(username);
                                binding.profileEmail.setText(email);

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
