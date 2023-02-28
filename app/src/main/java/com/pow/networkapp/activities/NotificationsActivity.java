package com.pow.networkapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pow.networkapp.databinding.ActivityNotificationsBinding;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());



    }
}