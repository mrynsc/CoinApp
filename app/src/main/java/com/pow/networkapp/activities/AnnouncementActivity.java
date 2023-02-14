package com.pow.networkapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pow.networkapp.R;
import com.pow.networkapp.databinding.ActivityAnnouncementBinding;

public class AnnouncementActivity extends AppCompatActivity {

    private ActivityAnnouncementBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());


    }
}