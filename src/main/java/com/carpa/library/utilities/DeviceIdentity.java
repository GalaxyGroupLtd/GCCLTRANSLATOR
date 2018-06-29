package com.carpa.library.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeviceIdentity {
    public static String LOCATION;

    public static String getLOCATION() {
        return LOCATION;
    }

    public static void setLOCATION(String location) {
        LOCATION = location;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceIMEI(Context context) {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (TextUtils.isEmpty(deviceUniqueIdentifier)) {
            deviceUniqueIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }

    @SuppressLint({"MissingPermission"})
    public static String getNetwork(Context context) {
        String network = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            network = tm.getNetworkCountryIso() + " | " + tm.getNetworkOperatorName();
        }
        return network;
    }

    @SuppressLint({"MissingPermission"})
    public static String getCountryCode(Context context) {
        String network = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            network = tm.getNetworkCountryIso();
        }
        return network;
    }

    @SuppressLint("MissingPermission")
    public static String getSerialNumber() {
        String serialNumber;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serialNumber = Build.getSerial();
        } else {
            serialNumber = Build.SERIAL;
        }
        return serialNumber;
    }

    public static String batteryPercentage(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (intent == null)
            return "";
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float) scale;
        String batteryState = isCharging ? usbCharge ? "USB_CHARGER" : acCharge ? "SOCKET_CHARGER" : "OTHER_DOCK" : "NOT_CHARGING";
        String accessDate = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Log.v("Battery", "Battery Percentage: " + batteryPct + " Level: " + level + " Scale: " + scale + " State: " + batteryState + " Access: " + accessDate);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Scale: ").append(scale).append(" | ")
                .append("Level: ").append(level).append(" | ")
                .append("Percentage: ").append(batteryPct).append(" | ")
                .append("Status: ").append(batteryState).append(" | ")
                .append("AccessTime: ").append(accessDate);
        return stringBuilder.toString();
    }
}
