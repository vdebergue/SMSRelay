package com.vdebergue.smsrelay;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.util.Log;

import org.apache.http.HttpResponse;

import java.util.Date;

/**
 * Created by vince on 29/10/13.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String SMS_EXTRA_NAME = "pdus";
    public static final String TAG = "Atez";

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d(TAG, "Start");

        // Get the SMS map from Intent
        Bundle extras = intent.getExtras();

        if ( extras != null )
        {
            StringBuilder m = new StringBuilder();
            String previousPhoneNumber = "";

            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );
            for (int i = 0; i< smsExtra.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
                String messageBody = sms.getMessageBody();
                String senderPhoneNumber = sms.getOriginatingAddress();
                if(previousPhoneNumber.isEmpty()) previousPhoneNumber = senderPhoneNumber;

                if (senderPhoneNumber.equals(previousPhoneNumber)) {
                    // multipart sms
                    m.append(messageBody);
                } else {
                    // new sender
                    sendSmsToServer(context, m.toString(), previousPhoneNumber);
                    previousPhoneNumber = senderPhoneNumber;
                    m = new StringBuilder();
                }
            }
            sendSmsToServer(context, m.toString(), previousPhoneNumber);
        }
    }

    private void sendSmsToServer(Context context, String messageBody, String senderPhoneNumber){
        // Get contact info
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderPhoneNumber));
        Cursor c = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null );
        String senderName = "";
        if(c.moveToFirst()) {
            senderName = c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }
        Log.d(TAG, "sender: " + senderName);
        c.close();

        // Post to server
        final String registrationId = Settings.getRegistrationId(context);
        if(registrationId != null) {
            Log.d(TAG, "Posting to server ...");
            new SMSPostTask(context, registrationId, messageBody, senderPhoneNumber, senderName).execute();
        }
    }

    private class SMSPostTask extends AsyncTask<String, Object, Object> {
        private Context context;
        private String registrationId;
        private String messageBody;
        private String senderPhoneNumber;
        private String senderName;

        SMSPostTask(Context c, String _registrationId, String _messageBody, String _senderPhoneNumber, String _senderName) {
            context = c;
            registrationId = _registrationId;
            messageBody = _messageBody;
            senderPhoneNumber = _senderPhoneNumber;
            senderName = _senderName;
        }

        @Override
        protected Object doInBackground(String... strings) {
            HttpResponse resp = ServerUtil.postSMS(context, registrationId, messageBody, senderPhoneNumber, senderName);
            Log.d(TAG, "Response: " + resp.getStatusLine().getStatusCode());
            return null;
        }
    }
}
