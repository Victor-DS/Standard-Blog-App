package app.blog.standard.standardblogapp.controller.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.SyncResponse;
import app.blog.standard.standardblogapp.model.util.AlarmHelper;
import app.blog.standard.standardblogapp.model.util.GoogleAnalyticsHelper;
import app.blog.standard.standardblogapp.model.util.NetworkHelper;
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

        NetworkHelper networkHelper = new NetworkHelper(this);

        if(PreferenceHelper.syncFrequency() == AlarmHelper.NEVER ||
                !networkHelper.hasPreferedConnection())
            return;

        new Sync().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //endregion

    //Sync methods
    private class Sync extends AsyncTask<Void, Void, SyncResponse> {
        @Override
        protected SyncResponse doInBackground(Void... voids) {
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
        protected void onPostExecute(SyncResponse response) {
            super.onPostExecute(response);
            Util.dismissNotification();

            if(response.isSuccess())
                PreferenceHelper.hasSynced();

            if(response.isSuccess() && response.hasNewPosts()) {
                Util.sendNotification(R.string.app_name, R.string.new_posts_synced);
                GoogleAnalyticsHelper.sendEvent("Background", "Notification new post");
            }
        }

        @Override
        protected void onCancelled(SyncResponse response) {
            super.onCancelled(response);
            Util.dismissNotification();
        }
    }
    //endregion

}
