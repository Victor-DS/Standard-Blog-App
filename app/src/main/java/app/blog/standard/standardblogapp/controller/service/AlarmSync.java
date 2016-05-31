package app.blog.standard.standardblogapp.controller.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import app.blog.standard.standardblogapp.model.util.PreferenceHelper;

public class AlarmSync extends BroadcastReceiver {

    private final int MINUTES = 60;
    private final int HOURS = 1000 * MINUTES * 60;
    private final int MINUTES_AFTER_BOOT = 5 * MINUTES;

    private final int REPEAT_TIME = PreferenceHelper.syncInterval() * HOURS;

    public AlarmSync() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager service = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Syncronizer.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.SECOND, MINUTES_AFTER_BOOT);

        service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(), REPEAT_TIME, pending);
    }
}
