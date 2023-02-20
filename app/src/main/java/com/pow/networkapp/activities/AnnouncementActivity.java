package com.pow.networkapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pow.networkapp.R;
import com.pow.networkapp.adapter.AnonsAdapter;
import com.pow.networkapp.databinding.ActivityAnnouncementBinding;
import com.pow.networkapp.model.Anons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {

    private ActivityAnnouncementBinding binding;
    private List<Anons> anonsList;
    private AnonsAdapter anonsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(view -> finish());


        initRecycler();
        getAnnouncements();

    }

    private void initRecycler(){
        anonsList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);
        anonsAdapter = new AnonsAdapter(anonsList,this);
        binding.recyclerView.setAdapter(anonsAdapter);
    }


    private void getAnnouncements(){
        FirebaseDatabase.getInstance().getReference().child("Announcements").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    if (snapshot.exists()){
                        Anons anons = ds.getValue(Anons.class);

                        if (anons!=null && anons.getStatus()==0){
                            anonsList.add(anons);
                        }
                    }
                }
                anonsAdapter.notifyDataSetChanged();
                Collections.reverse(anonsList);
                if (anonsList.size()==0){
                    binding.lottieAnimation.setVisibility(View.VISIBLE);
                }else {
                    binding.lottieAnimation.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}