package com.vdebergue.smsrelay;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vince on 30/10/13.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver{
    private static final String TAG = "Atez";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()) {

            if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                if ("smsToPhone".equals(extras.getString("type"))) {
                    String message = extras.getString("text");
                    String number = extras.getString("number");
                    Log.d(TAG, "Received: " + message + "\nSend To " + number);
                    // send sms
                    SmsManager sms = SmsManager.getDefault();
                    ArrayList<String> smsStringArray = sms.divideMessage(message);
                    sms.sendMultipartTextMessage(number, null, smsStringArray, null, null);

                    ContentValues values = new ContentValues();
                    values.put("address", number);
                    values.put("body", message);
                    context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
                }
            }
        }
    }
}
