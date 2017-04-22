package com.adobe.phonegap.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.content.SharedPreferences;
import java.util.Map;

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

        SharedPreferences prefs = context.getSharedPreferences(PushPlugin.REMINDERS_LIST, Context.MODE_PRIVATE);

        if(prefs != null) {

            String keyToRemove = "";
        
            Map<String, ?> allEntries = prefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                if(entry.getValue().toString().equals(String.valueOf(notificationId))) {
                    keyToRemove = entry.getKey().toString();
                }
            }

            Log.d("REMOVING", keyToRemove);

            if(!keyToRemove.equals("")) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(keyToRemove);
                editor.commit();
            }

        }
    }
}