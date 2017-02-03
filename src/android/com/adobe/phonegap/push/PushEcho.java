package com.adobe.phonegap.push;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.os.StrictMode;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.iid.InstanceID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class PushEcho {

    public static final String LOG_TAG = "PushEcho";

    public static void registerPushEcho(Context context, String urlstr, String epstr) {
        String pkgName = context.getPackageName();
        Log.d(LOG_TAG, "registerPushEcho=>pkgName: " + pkgName);
        SharedPreferences sharedPref = context.getSharedPreferences(pkgName,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("pushEchoUrl", urlstr);
        editor.putString("pushEPUrl", epstr);
        Boolean success = editor.commit();
        Log.d(LOG_TAG, "registerPushEcho=>success: " + Boolean.toString(success));
        doPushEcho(context, null);
    }

    public static boolean processPushBundle(Context context, Bundle extras){
        String msg = "";
        String data = extras.getString("data");
        JSONObject jsonData = null;
        boolean retval = false;
        if(data != null){
            try {
                JSONObject t = new JSONObject(data);
                msg = t.getString("msg");

                jsonData = t.getJSONObject("json");
                Log.d(LOG_TAG, "jsonData: " + jsonData.toString(4));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting data from payload: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if(jsonData != null ){
            String postid = "";
            int serial = 0;
            String event = "";

            try {
                postid = jsonData.getString("postid");
                serial = jsonData.getInt("serial");
                Log.d(LOG_TAG, "postid: " + postid + ", serial:" + Integer.toString(serial));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting postid and serial from jsondata: " + e.getMessage());
                e.printStackTrace();
            }
            if(postid != "" && serial != 0){
                String rRec = postid + "|" + serial;
                JSONObject echopayload = new JSONObject();
                try {
                    echopayload.put("rRec",rRec);
                    Log.d(LOG_TAG, "echopayload: " + echopayload.toString(4));
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error forming rRec json: " + e.getMessage());
                    e.printStackTrace();
                }
                String echostr = echopayload.toString();
                Log.d(LOG_TAG, "echopayload: " + echostr);
                retval = doPushEcho(context, echostr);
            }
        }
        return retval;
    }

    public static boolean doPushEcho(Context context, String echopayload){
        //Context context;
        Log.d(LOG_TAG, "doPushEcho=>echopayload: " + (echopayload != null ? echopayload : "No payload given") );
        /*if(ctx != null){
            context = ctx;
            Log.d(LOG_TAG, "doPushEcho=>context: " + "using param ctx");
        }else{
            context = getApplicationContext();
            Log.d(LOG_TAG, "doPushEcho=>context: " + "using cordova activity context");
        }*/
        String pkgName = context.getPackageName();
        Log.d(LOG_TAG, "doPushEcho=>pkgName: " + pkgName);
        SharedPreferences sharedPref = context.getSharedPreferences(pkgName,context.MODE_PRIVATE);
        String url =  sharedPref.getString("pushEchoUrl","");
        Log.d(LOG_TAG, "doPushEcho=>echoUrl: " + url);
        if(echopayload != null){
            String upayload = "";
            try {
                upayload = URLEncoder.encode(echopayload, "utf-8");
                Log.d(LOG_TAG, "doPushEcho=>upayload(UrlEncoded): " + upayload);
            } catch (UnsupportedEncodingException e) {
                Log.e(LOG_TAG, "doPushEcho=>UrlEncoding=>Error: " + e.getMessage());
                e.printStackTrace();
            }
            url = url + "?p=" + upayload;
            Log.d(LOG_TAG, "doPushEcho=>full url: " + url);
        }
        /*try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            response = httpclient.execute(new HttpGet(url));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){

                return true;
            } else{
                return false;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        HttpURLConnection connection = null;
        boolean success = false;
        try {
            URL urla = new URL(url);
            connection = (HttpURLConnection) urla.openConnection();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(5000);
            connection.connect();
            int code = connection.getResponseCode();

            if(code == HttpURLConnection.HTTP_OK){
                //connection.disconnect();
                Log.d(LOG_TAG, "doPushEcho=>connection=>responsecode: " + Integer.toString(code));
                success = true;
            }

        }
        catch (MalformedURLException e) {
            Log.e(LOG_TAG, "doPushEcho=>Incorrect URL");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "doPushEcho=>Failed to create new File from HTTP Content");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "doPushEcho=>No Input can be created from http Stream");
            e.printStackTrace();
        }finally{
            if (connection != null) {
                connection.disconnect();
            }
        }

        return success;
    }
}
