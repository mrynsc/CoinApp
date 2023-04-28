package com.pow.mining.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.onesignal.OneSignal;
import com.pow.mining.R;
import com.pow.mining.databinding.ActivityMainBinding;
import com.pow.mining.util.NetworkChangeListener;

import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(getString(R.string.one_signal_app_id));


        pd = new ProgressDialog(this, R.style.CustomDialog);
        pd.setCancelable(false);


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getResources().getString(R.string.client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this
                , googleSignInOptions);


        binding.googleLoginBtn.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 100);
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            pd.show();

            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            if (signInAccountTask.isSuccessful()) {

                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {


                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        , null);
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                        if (firebaseUser != null) {
                                            addToDatabase(firebaseUser);
                                        }


                                    } else {
                                        Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();
                                    }
                                });

                    }else {
                        System.out.println("hata boş");

                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("hata kodu 2");
            }
        }
        else {
            System.out.println("hata kodu");
        }
    }

    private void addToDatabase(FirebaseUser firebaseUser) {

//        Query query = FirebaseDatabase.getInstance()
//                .getReference().child("Users")
//                .orderByChild("email").equalTo(firebaseUser.getEmail());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getChildrenCount()<=0){
//                    String userId = firebaseAuth.getCurrentUser().getUid();
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
//                            .getReference().child("Users").child(userId);
//
//                    String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//                    HashMap<String,Object> map = new HashMap<>();
//                    map.put("username",firebaseUser.getDisplayName());
//                    map.put("email",firebaseUser.getEmail());
//                    map.put("registerDate",System.currentTimeMillis());
//                    map.put("lastSeen",System.currentTimeMillis());
//                    map.put("claimed",0);
//                    map.put("referral",0);
//                    map.put("deviceId",deviceId);
//                    map.put("balance",0);
//                    map.put("userId",userId);
//                    map.put("image",firebaseUser.getPhotoUrl().toString());
//                    map.put("accountType",0);
//                    map.put("referralStatus",0);
//                    map.put("referralLink", UUID.randomUUID().toString().substring(0,8));
//
//                    databaseReference.setValue(map).addOnSuccessListener(unused -> {
//                        SharedPreferences sharedPreferences = getSharedPreferences("PREFS",0);
//                        SharedPreferences.Editor editor=sharedPreferences.edit();
//                        editor.putString("username",firebaseUser.getDisplayName());
//                        editor.putString("email",firebaseUser.getEmail());
//                        editor.putString("image",firebaseUser.getPhotoUrl().toString());
//                        editor.apply();
//                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());
//                }else {
//                    SharedPreferences sharedPreferences = getSharedPreferences("PREFS",0);
//                    SharedPreferences.Editor editor=sharedPreferences.edit();
//                    editor.putString("username",firebaseUser.getDisplayName());
//                    editor.putString("email",firebaseUser.getEmail());
//                    editor.putString("image",firebaseUser.getPhotoUrl().toString());
//                    editor.apply();
//                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("Users");
        Query query = collectionRef.whereEqualTo("email", firebaseUser.getEmail());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("username", firebaseUser.getDisplayName());
                    map.put("email", firebaseUser.getEmail());
                    map.put("registerDate", System.currentTimeMillis());
                    map.put("lastSeen", System.currentTimeMillis());
                    map.put("claimed", 0);
                    map.put("referral", 0);
                    map.put("deviceId", deviceId);
                    map.put("balance", 0);
                    map.put("userId", userId);
                    map.put("image", firebaseUser.getPhotoUrl().toString());
                    map.put("accountType", 0);
                    map.put("referralStatus", 0);
                    map.put("referralLink", UUID.randomUUID().toString().substring(0, 8));
                    FirebaseFirestore.getInstance().collection("Users").document(userId).set(map).addOnSuccessListener(unused -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", firebaseUser.getDisplayName());
                        editor.putString("email", firebaseUser.getEmail());
                        editor.putString("image", firebaseUser.getPhotoUrl().toString());
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show());
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", firebaseUser.getDisplayName());
                    editor.putString("email", firebaseUser.getEmail());
                    editor.putString("image", firebaseUser.getPhotoUrl().toString());
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            } else {
                System.out.println("Sorgu çalıştırılamadı: " + task.getException());
            }
        });

//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (DocumentSnapshot document : task.getResult()) {
//                        if (document.exists()) {
//                            Log.d("TAG", "name already exists");
//                            Intent intent = new Intent(MainActivity.this, StartActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                        } else {
//                            String userId = firebaseAuth.getCurrentUser().getUid();
//                            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//                            HashMap<String,Object> map = new HashMap<>();
//                            map.put("username",firebaseUser.getDisplayName());
//                            map.put("email",firebaseUser.getEmail());
//                            map.put("registerDate",System.currentTimeMillis());
//                            map.put("lastSeen",System.currentTimeMillis());
//                            map.put("claimed",0);
//                            map.put("referral",0);
//                            map.put("deviceId",deviceId);
//                            map.put("balance",0);
//                            map.put("userId",userId);
//                            map.put("image",firebaseUser.getPhotoUrl().toString());
//                            map.put("accountType",0);
//                            map.put("referralStatus",0);
//                            map.put("referralLink", UUID.randomUUID().toString().substring(0,8));
//                            FirebaseFirestore.getInstance().collection("Users").document(userId).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//                } else {
//                    Log.d("TAG", "Error getting documents: ", task.getException());
//                }
//            }
//        });


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