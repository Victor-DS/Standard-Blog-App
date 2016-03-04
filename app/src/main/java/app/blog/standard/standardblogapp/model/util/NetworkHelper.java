package app.blog.standard.standardblogapp.model.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author victor
 */
public class NetworkHelper {

    private OkHttpClient client;
    private Context mContext;
    private NetworkInfo mNetworkInfo;
    private SharedPreferences mSharedPreferences;

    public NetworkHelper(Context mContext) {
        this.mContext = mContext;
        this.client = new OkHttpClient();
        this.mNetworkInfo = ((ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
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
