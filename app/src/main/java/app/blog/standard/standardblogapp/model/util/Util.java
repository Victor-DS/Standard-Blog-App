package app.blog.standard.standardblogapp.model.util;

import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.activities.MainActivity;
import app.blog.standard.standardblogapp.model.Publication;

/**
 * Helper class with generic useful methods.
 *
 * @author victor
 */
public class Util extends Application{

    private static final long NUMBER_OF_HOURS_BEFORE_AUTO_SYNC = 6;

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    /**
     * Converts a Pixel value to Density Pixels.
     *
     * @param px Value in pixels you want to convert.
     * @return Value in DPs.
     */
    public static float pxToDp(float px) {
        return px / getContext().getResources().getDisplayMetrics().density;
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

    public static void openGenericDialog(Context mContext, int title, int message, DialogInterface.OnClickListener ok) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(mContext);
        adBuilder.setMessage(message);
        adBuilder.setTitle(title);
        adBuilder.setCancelable(true);
        adBuilder.setPositiveButton(android.R.string.ok, ok);
        AlertDialog adFinal = adBuilder.create();
        adFinal.show();
    }

    /**
     * Method to be used in non-activity classes to get a String from resources
     * without the need to pass a context around.
     *
     * @param stringId String identification (e.g.: R.string.name)
     * @return Returns the String with that ID in the Strings' resource.
     */
    public static String getStringById(int stringId) {
        return getContext().getResources().getString(stringId);
    }

    /**
     * Intent to open the official Facebook app. If the Facebook app is not installed then the
     * default web browser will be used.</p>
     *
     * Example usage:</p>
     * <code>newFacebookIntent("https://www.facebook.com/JRummyApps");</code></p>
     *
     * @param url
     *            The full URL to the Facebook page or profile.
     * @return An intent that will open the Facebook page/profile.
     */
    public static Intent newFacebookIntent(String url) {
        Uri uri;
        try {
            getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
            // http://stackoverflow.com/a/24547437/1048340
            uri = Uri.parse("fb://facewebmodal/f?href=" + url);
        } catch (PackageManager.NameNotFoundException e) {
            uri = Uri.parse(url);
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static Intent getFBPageIntent() {
        return newFacebookIntent(getStringById(R.string.fb_page_link));
    }

    public static <T> T[] append(T[] arr, T element) {
        final int N = arr.length;
        arr = Arrays.copyOf(arr, N + 1);
        arr[N] = element;
        return arr;
    }

    public static void sendNotification(int title, int message) {
        Intent resultIntent = new Intent(getContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        int priority = NotificationCompat.PRIORITY_DEFAULT;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(getStringById(title))
                .setContentText(getStringById(message))
                .setOngoing(false)
                .setPriority(priority)
                .setVibrate(new long[0])
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getStringById(message)))
                .setContentIntent(resultPendingIntent);

        NotificationManager nManager = (NotificationManager)
                getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(100, mBuilder.build());
    }

    public static void dismissNotification() {
        NotificationManager nManager = (NotificationManager)
                getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(100);
    }

    public static String youtubeImageURL(String videoID) {
        return "http://img.youtube.com/vi/"+videoID+"/hqdefault.jpg";
    }

    public static String getIDFromYTURL(String URL) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?" +
                "v%3D|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(URL);

        if(matcher.find()){
            return matcher.group();
        }

        return null;
    }

    public static String getImageFromYTURL(String URL) {
        return youtubeImageURL(getIDFromYTURL(URL));
    }

    public static boolean stringContainsAnyOfThese(String original, String[] strings) {
        for(String s : strings)
            if(original.contains(s))
                return true;

        return false;
    }

    //region SharedPreferences
    public static boolean shouldSynchronizeAgain() {
        String timestamp = getContext().getSharedPreferences("StandardBlogApp_SP",
                getContext().MODE_PRIVATE).getString("lastTimeSync", null);

        if(timestamp == null) return true;

        return DateHelper.numberOfHoursAgo(timestamp) >= NUMBER_OF_HOURS_BEFORE_AUTO_SYNC;
    }

    public static void hasSynced() {
        getContext().getSharedPreferences("StandardBlogApp_SP",
                getContext().MODE_PRIVATE).edit().putString("lastTimeSync",
                DateHelper.dateToTimestamp(new Date())).commit();
    }

    public static Date getLastSyncDate() {
        String timestamp = getContext().getSharedPreferences("StandardBlogApp_SP",
                getContext().MODE_PRIVATE).getString("lastTimeSync", null);

        if(timestamp == null) return null;

        return DateHelper.timestampToDate(timestamp);
    }

    public static void saveMyAd(String xml) {
        getContext().getSharedPreferences("StandardBlogApp_SP",
                getContext().MODE_PRIVATE).edit().putString("myAd", xml).commit();
    }

    public static Publication getMyAd() throws XmlPullParserException, IOException {
        return XMLParser.getPublicationsFromRSS(getContext().
                getSharedPreferences("StandardBlogApp_SP", getContext().MODE_PRIVATE)
                .getString("myAd", null)).get(0);
    }
    //endregion

    //region Google Analytics
    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ?
                    analytics.newTracker(getString(R.string.app_tracking))
                    : analytics.newTracker(getString(R.string.global_tracking));
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public void track(String screenname) {
        Tracker t;
        for(TrackerName trackerName : TrackerName.values()) {
            t = getTracker(trackerName);
            t.setScreenName(screenname);
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public void sendEvent(String category, String action) {
        Tracker t;
        for(TrackerName trackerName : TrackerName.values()) {
            t = getTracker(trackerName);
            t.send(new HitBuilders.EventBuilder()
                    .setAction(action)
                    .setCategory(category)
                    .build());
        }
    }
    //endregion

}
