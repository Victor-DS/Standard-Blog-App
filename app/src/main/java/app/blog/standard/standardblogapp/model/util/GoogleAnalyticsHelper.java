package app.blog.standard.standardblogapp.model.util;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import app.blog.standard.standardblogapp.R;

/**
 * List of Helper methods to deal with Google Analytics related issues.
 *
 * @author victor
 */
public class GoogleAnalyticsHelper {

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    public static HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(Util.getContext());
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ?
                    analytics.newTracker(Util.getStringById(R.string.app_tracking))
                    : analytics.newTracker(Util.getStringById(R.string.global_tracking));
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public static void track(String screenname) {
        Tracker t;
        for(TrackerName trackerName : TrackerName.values()) {
            t = getTracker(trackerName);
            t.setScreenName(screenname);
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static void sendEvent(String category, String action) {
        Tracker t;
        for(TrackerName trackerName : TrackerName.values()) {
            t = getTracker(trackerName);
            t.send(new HitBuilders.EventBuilder()
                    .setAction(action)
                    .setCategory(category)
                    .build());
        }
    }

}
