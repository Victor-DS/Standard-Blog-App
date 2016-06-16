package app.blog.standard.standardblogapp.controller.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import app.blog.standard.standardblogapp.model.util.AlarmHelper;
import app.blog.standard.standardblogapp.model.util.PreferenceHelper;

public class AlarmSync extends BroadcastReceiver {

    public AlarmSync() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if(AlarmHelper.getSyncInterval() == AlarmHelper.NEVER)
                return;

            AlarmManager service = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, Syncronizer.class);
            PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar cal = Calendar.getInstance();

            cal.add(Calendar.MILLISECOND, AlarmHelper.waitAfterBoot());

            service.setInexactRepeating(AlarmManager.RTC,
                    cal.getTimeInMillis(), AlarmHelper.getSyncInterval(), pending);
        }
    }
}
