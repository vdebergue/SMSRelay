package com.vdebergue.smsrelay;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = "Atez";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        Context context = getApplicationContext();

        Button registerButton = (Button) findViewById(R.id.buttonRegister);

        if(Settings.getRegistrationId(context) != null) {
            registerButton.setText("Already registered");
        }
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        final String result = register();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            }
                        });
                        return null;
                    }
                }.execute(null, null, null);
            }
        });
    }

    private void sendRegistrationToBackend(Context c, String registrationId) {
        HttpResponse response = ServerUtil.postRegistrationId(c, registrationId);
        final int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode!= 200) {
            HttpEntity e = response.getEntity();
            String b = "unknown";
            try {
                b = EntityUtils.toString(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            final String body = b;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Server Error " + statusCode + ": "+ body,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String register() {
        Log.i(TAG, "Registering device");
        String SENDER_ID = getString(R.string.sender_id);

        Context context = getApplicationContext();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String registrationId = null;
        try {
            registrationId = gcm.register(SENDER_ID);
            Settings.storeRegistrationId(context, registrationId);
            // send registrationId
            sendRegistrationToBackend(context, registrationId);
            return "Registration Successfull";

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in registering " + e.getMessage());
            return "Registration error " + e.getMessage();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
