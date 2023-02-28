package com.pow.networkapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.appodeal.ads.Appodeal;
import com.pow.networkapp.R;

public class OpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Appodeal.initialize(this, getString(R.string.appodeal_app_id),Appodeal.INTERSTITIAL|Appodeal.REWARDED_VIDEO);

        new Handler().postDelayed(() -> {

            Intent i = new Intent(OpeningActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, 1500);


    }
}