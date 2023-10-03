package com.example.alert_sms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.alert_sms.R;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_FILE = "MyPrefsFile";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_ALERT_MESSAGE = "alert_message";
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;

    private EditText editTextPhoneNumber;
    private EditText editTextAlertMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextAlertMessage = findViewById(R.id.editTextAlertMessage);


        // Load saved settings
        loadSavedSettings();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the PowerButtonService when the activity is destroyed
        stopService(new Intent(this, PowerButtonService.class));
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, send the SMS
            sendAlertSMS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the SMS
                sendAlertSMS();
            } else {
                // Permission denied, handle it
                showToast("SMS permission denied. The alert SMS cannot be sent.");
            }
        }
    }

    private void saveSettings() {
        String phoneNumber = editTextPhoneNumber.getText().toString();
        String alertMessage = editTextAlertMessage.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("Please enter a phone number.");
            return;
        }

        if (TextUtils.isEmpty(alertMessage)) {
            showToast("Please enter an alert message.");
            return;
        }

        SharedPreferences settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(KEY_ALERT_MESSAGE, alertMessage);
        editor.apply();

        showToast("Settings saved.");
    }

    private void loadSavedSettings() {
        SharedPreferences settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        String phoneNumber = settings.getString(KEY_PHONE_NUMBER, "");
        String alertMessage = settings.getString(KEY_ALERT_MESSAGE, "");
        getWindow().addFlags(WindowManager.LayoutParams.PREVENT_POWER_KEY);


        editTextPhoneNumber.setText(phoneNumber);
        editTextAlertMessage.setText(alertMessage);
    }

    private void sendAlertSMS() {
        SharedPreferences settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        String phoneNumber = settings.getString(KEY_PHONE_NUMBER, "");
        String alertMessage = settings.getString(KEY_ALERT_MESSAGE, "");

        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(alertMessage)) {
            showToast("Phone number or alert message is empty. Please check settings.");
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, alertMessage, null, null);
            showToast("Alert SMS sent successfully.");
        } catch (Exception e) {
            showToast("Failed to send the alert SMS. Please check your SMS settings.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Getter methods for accessing the keys from other classes
    public static String getPrefsFile() {
        return PREFS_FILE;
    }

    public static String getKeyPhoneNumber() {
        return KEY_PHONE_NUMBER;
    }

    public static String getKeyAlertMessage() {
        return KEY_ALERT_MESSAGE;
    }
}
