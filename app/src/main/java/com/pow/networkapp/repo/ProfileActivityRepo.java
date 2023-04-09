package com.pow.networkapp.repo;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pow.networkapp.databinding.ActivityProfileBinding;
import com.pow.networkapp.model.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivityRepo {

    public void getUserInfo(String userId, ActivityProfileBinding binding, Activity activity) {

        FirebaseFirestore.getInstance().collection("Users").document(userId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            binding.acCreated.setText(convertTime(user.getRegisterDate()));


                            SharedPreferences preferences = activity.getSharedPreferences("PREFS", 0);
                            String username = preferences.getString("username", "");
                            String image = preferences.getString("image", "");
                            String email = preferences.getString("email", "");

                            Picasso.get().load(image).into(binding.profileImage);
                            binding.username.setText(username);
                            binding.profileEmail.setText(email);
                        }
                    }
                });


    }

    private String convertTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }


}
