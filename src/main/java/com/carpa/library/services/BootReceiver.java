package com.carpa.library.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.widget.Toast;

import com.carpa.library.activities.Home;
import com.carpa.library.utilities.DownloadTaskListener;
import com.carpa.library.utilities.SDCardFinder;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private AlarmManager alarm;
    private Context context;

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            this.context = context;
            Log.d("BOOT", "Library received boot completed");
            //make schedule for download tasks
            scheduleAlarm();
        }

        if(intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)){
            addShortcut(context);
        }
    }

    public void scheduleAlarm() {
        DownloadTaskListener.setSchedule(true);
        Log.d("BOOT", "Scheduling download task");
        Calendar cal = Calendar.getInstance();
        alarmIntent = new Intent(context, CloudService.class);
        alarmIntent.setAction(CloudService.ACTION_SYNC);
        pendingIntent = PendingIntent.getService(context,
                999,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), CloudService.PERIOD, pendingIntent);

        //load SDCard
        new SDCardFinder(context);
    }

    public void cancelAlarm() {
        if (alarm != null && pendingIntent != null) {
            alarm.cancel(pendingIntent);
        }
    }

    public static void addShortcut(Context context)
    {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

        ApplicationInfo appInfo = context.getApplicationInfo();

        // Shortcut name
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appInfo.name);
        shortcut.putExtra("duplicate", false); // Just create once

        // Setup activity shoud be shortcut object
        ComponentName component = new ComponentName(appInfo.packageName, appInfo.className);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(component));

        // Set shortcut icon
        Intent.ShortcutIconResource iconResource = Intent.ShortcutIconResource.fromContext(context, appInfo.icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

        context.sendBroadcast(shortcut);
        Toast.makeText(context, appInfo.name+" Shorcut created.", Toast.LENGTH_SHORT).show();
    }

    public static void deleteShortcut(Context context)
    {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");

        ApplicationInfo appInfo = context.getApplicationInfo();

        // Shortcut name
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, appInfo.name);

        ComponentName comp = new ComponentName(appInfo.packageName, appInfo.className);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));

        context.sendBroadcast(shortcut);
    }
}
