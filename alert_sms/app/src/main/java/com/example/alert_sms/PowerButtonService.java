package com.example.alert_sms;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

public class PowerButtonService extends Service {

    private static final long DOUBLE_CLICK_DELAY = 1000; // milliseconds
    private long lastClickTime = 0;
    private String userMobileNumber;
    private String alertMessage;

    private BroadcastReceiver powerButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                // Power button is pressed
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < DOUBLE_CLICK_DELAY) {
                    // Double-click detected
                    sendAlertSMS(context);
                }
                lastClickTime = currentTime;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(powerButtonReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerButtonReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendAlertSMS(Context context) {
        SharedPreferences settings = context.getSharedPreferences(MainActivity.getPrefsFile(), Context.MODE_PRIVATE);
        userMobileNumber = settings.getString(MainActivity.getKeyPhoneNumber(), "");
        alertMessage = settings.getString(MainActivity.getKeyAlertMessage(), "");

        if (userMobileNumber.isEmpty() || alertMessage.isEmpty()) {
            showToast(context, "Phone number or alert message is empty. Please check settings.");
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userMobileNumber, null, alertMessage, null, null);
            showToast(context, "Alert SMS sent successfully.");
        } catch (Exception e) {
            showToast(context, "Failed to send the alert SMS. Please check your SMS settings.");
        }
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
