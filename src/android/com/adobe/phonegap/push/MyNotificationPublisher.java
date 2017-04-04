package com.adobe.phonegap.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyNotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";
    public static String APP_NAME = "app_name";
    public static String LOG_TAG = "MyNotificationPublisher";

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d(LOG_TAG, "intent onReceive fired");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        //String appName = intent.getStringExtra(APP_NAME, "HotLines");
        String appName = (String) context.getPackageManager().getApplicationLabel(context.getApplicationInfo());

        Log.d(LOG_TAG, "Notification ID: "  + String.valueOf(notificationId));
        notificationManager.notify(appName, notificationId, notification);
    }
}