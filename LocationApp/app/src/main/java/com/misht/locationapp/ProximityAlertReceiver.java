package com.misht.locationapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
/**
 * Created by Omen23 on 20/02/2018.
 */

public class ProximityAlertReceiver extends BroadcastReceiver{
    LocationApp loc;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
            PendingIntent pIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, 0);

            String CHANNEL_ID = "my_channel_01";
            CharSequence name = "Mishell";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            //Build notification
            Notification noti = new Notification.Builder(context)
                    .setContentTitle("LocationApp")
                    .setContentText(loc.reminder)
                    .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                    .setContentIntent(pIntent)
                    .setChannelId(CHANNEL_ID)
                    .build();
           // NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, noti);
        }else if (!intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING,true)) {
            Log.d("It doesn't work",";(");
        }
    }
}
