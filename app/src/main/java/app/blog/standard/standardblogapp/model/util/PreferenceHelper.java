package app.blog.standard.standardblogapp.model.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.TimeAgo;

/**
 * List of Helper methods to deal with Preferences and SharedPreferences related issues.
 *
 * @author victor
 */
public class PreferenceHelper {

    private static final long NUMBER_OF_HOURS_BEFORE_AUTO_SYNC = 6;

    public static boolean showPatrocinatedPosts() {
        return PreferenceManager.getDefaultSharedPreferences(Util.getContext())
                .getBoolean("publicity_post", false);
    }

    public static int syncFrequency() {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(Util.getContext())
                .getString("sync_frequency", "2"));
    }

    public static boolean shouldSynchronizeAgain() {
        String timestamp = Util.getContext().getSharedPreferences("StandardBlogApp_SP",
                Util.getContext().MODE_PRIVATE).getString("lastTimeSync", null);

        return timestamp == null ||
                DateHelper.numberOfHoursAgo(timestamp) >= NUMBER_OF_HOURS_BEFORE_AUTO_SYNC;
    }

    public static void hasSynced() {
        Util.getContext().getSharedPreferences("StandardBlogApp_SP",
                Util.getContext().MODE_PRIVATE).edit().putString("lastTimeSync",
                DateHelper.dateToTimestamp(new Date())).commit();
    }

    public static Date getLastSyncDate() {
        String timestamp = Util.getContext().getSharedPreferences("StandardBlogApp_SP",
                Util.getContext().MODE_PRIVATE).getString("lastTimeSync", null);

        if(timestamp == null) return null;

        return DateHelper.timestampToDate(timestamp);
    }

    public static void saveMyAd(String xml) {
        Util.getContext().getSharedPreferences("StandardBlogApp_SP",
                Util.getContext().MODE_PRIVATE).edit().putString("myAd", xml).commit();
    }

    public static Publication getMyAd() throws XmlPullParserException, IOException {
        return XMLParser.getPublicationsFromRSS(Util.getContext().
                getSharedPreferences("StandardBlogApp_SP", Util.getContext().MODE_PRIVATE)
                .getString("myAd", null)).get(0);
    }


}
