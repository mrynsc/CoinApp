package com.pow.networkapp.util;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.pow.networkapp.R;
import com.pow.networkapp.activities.MainActivity;

public class TimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(context,
                    0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(context,
                    0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id));
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        b.setSound(notification)
                .setContentTitle("Timer Has Finished!")
                .setAutoCancel(true)
                .setContentText("Come and get your coins.")
                .setSmallIcon(R.drawable.powlogo)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(context.getString(R.string.default_notification_channel_id), context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotifyMgr.createNotificationChannel(channel);
        }

        int mNotificationId = (int) System.currentTimeMillis();
        Notification n = b.build();
        mNotifyMgr.notify(mNotificationId, b.build());


    }
}
