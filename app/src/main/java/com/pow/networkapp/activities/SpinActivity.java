package com.pow.networkapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pow.networkapp.R;
import com.pow.networkapp.databinding.ActivitySpinBinding;

public class SpinActivity extends AppCompatActivity {

    private ActivitySpinBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}