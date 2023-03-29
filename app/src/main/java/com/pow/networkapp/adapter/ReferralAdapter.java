package com.pow.networkapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pow.networkapp.R;
import com.pow.networkapp.databinding.InviterItemBinding;
import com.pow.networkapp.model.Referral;
import com.pow.networkapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.MyHolder> {

    private ArrayList<Referral> referralArrayList;
    private Context context;

    public ReferralAdapter(ArrayList<Referral>referralArrayList, Context context){
        this.referralArrayList= referralArrayList;
        this.context=context;
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
        InviterItemBinding recyclerRowBinding = InviterItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        Referral referral = referralArrayList.get(position);


        getUserData(referral,holder);


    }

    private void getUserData(Referral referral,MyHolder holder){

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users").child(referral.getReceiverId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        holder.recyclerRowBinding.username.setText(user.getUsername());
                        Picasso.get().load(user.getImage()).into(holder.recyclerRowBinding.profileImage);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }




    @Override
    public int getItemCount() {
        return null!=referralArrayList?referralArrayList.size():0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
