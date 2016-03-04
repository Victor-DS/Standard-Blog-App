package app.blog.standard.standardblogapp.controller.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Syncronizer extends Service {

    public Syncronizer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //TODO Synchronize here

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
