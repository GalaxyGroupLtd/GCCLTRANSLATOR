package com.carpa.library.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.carpa.library.utilities.DownloadTaskListener;

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
    }

    public void cancelAlarm() {
        if (alarm != null && pendingIntent != null) {
            alarm.cancel(pendingIntent);
        }
    }
}
