package com.example.alert_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.alert_sms.MainActivity;

public class PowerButtonReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            // Power button is pressed
            sendAlertSMS(context);
        }
    }

    private void sendAlertSMS(Context context) {
        SharedPreferences settings = context.getSharedPreferences(MainActivity.getPrefsFile(), Context.MODE_PRIVATE);
        String userMobileNumber = settings.getString(MainActivity.getKeyPhoneNumber(), "");
        String alertMessage = settings.getString(MainActivity.getKeyAlertMessage(), "");

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
