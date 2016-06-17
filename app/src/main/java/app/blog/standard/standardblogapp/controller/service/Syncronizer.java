package app.blog.standard.standardblogapp.controller.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.util.AlarmHelper;
import app.blog.standard.standardblogapp.model.util.PreferenceHelper;
import app.blog.standard.standardblogapp.model.util.PublicationHelper;
import app.blog.standard.standardblogapp.model.util.Util;

public class Syncronizer extends Service {

    public Syncronizer() {
    }

    //region Service methods
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //FIXME Shouldn't be called, but just in case...
        if(PreferenceHelper.syncFrequency() == AlarmHelper.NEVER)
            return;

        new Sync().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //endregion

    //Sync methods
    private class Sync extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return PublicationHelper.getInstance(Util.getContext()).sync();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Util.dismissNotification();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Util.sendNotification(R.string.app_name, R.string.synchronizing);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Util.dismissNotification();

            if(aBoolean)
                PreferenceHelper.hasSynced();

            //TODO Add Google Analytics

            //TODO Update this!
            Util.sendNotification(R.string.app_name, R.string.new_posts_synced);
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            Util.dismissNotification();
        }
    }
    //endregion

}
