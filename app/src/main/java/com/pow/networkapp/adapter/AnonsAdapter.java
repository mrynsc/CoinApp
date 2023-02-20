package com.pow.networkapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pow.networkapp.databinding.AnonsItemBinding;
import com.pow.networkapp.databinding.TransactionItemBinding;
import com.pow.networkapp.model.Anons;
import com.pow.networkapp.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnonsAdapter extends RecyclerView.Adapter<AnonsAdapter.MyHolder> {

    private List<Anons> anonsArrayList;
    private Context context;

    public AnonsAdapter(List<Anons>anonsArrayList,Context context){
        this.anonsArrayList= anonsArrayList;
        this.context=context;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        AnonsItemBinding recyclerRowBinding;

        public MyHolder(@NonNull AnonsItemBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AnonsItemBinding recyclerRowBinding = AnonsItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        Anons anons = anonsArrayList.get(position);

        holder.recyclerRowBinding.titleText.setText(anons.getTitle());
        holder.recyclerRowBinding.descText.setText(anons.getDescription());
        holder.recyclerRowBinding.timeText.setText(convertTime(anons.getTime()));




    }

    private String convertTime(String time){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }


    @Override
    public int getItemCount() {
        return null!=anonsArrayList?anonsArrayList.size():0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
