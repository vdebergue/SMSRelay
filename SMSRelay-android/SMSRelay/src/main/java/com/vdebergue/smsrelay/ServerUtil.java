package com.vdebergue.smsrelay;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Date;

/**
 * Created by vince on 31/10/13.
 */
public class ServerUtil {

    private static final String TAG = "Atez";

    public static HttpResponse post(Context context, JSONObject json, String endPoint) {
        final String SERVER_URL = context.getString(R.string.serveur_url);
        try {
            URL url = new URL(SERVER_URL + endPoint);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url.toURI());

            post.setEntity(new StringEntity(json.toString(), "UTF-8"));
            post.setHeader("Content-Type", "application/json");
            //post.setHeader("Accept-Encoding", "application/json");
            return httpClient.execute(post);
        } catch (Exception e) {
            Log.e(TAG, "Erreur: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static HttpResponse postRegistrationId(Context context, String registrationId) {
        JSONObject json = new JSONObject();
        try {
            json.put("registrationId", registrationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String endPoint = context.getString(R.string.registration_url);
        return post(context, json, endPoint);
    }

    public static HttpResponse postSMS(Context context, String registrationId, String SMS, String senderNumber, String senderName) {
        JSONObject json = new JSONObject();
        try {
            json.put("registrationId", registrationId);
            json.put("type", "smsFromPhone");
            json.put("sms", SMS);
            json.put("date", new Date().getTime());
            JSONObject sender = new JSONObject();
            sender.put("number", senderNumber);
            sender.put("name", senderName);
            json.put("sender", sender);
        } catch (JSONException e) {

        }
        String endPoint = context.getString(R.string.sms_url);
        return post(context, json, endPoint);
    }
}
