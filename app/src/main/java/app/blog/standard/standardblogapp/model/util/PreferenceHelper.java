package app.blog.standard.standardblogapp.model.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.blog.standard.standardblogapp.model.TimeAgo;

/**
 * @author victor
 */
public class PreferenceHelper {

    public static boolean showPatrocinatedPosts() {
        return PreferenceManager.getDefaultSharedPreferences(Util.getContext())
                .getBoolean("publicity_post", false);
    }

    public static int syncInterval() {
        return 6;
    }

    public static boolean shouldSync() {
        long timestamp = Util.getContext().getSharedPreferences("BlogAppPreferences",
                Context.MODE_PRIVATE).getLong("lastSync", 0);

        if(timestamp == 0) return true;

        Date syncDate = new Date();
        syncDate.setTime(timestamp);

        long diffInMillies = new Date().getTime() - syncDate.getTime();
        long hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return hours > 6;
    }

    public static void setLastSyncDate() {
        long timestamp = new Date().getTime();

        Util.getContext().getSharedPreferences("BlogAppPreferences",
                Context.MODE_PRIVATE).edit().putLong("lastSync", timestamp).commit();
    }

}
