package com.pow.networkapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pow.networkapp.databinding.ActivityNotificationsBinding;

import java.util.Objects;

public class NotificationsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotificationsBinding binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());


    }


}