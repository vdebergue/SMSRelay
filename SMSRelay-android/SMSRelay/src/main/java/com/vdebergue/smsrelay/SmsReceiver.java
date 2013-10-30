package com.vdebergue.smsrelay;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by vince on 29/10/13.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_EXTRA_NAME = "pdus";
    public static final String ATEZ = "Atez";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(ATEZ, "Start");

        // Get the SMS map from Intent
        Bundle extras = intent.getExtras();

        String messages = "";

        if ( extras != null )
        {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );
            for(int i = 0; i< smsExtra.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
                String messageBody = sms.getMessageBody();
                String senderPhoneNumber = sms.getOriginatingAddress();
                Log.d(ATEZ, messageBody);
                Log.d(ATEZ, senderPhoneNumber);

                // Get contact info
                ContentResolver cr = context.getContentResolver();
                Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(senderPhoneNumber));
                Cursor c = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null );
                String senderName = "";
                if(c.moveToFirst()) {
                    senderName = c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
                }
                Log.d(ATEZ, "sender:" + senderName);
                c.close();
            }


        }
    }
}
