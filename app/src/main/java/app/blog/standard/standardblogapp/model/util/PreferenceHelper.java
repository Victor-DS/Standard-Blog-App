package app.blog.standard.standardblogapp.model.util;

import android.preference.PreferenceManager;

/**
 * @author victor
 */
public class PreferenceHelper {

    public static boolean showPatrocinatedPosts() {
        return PreferenceManager.getDefaultSharedPreferences(Util.getContext())
                .getBoolean("publicity_post", false);
    }

}
