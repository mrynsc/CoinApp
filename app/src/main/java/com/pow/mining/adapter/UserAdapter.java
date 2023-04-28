package com.pow.mining.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pow.mining.databinding.InviterItemBinding;
import com.pow.mining.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder> {

    private final ArrayList<User> userArrayList;

    public UserAdapter(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        InviterItemBinding recyclerRowBinding;

        public MyHolder(@NonNull InviterItemBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InviterItemBinding recyclerRowBinding = InviterItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        User user = userArrayList.get(position);

        holder.recyclerRowBinding.username.setText(user.getUsername());
        if (user.getImage().equals("default")) {
            holder.recyclerRowBinding.profileImage.setImageResource(com.shashank.sony.fancydialoglib.R.drawable.ic_person_black_24dp);
        } else {
            Picasso.get().load(user.getImage()).into(holder.recyclerRowBinding.profileImage);

        }


    }


    @Override
    public int getItemCount() {
        return null != userArrayList ? userArrayList.size() : 0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
