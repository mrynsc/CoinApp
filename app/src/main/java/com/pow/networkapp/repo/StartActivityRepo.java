package com.pow.networkapp.repo;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.model.MainAnons;
import com.pow.networkapp.model.Point;
import com.pow.networkapp.model.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class StartActivityRepo {

    private final FirebaseDatabase firebaseDatabase;

    public StartActivityRepo(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }


    public void getUserInfo(Activity activity,String userId, ActivityStartBinding binding){
        FirebaseFirestore.getInstance().collection("Users").document(userId)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            User user = documentSnapshot.toObject(User.class);
                            if (user!=null){
                                System.out.println("bilgi "  + user.getUsername());
                                binding.mainProfile.coinText.setText(new StringBuilder().append("").append(user.getClaimed()).toString());
                                binding.mainProfile.referral.setText(new StringBuilder().append("").append(user.getReferral()).toString());
                                Picasso.get().load(user.getImage()).into(binding.userImage);
                                binding.mainProfile.username.setText(user.getUsername());
                            }
                        }
                    }
                });


    }



    public void getTotalUsers(ActivityStartBinding binding){
//        firebaseDatabase.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    long users = snapshot.getChildrenCount();
//                    binding.mainProfile.totalUsers.setText(new StringBuilder().append("Total POW Users: ").append(users).toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = task.getResult().size();
                    binding.mainProfile.totalUsers.setText(new StringBuilder().append("Total POW Users: ").append(count).toString());
                } else {

                }
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
        FirebaseFirestore.getInstance().collection("Users").document(myId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            User user = documentSnapshot.toObject(User.class);
                            if (user!=null){

                                HashMap<String,Object> map = new HashMap<>();
                                FirebaseFirestore.getInstance().collection("Points").document("Points")
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()){
                                                    Point point = documentSnapshot.toObject(Point.class);
                                                    if (point!=null){
                                                        map.put("claimed",user.getClaimed() + point.getCounterPoint());
                                                        map.put("balance",user.getBalance() + point.getCounterPoint());

                                                        FirebaseFirestore.getInstance().collection("Users").document(myId)
                                                                .update(map);
                                                    }
                                                }
                                            }
                                        });






                            }
                        }
                    }
                });


    }





    public void getMainAnons(ActivityStartBinding binding){
        DocumentReference collectionReference = FirebaseFirestore.getInstance().collection("MainAnons").document("MainAnons");
        collectionReference.get().addOnSuccessListener(documentSnapshot -> {
            MainAnons anons = documentSnapshot.toObject(MainAnons.class);
            if (anons != null && anons.getStatus() == 1) {
                binding.mainProfile.infotext.setText(anons.getAnons());
            }
        });


    }

    public void updateLastSeen(String myId){
        HashMap<String,Object> map = new HashMap<>();
        map.put("lastSeen",System.currentTimeMillis());

        FirebaseFirestore.getInstance().collection("Users").document(myId)
                .update(map);
    }

}
