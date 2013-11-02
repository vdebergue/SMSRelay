package com.vdebergue.smsrelay;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vince on 31/10/13.
 */
public class Settings {

    private static final String PREFS_REG_ID = "REG_ID";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("SMSRelay", Context.MODE_PRIVATE);
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(context);
        String registrationId = prefs.getString(PREFS_REG_ID, "");
        if(registrationId.isEmpty()) {
            return null;
        }
        return  registrationId;
    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_REG_ID, regId);
        editor.commit();
    }
}
