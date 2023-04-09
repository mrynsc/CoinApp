package com.pow.networkapp.util;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.pow.networkapp.R;


public class NetworkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null);
            builder.setView(dialog);

            AppCompatButton btnRetry = dialog.findViewById(R.id.checkNetBtn);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setGravity(Gravity.CENTER);

            btnRetry.setOnClickListener(view -> {
                alertDialog.dismiss();
                onReceive(context, intent);
            });

        }
    }
}
