package com.pow.networkapp.repo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pow.networkapp.R;
import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.model.MainAnons;
import com.pow.networkapp.model.Point;
import com.pow.networkapp.model.User;

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

    private final FirebaseFirestore firestore;

    public StartActivityRepo() {
        firestore = FirebaseFirestore.getInstance();
    }


    public void getUserInfo(Activity activity, String userId, ActivityStartBinding binding, NavigationView navigationView) {
        firestore.collection("Users").document(userId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {

                            binding.mainProfile.coinText.setText(new StringBuilder().append("").append(user.getClaimed()).toString());
                            binding.mainProfile.referral.setText(new StringBuilder().append("").append(user.getReferral()).toString());

                            SharedPreferences preferences = activity.getSharedPreferences("PREFS", 0);
                            String username = preferences.getString("username", "");
                            String image = preferences.getString("image", "");
                            //Picasso.get().load(image).into(binding.userImage);
                            binding.mainProfile.username.setText(username);

                            View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
                            TextView usernameHeader = headerView.findViewById(R.id.usernameHeader);
                            usernameHeader.setText(username);
                        }
                    }
                });


    }


    public void getTotalUsers(ActivityStartBinding binding) {

        CollectionReference collection = firestore.collection("Users");
        AggregateQuery countQuery = collection.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AggregateQuerySnapshot snapshot = task.getResult();
                binding.mainProfile.totalUsers.setText(new StringBuilder().append("Total POW Users: ").append(snapshot.getCount()).toString());
            }
        });
    }

    public long getNow(Activity activity) {
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

        return calendar.getTimeInMillis() / 1000;

    }


    public void updateBalance(String myId) {
        CollectionReference collection = firestore.collection("Users");
        collection.document(myId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {

                    HashMap<String, Object> map = new HashMap<>();
                    FirebaseFirestore.getInstance().collection("Points").document("Points")
                            .get().addOnSuccessListener(documentSnapshot1 -> {
                                if (documentSnapshot1.exists()) {
                                    Point point = documentSnapshot1.toObject(Point.class);
                                    if (point != null) {
                                        map.put("claimed", user.getClaimed() + point.getCounterPoint());
                                        map.put("balance", user.getBalance() + point.getCounterPoint());

                                        collection.document(myId).update(map);
                                    }
                                }
                            });
                }
            }
        });
    }




    public void getMainAnons(Activity activity, ActivityStartBinding binding) {
        DocumentReference collectionReference = firestore.collection("MainAnons").document("MainAnons");
        collectionReference.get().addOnSuccessListener(documentSnapshot -> {
            MainAnons anons = documentSnapshot.toObject(MainAnons.class);
            if (anons != null && anons.getStatus() == 1) {
                SharedPreferences sharedPreferences = activity.getSharedPreferences("PREFS", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("anons", anons.getAnons());
                editor.apply();
                SharedPreferences preferences = activity.getSharedPreferences("PREFS", 0);
                String mainAnons = preferences.getString("anons", "");
                if (mainAnons.equals("")) {
                    binding.mainProfile.infotext.setText(anons.getAnons());
                } else {
                    binding.mainProfile.infotext.setText(mainAnons);

                }
            }
        });


    }

    public void updateLastSeen(String myId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("lastSeen", System.currentTimeMillis());

        firestore.collection("Users").document(myId)
                .update(map);
    }

}
