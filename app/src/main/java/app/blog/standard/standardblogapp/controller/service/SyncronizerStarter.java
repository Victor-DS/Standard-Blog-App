package app.blog.standard.standardblogapp.controller.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncronizerStarter extends BroadcastReceiver {

    //TODO Add Alarm Manager

    public SyncronizerStarter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ComponentName service = context.startService(
                    new Intent(context, Syncronizer.class));

            if (service == null)
                Log.e(getClass().getName(), "Can't start service "
                        + Syncronizer.class.getName());
        } else {
            Log.e(getClass().getName(),
                    "Recieved unexpected intent " + intent.toString());
        }
    }
}
