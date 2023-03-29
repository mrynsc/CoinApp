package com.pow.networkapp.repo;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

        FirebaseFirestore.getInstance().collection("Users").document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            User user = documentSnapshot.toObject(User.class);
                            if (user!=null){
                                System.out.println("bilgi "  + user.getUsername());
                                binding.acCreated.setText(convertTime(user.getRegisterDate()));
                                Picasso.get().load(user.getImage()).into(binding.profileImage);
                                binding.username.setText(user.getUsername());
                                binding.profileEmail.setText(user.getEmail());
                            }
                        }
                    }
                });


    }

    private String convertTime(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }



}
