package com.pow.networkapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.RemoteException;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import android.view.MenuItem;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.model.User;
import com.pow.networkapp.util.NetworkChangeListener;
import com.pow.networkapp.util.PrefUtils;
import com.pow.networkapp.R;
import com.pow.networkapp.viewmodel.StartActivityViewModel;
import com.pow.networkapp.util.TimeReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InstallReferrerStateListener {

    private ActivityStartBinding binding;
    private DrawerLayout drawer;

    private FirebaseAuth firebaseAuth;
    private StartActivityViewModel viewModel;
    private FirebaseUser firebaseUser;
    private TimerState timerState;
    private PrefUtils prefUtils;
    private int MAX_TIME = 14500;
    private int tcrl = 14500;
    private int en = 0;
    private int timeToStart;
    private CountDownTimer timer1;
    private ProgressDialog pd;
    private InstallReferrerClient mReferrerClient;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        pd = new ProgressDialog(this,R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();

        mReferrerClient = InstallReferrerClient.newBuilder(this).build();
        mReferrerClient.startConnection(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        prefUtils = new PrefUtils(getApplicationContext());


        Appodeal.initialize(this, getResources().getString(R.string.appodeal_app_id), Appodeal.REWARDED_VIDEO);
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



        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean b) {

            }

            @Override
            public void onRewardedVideoFailedToLoad() {
            }

            @Override
            public void onRewardedVideoShown() {

            }

            @Override
            public void onRewardedVideoShowFailed() {

            }

            @Override
            public void onRewardedVideoFinished(double v, String s) {

            }

            @Override
            public void onRewardedVideoClosed(boolean b) {
            }

            @Override
            public void onRewardedVideoExpired() {

            }

            @Override
            public void onRewardedVideoClicked() {

            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        viewModel = new ViewModelProvider(this).get(StartActivityViewModel.class);



        initViewModels();


        binding.userImage.setOnClickListener(view -> startActivity(new Intent(this,ProfileActivity.class)));


        binding.mainProfile.startBtn.setOnClickListener(view -> {
            if (timerState == TimerState.STOPPED ) {
                prefUtils.setStartedTime((int) viewModel.getNow(this));
                Random r = new Random();
                MAX_TIME = r.nextInt(tcrl - (tcrl - 30)) + (tcrl - 30);
                binding.mainProfile.progressBarCircle.setMax(MAX_TIME);
                timeToStart = MAX_TIME;
                if (timer1 != null) {
                    timer1.cancel();
                }
                startTimer(MAX_TIME);
                timerState = TimerState.RUNNING;



            }
        });



        binding.mainProfile.telegramBtn.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/PowNetwork"))));
        binding.mainProfile.twitterBtn.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.twitter.com/Pow__Network"))));
        binding.mainProfile.instaBtn.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/pow__network"))));



    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navInvite:
                if(Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                    Appodeal.show(this, Appodeal.REWARDED_VIDEO);
                    startActivity(new Intent(this,InviteActivity.class));
                }else {
                    startActivity(new Intent(this,InviteActivity.class));
                }
                break;
            case R.id.navWallet:
                if(Appodeal.isLoaded(Appodeal.REWARDED_VIDEO)) {
                    Appodeal.show(this, Appodeal.REWARDED_VIDEO);
                    startActivity(new Intent(this,WalletActivity.class));
                }else {
                    startActivity(new Intent(this,WalletActivity.class));
                }
                break;
//            case R.id.navTransactions:
//                startActivity(new Intent(this,TransactionsActivity.class));
//                break;

            case R.id.navSupport:
                startActivity(new Intent(this,NotificationsActivity.class));

                break;

            case R.id.navAnnouncements:
                startActivity(new Intent(this,AnnouncementActivity.class));
                break;
            case R.id.navSignOut:
                firebaseAuth.signOut();
                Intent intent = new Intent(this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initViewModels(){
        viewModel.getUserInfo(this,firebaseUser.getUid(),binding);
        viewModel.getTotalUsers(binding);
        viewModel.updateLastSeen(firebaseUser.getUid(),pd);
    }


//    private long getNow() {
//        Calendar rightnow = Calendar.getInstance();
//        return rightnow.getTimeInMillis() / 1000;
//    }



    private void updatingUI() {


        if (timerState == TimerState.RUNNING) {

            int hou = (int) timeToStart / (60 * 60) % 24;
            int min = (int) timeToStart / 60 % 60;
            int sec = (int) timeToStart % 60;
            String timeString = String.format(Locale.US, "%02d  :  %02d : %02d", hou, min, sec);
            SpannableString ss = new SpannableString(timeString);
            ss.setSpan(new RelativeSizeSpan(0.2f), 2, 3, 0);
            ss.setSpan(new RelativeSizeSpan(0.2f), 5, 6, 0);
            binding.mainProfile.startBtn.setText(ss);
            binding.mainProfile.progressBarCircle.setProgress(MAX_TIME - timeToStart);
            binding.mainProfile.energyCount.setText("Wait for Next Claim!");
            binding.mainProfile.energyStatusImage.setImageResource(R.drawable.hourglass_done_svgrepo_com);
        } else {

            if (timer1 != null)
                timer1.cancel();

            binding.mainProfile.startBtn.setText("START");
            binding.mainProfile.energyCount.setText("Time to Claim!");
            binding.mainProfile.energyStatusImage.setImageResource(R.drawable.high_voltage_svgrepo_com);

        }



    }


    private void startTimer(int sec) {
        final int t = sec;
        sec = sec * 1000;
        timer1 = new CountDownTimer(sec, 1000) {
            public void onTick(long millisUntilFinished) {
                timeToStart -= 1;
                updatingUI();
            }
            public void onFinish() {
                timerState = TimerState.STOPPED;

                if(en==1) en=0;
                onTimerFinish();
                updatingUI();

            }
        }.start();
    }

    private void initTimer() {
        long startTime = prefUtils.getStartedTime();
        if (startTime > 0) {
            MAX_TIME = prefUtils.getMaxTime();
            binding.mainProfile.progressBarCircle.setMax(MAX_TIME);
            timeToStart = (int) (MAX_TIME - (viewModel.getNow(this) - startTime));
            if (timeToStart <= 0) {
                timeToStart = MAX_TIME;
                timerState = TimerState.STOPPED;
                binding.mainProfile.progressBarCircle.setProgress(MAX_TIME);
                onTimerFinish();
            } else {
                startTimer(timeToStart);
                timerState = TimerState.RUNNING;
            }
        } else {
            timeToStart = MAX_TIME;
            timerState = TimerState.STOPPED;
            updatingUI();
        }
    }


    private void removeAlarmManager() {

        Intent intent = new Intent(this, TimeReceiver.class);

        PendingIntent sender;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sender = PendingIntent.getBroadcast(this,
                    0,  intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else {
            sender = PendingIntent.getBroadcast(this,
                    0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }




        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    private void setAlarmManager() {
        Calendar c = Calendar.getInstance();
        long wakeUpTime = (timeToStart * 1000) + c.getTimeInMillis();
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeReceiver.class);
        PendingIntent sender;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            sender = PendingIntent.getBroadcast(this,
                    0,  intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else {
            sender = PendingIntent.getBroadcast(this,
                    0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(wakeUpTime, sender), sender);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, wakeUpTime, sender);
        }


    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void onTimerFinish() {
        timeToStart = MAX_TIME;
        prefUtils.setStartedTime(0);

        updateBalance();
        updatingUI();
    }

    private void updateBalance() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);

                    HashMap<String,Object> map = new HashMap<>();
                    if (user != null) {
                        map.put("claimed",user.getClaimed() + 30);
                        map.put("balance",user.getBalance() + 30);

                    }
                    FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                            .updateChildren(map);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendPointToInviter(String userId){
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    HashMap<String,Object> map = new HashMap<>();
                    if (user!=null){
                        map.put("balance", user.getBalance() + 50);
                        map.put("referral",user.getReferral() + 50);
                    }
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                            .updateChildren(map);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void saveUserToMyReferrals(String userId){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("inviterId",userId);
        hashMap.put("receiverId",firebaseUser.getUid());

        FirebaseDatabase.getInstance()
                .getReference().child("Referrals").child(userId).child(firebaseUser.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(unused -> {

                }).addOnFailureListener(e -> {
                });
    }
    @Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.OK:
                try {
                    ReferrerDetails response = mReferrerClient.getInstallReferrer();
                    String referrer = response.getInstallReferrer();

                    FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                User user = snapshot.getValue(User.class);
                                if (user != null && user.getReferralStatus() == 0) {
                                    if (referrer.length()==28){
                                        sendPointToInviter(referrer);
                                        saveUserToMyReferrals(referrer);
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("referralStatus", 1);
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid())
                                                .updateChildren(hashMap);
                                    }

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    System.out.println("davet " + referrer);
                    mReferrerClient.endConnection();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                break;
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                break;
            default:
                ;
        }
    }

    @Override
    public void onInstallReferrerServiceDisconnected() {

    }

    private enum TimerState {
        STOPPED,
        RUNNING
    }

    @Override
    protected void onPause() {
        if (timerState == TimerState.RUNNING) {
            timer1.cancel();
            prefUtils.setMaxTime(MAX_TIME);
            setAlarmManager();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        initTimer();
        updatingUI();
        removeAlarmManager();
        super.onResume();
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