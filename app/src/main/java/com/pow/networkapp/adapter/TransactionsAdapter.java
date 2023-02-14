package com.pow.networkapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pow.networkapp.R;
import com.pow.networkapp.databinding.TransactionItemBinding;
import com.pow.networkapp.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyHolder> {

    private ArrayList<Transaction> transactionArrayList;
    private Context context;

    public TransactionsAdapter(ArrayList<Transaction>transactionArrayList,Context context){
        this.transactionArrayList= transactionArrayList;
        this.context=context;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TransactionItemBinding recyclerRowBinding;

        public MyHolder(@NonNull TransactionItemBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TransactionItemBinding recyclerRowBinding = TransactionItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        Transaction transaction = transactionArrayList.get(position);

        holder.recyclerRowBinding.addressItem.setText(new StringBuilder().append("To: ").append(transaction.getAddress()).toString());
        holder.recyclerRowBinding.withdrawalItem.setText(new StringBuilder().append("Withdrawal: ").append(transaction.getWithdrawal()).toString());
        holder.recyclerRowBinding.timeItem.setText(new StringBuilder().append("Time: ").append(convertTime(transaction.getTime())).toString());





    }

    private String convertTime(long time){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM k:mm");
        String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }


    @Override
    public int getItemCount() {
        return null!=transactionArrayList?transactionArrayList.size():0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
