package app.blog.standard.standardblogapp.model.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Date;

/**
 * @author victor
 */
public class NetworkHelper {

    private Context mContext;
    private NetworkInfo mNetworkInfo;
    private SharedPreferences mSharedPreferences;

    public NetworkHelper(Context mContext) {
        this.mContext = mContext;
        this.mNetworkInfo = ((ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    //FIXME Verify if it still returns a "cached" version
    public String run(String url) throws IOException {
        Connection.Response res = Jsoup.connect(url)
                .data("If-Modified-Since", DateHelper.dateToRSSString(new Date()))
                .timeout(5000)
                .execute();

        return  res.body().toString();
    }

    //FIXME Returns if the user is connected to a network, not actually if that network has a connection.
    public boolean hasConnection() {
        return mNetworkInfo != null && mNetworkInfo.isConnected();
    }

    //FIXME Returns if the user is connected to a network, not actually if that network has a connection.
    public boolean hasWifiConnection() {
        return mNetworkInfo != null
                && mNetworkInfo.isConnected()
                && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /*
     * Returns if the phone has an internet connection, in the prefered user setting.
     */
    public boolean hasPreferedConnection() {
        boolean useData = mSharedPreferences.getBoolean("use_mobile_data", false);

        if(useData) return hasConnection();

        return hasWifiConnection();
    }
}
