package com.adobe.phonegap.push;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.RemoteInput;
import android.os.StrictMode;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class BackgroundRestActionButtonHandler extends BroadcastReceiver implements PushConstants {
    private static String LOG_TAG = "PushPlugin_BackgroundRestActionButtonHandler";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Log.d(LOG_TAG, "BackgroundRestActionButtonHandler = " + extras);

        int notId = intent.getIntExtra(NOT_ID, 0);
        Log.d(LOG_TAG, "not id = " + notId);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(GCMIntentService.getAppName(context), notId);

        //JSONArray actionArray = 

        Context appcontext = context.getApplicationContext();
        String pkgName = appcontext.getPackageName();
        Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>pkgName: " + pkgName);
        SharedPreferences sharedPref = appcontext.getSharedPreferences(pkgName,context.MODE_PRIVATE);
        String url =  sharedPref.getString("pushEPUrl","");

        if (extras != null)	{
            Bundle originalExtras = extras.getBundle(PUSH_BUNDLE);

            originalExtras.putBoolean(FOREGROUND, false);
            originalExtras.putBoolean(COLDSTART, false);
            originalExtras.putString(ACTION_CALLBACK, extras.getString(CALLBACK));
            String actionCallback = extras.getString(CALLBACK);
            Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>actionCallback: " + actionCallback);

            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                String inputString = remoteInput.getCharSequence(INLINE_REPLY).toString();
                Log.d(LOG_TAG, "response: " + inputString);
                originalExtras.putString(INLINE_REPLY, inputString);
            }

            //Log.d(LOG_TAG, "BackgroundRestActionButtonHandler final extras " + originalExtras);

            String jsondata = originalExtras.getString("data");
            try{
                JSONObject jsonobj = new JSONObject(jsondata);
                Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>jsonobj" + jsonobj);

                JSONObject ajsonobj = jsonobj.getJSONObject("json");
                Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>ajsonobj" + ajsonobj);
                String nowId = ajsonobj.getString("nowid");
                url = url + "?nrid=" + nowId;

                Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>nowId" + nowId);
                Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>CALLBACK"+actionCallback);
                Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>url"+url);
                
                if(actionCallback.equals("app.pickup")){
                    HttpURLConnection connection = null;
                    boolean success = false;
                    try {
                        URL urla = new URL(url);
                        Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>set Url" + urla.toString());
                        connection = (HttpURLConnection) urla.openConnection();
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Connection", "close");
                        connection.setConnectTimeout(5000);
                        connection.connect();
                        Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>doconnection");
                        int code = connection.getResponseCode();

                        if(code == HttpURLConnection.HTTP_OK){
                            //connection.disconnect();
                            Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>connection=>responsecode: " + Integer.toString(code));
                            success = true;
                        }

                    }
                    catch (MalformedURLException e) {
                        Log.e(LOG_TAG, "BackgroundRestActionButtonHandler=>Incorrect URL");
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        Log.e(LOG_TAG, "BackgroundRestActionButtonHandler=>Failed to create new File from HTTP Content");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "BackgroundRestActionButtonHandler=>No Input can be created from http Stream");
                        e.printStackTrace();
                    }
                    finally{
                        if (connection != null) {
                            Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>disconnecting");
                            connection.disconnect();
                        }
                    }                    
                }else{
                    Log.d(LOG_TAG, "BackgroundRestActionButtonHandler=>not accept");
                }

            }
            catch(JSONException e) {
                // nope
                Log.e(LOG_TAG, "BackgroundRestActionButtonHandler=>JSONException " + e.toString());
                e.printStackTrace();
            }
            catch(Exception e) {
                // nope
                Log.e(LOG_TAG, "BackgroundRestActionButtonHandler=>Exception " + e.toString());
                e.printStackTrace();
            }

            PushPlugin.sendExtras(originalExtras);

        }
     }
}
