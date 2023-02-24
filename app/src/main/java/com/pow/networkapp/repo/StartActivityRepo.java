package com.pow.networkapp.repo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.activities.MainActivity;
import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.model.Point;
import com.pow.networkapp.model.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class StartActivityRepo {


    public StartActivityRepo(){
    }


    public void getUserInfo(Activity activity,String userId, ActivityStartBinding binding){
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

                            }else {
                                activity.finish();
                            }

                        }else {
                            activity.finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    public void getTotalUsers(ActivityStartBinding binding){
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long users = snapshot.getChildrenCount();
                    binding.mainProfile.totalUsers.setText(new StringBuilder().append("Total POW Users: ").append(users).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public long getNow(Activity activity){
        final Calendar calendar = Calendar.getInstance();
        OkHttpClient client = new OkHttpClient();
        String url = "https://www.timeapi.io/api/Time/current/zone?timeZone=Europe/Istanbul";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    activity.runOnUiThread(() -> {
                        String resStr = null;
                        try {
                            resStr = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                        try {
                            JSONObject object = null;
                            if (resStr != null) {
                                object = new JSONObject(resStr);
                            }
                            int year = object.getInt("year");
                            int month = object.getInt("month");
                            int day = object.getInt("day");
                            int hour = object.getInt("hour");
                            int minute = object.getInt("minute");
                            calendar.set(year, month, day,
                                    hour, minute, 0);

                            System.out.println("bilgi " + calendar.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    });
                }
            }
        });

        return calendar.getTimeInMillis()/1000;

    }


    public void updateBalance(String myId){
        FirebaseDatabase.getInstance().getReference().child("Users").child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        HashMap<String,Object> map = new HashMap<>();
                        FirebaseDatabase.getInstance().getReference().child("Points").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    Point point = snapshot.getValue(Point.class);
                                    if (point!=null){
                                        map.put("claimed",user.getClaimed() + point.getCounterPoint());
                                        map.put("balance",user.getBalance() + point.getCounterPoint());
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(myId)
                                                .updateChildren(map);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void updateLastSeen(String myId){
        HashMap<String,Object> map = new HashMap<>();
        map.put("lastSeen",System.currentTimeMillis());

        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(myId).updateChildren(map).addOnSuccessListener(unused -> {
                }).addOnFailureListener(e -> {
                });
    }

}
