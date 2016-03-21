package app.blog.standard.standardblogapp.model.util;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import app.blog.standard.standardblogapp.R;

/**
 * Helper class with generic useful methods.
 *
 * @author victor
 */
public class Util extends Application{

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

}
