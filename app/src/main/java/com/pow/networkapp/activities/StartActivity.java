package com.pow.networkapp.activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.pow.networkapp.R;
import com.pow.networkapp.databinding.ActivityStartBinding;
import com.pow.networkapp.model.Point;
import com.pow.networkapp.model.User;
import com.pow.networkapp.util.NetworkChangeListener;
import com.pow.networkapp.util.PrefUtils;
import com.pow.networkapp.util.TimeReceiver;
import com.pow.networkapp.viewmodel.StartActivityViewModel;

import org.aviran.cookiebar2.CookieBar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;


public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InstallReferrerStateListener {

    private ActivityStartBinding binding;
    private DrawerLayout drawer;

    private FirebaseAuth firebaseAuth;
    private StartActivityViewModel viewModel;
    private FirebaseUser firebaseUser;
    private TimerState timerState;
    private PrefUtils prefUtils;
    private int MAX_TIME = 14500;
    private final int tcrl = 14500;
    private int timeToStart;
    private CountDownTimer timer1;
    private ProgressDialog pd;
    private InstallReferrerClient mReferrerClient;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    private NavigationView navigationView;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        pd = new ProgressDialog(this, R.style.CustomDialog);
        pd.setCancelable(false);
        pd.show();

        mReferrerClient = InstallReferrerClient.newBuilder(this).build();
        mReferrerClient.startConnection(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        prefUtils = new PrefUtils(getApplicationContext());
        firestore = FirebaseFirestore.getInstance();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        viewModel = new ViewModelProvider(this).get(StartActivityViewModel.class);


        //binding.userImage.setOnClickListener(view -> startActivity(new Intent(this,ProfileActivity.class)));


        binding.mainProfile.startBtn.setOnClickListener(view -> {
            if (timerState == TimerState.STOPPED) {
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


            } else {
                CookieBar.build(this)
                        .setTitle("Please Get Energy First!")
                        .setMessage("Click on Get Energy.")
                        .setBackgroundColor(R.color.app_purple)
                        .setCookiePosition(CookieBar.TOP)
                        .show();
            }
        });


        binding.mainProfile.telegramBtn.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/PowNetwork"))));
        binding.mainProfile.twitterBtn.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.twitter.com/Pow__Network"))));
        binding.mainProfile.instaBtn.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/pow__network"))));


        initViewModels();
        loadBanner();
        new Handler().postDelayed(() -> pd.dismiss(), 5000);

    }


//    private void loadAds(){
//        AdRequest adRequest = new AdRequest.Builder().build();
//        RewardedAd.load(this, getString(R.string.rewardedId),
//                adRequest, new RewardedAdLoadCallback() {
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        super.onAdFailedToLoad(loadAdError);
//                    }
//
//                    @Override
//                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                        super.onAdLoaded(rewardedAd);
//                        mRewardedAd = rewardedAd;
//
//                    }
//
//                });
//
//    }

    private void loadBanner() {
        MobileAds.initialize(StartActivity.this, initializationStatus -> {

        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.mainProfile.adView.loadAd(adRequest);

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navInvite:

                startActivity(new Intent(this, InviteActivity.class));

                break;
            case R.id.navWallet:

                startActivity(new Intent(this, WalletActivity.class));

                break;
//            case R.id.navTransactions:
//                startActivity(new Intent(this,TransactionsActivity.class));
//                break;

            case R.id.navSupport:
                startActivity(new Intent(this, NotificationsActivity.class));

                break;

            case R.id.navAnnouncements:
                startActivity(new Intent(this, AnnouncementActivity.class));
                break;
            case R.id.navSignOut:
                firebaseAuth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initViewModels() {
        viewModel.getUserInfo(this, firebaseUser.getUid(), binding, navigationView);
        viewModel.getTotalUsers(binding);
        viewModel.updateLastSeen(firebaseUser.getUid());
        viewModel.getMainAnons(this, binding);
    }


//    private long getNow() {
//        Calendar rightnow = Calendar.getInstance();
//        return rightnow.getTimeInMillis() / 1000;
//    }


    private void updatingUI() {


        if (timerState == TimerState.RUNNING) {

            int hou = timeToStart / (60 * 60) % 24;
            int min = timeToStart / 60 % 60;
            int sec = timeToStart % 60;
            String timeString = String.format(Locale.US, "%02d  :  %02d : %02d", hou, min, sec);
            SpannableString ss = new SpannableString(timeString);
            ss.setSpan(new RelativeSizeSpan(0.2f), 2, 3, 0);
            ss.setSpan(new RelativeSizeSpan(0.2f), 5, 6, 0);
            binding.mainProfile.startBtn.setText(ss);
            binding.mainProfile.progressBarCircle.setProgress(MAX_TIME - timeToStart);
            binding.mainProfile.startBtn.setEnabled(false);

            binding.mainProfile.energyStatusImage.setImageResource(R.drawable.hourglass_done_svgrepo_com);
        } else {

            if (timer1 != null)
                timer1.cancel();

            binding.mainProfile.startBtn.setText("START");
            binding.mainProfile.energyStatusImage.setImageResource(R.drawable.high_voltage_svgrepo_com);

        }


    }


    private void startTimer(int sec) {
        sec = sec * 1000;
        timer1 = new CountDownTimer(sec, 1000) {
            public void onTick(long millisUntilFinished) {
                timeToStart -= 1;
                updatingUI();
            }

            public void onFinish() {
                timerState = TimerState.STOPPED;

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
                    0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
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
                    0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
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

        viewModel.updateBalance(firebaseUser.getUid());
        updatingUI();
    }

    private void sendPointToInviter(String userId) {

        CollectionReference collectionReference = firestore.collection("Users");
        collectionReference.document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                HashMap<String, Object> map = new HashMap<>();
                if (user != null) {
                    FirebaseFirestore.getInstance().collection("Points").document("Points")
                            .get().addOnSuccessListener(documentSnapshot1 -> {
                                if (documentSnapshot1.exists()) {
                                    Point point = documentSnapshot1.toObject(Point.class);
                                    if (point != null) {
                                        map.put("balance", user.getBalance() + point.getInvitePoint());
                                        map.put("referral", user.getReferral() + point.getInvitePoint());
                                        collectionReference.document(userId).update(map);
                                    }
                                }
                            });
                }
            }
        });
    }


    private void saveUserToMyReferrals(String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("inviterId", userId);
        hashMap.put("receiverId", firebaseUser.getUid());


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

                    firestore.collection("Users").document(firebaseUser.getUid())
                            .get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    User user = documentSnapshot.toObject(User.class);
                                    if (user != null && user.getReferralStatus() == 0) {
                                        if (referrer.length() == 28) {
                                            sendPointToInviter(referrer);
                                            saveUserToMyReferrals(referrer);
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("referralStatus", 1);
                                            firestore.collection("Users").document(firebaseUser.getUid())
                                                    .update(hashMap);
                                        }
                                    }
                                }
                            });


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