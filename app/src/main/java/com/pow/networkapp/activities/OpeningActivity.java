package com.pow.networkapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.appodeal.ads.Appodeal;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pow.networkapp.BuildConfig;
import com.pow.networkapp.R;
import com.pow.networkapp.model.Config;
import com.pow.networkapp.viewmodel.StartActivityViewModel;

public class OpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {
            Intent i = new Intent(OpeningActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, 2500);


//        FirebaseFirestore.getInstance().collection("Config").document("Config")
//                .get().addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Config config = documentSnapshot.toObject(Config.class);
//                        if (config != null) {
//                            String currentVersionName = BuildConfig.VERSION_NAME;
//
//
//
//                            if (!currentVersionName.equals(config.getAppVersionName())) {
//                                //Show update dialog
//                                AlertDialog.Builder builder = new AlertDialog.Builder(OpeningActivity.this);
//                                builder.setTitle("Update");
//                                builder.setMessage("Please update the app to the latest version");
//                                builder.setCancelable(false);
//                                builder.setPositiveButton("Update", (dialog, which) -> {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
//                                    startActivity(intent);
//                                    finish();
//                                });
//                                builder.show();
//                            } else{
//                                //Continue
//                                new Handler().postDelayed(() -> {
//                                    Intent i = new Intent(OpeningActivity.this, MainActivity.class);
//                                    startActivity(i);
//                                    finish();
//                                }, 1500);
//                            }
//                        }
//                    }
//                });


    }
}