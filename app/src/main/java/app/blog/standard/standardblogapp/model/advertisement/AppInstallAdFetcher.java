package app.blog.standard.standardblogapp.model.advertisement;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;

import app.blog.standard.standardblogapp.controller.adapter.holders.AppInstallAdViewHolder;

/**
 * @author victor
 */
public class AppInstallAdFetcher implements Fetcher<AppInstallAdViewHolder> {

    private final Object mSyncObject = new Object();
    private AdLoader mAdLoader;
    private String mAdUnitId;
    private NativeAppInstallAd mContentAd;
    private AppInstallAdViewHolder mViewHolder;

    /**
     * Creates a {@link AppInstallAdFetcher}.
     *
     * @param adUnitId The ad unit ID used to request ads.
     */
    public AppInstallAdFetcher(String adUnitId) {
        this.mAdUnitId = adUnitId;
    }

    @Override
    public void fetchAd(Context context) {
        synchronized (mSyncObject) {
            if ((mAdLoader != null) && mAdLoader.isLoading()) {
                Log.d(this.getClass().getName(), "AppAdFetcher is already loading an ad.");
                return;
            }

            // If an ad previously loaded, do nothing.
            if (mContentAd != null) {
                return;
            }

            NativeAppInstallAd.OnAppInstallAdLoadedListener contentAdListener =
                    new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        @Override
                        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                            mContentAd = nativeAppInstallAd;
                        }
                    };

            if (mAdLoader == null) {
                mAdLoader = new AdLoader.Builder(context, mAdUnitId)
                        .forAppInstallAd(contentAdListener)
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                //TODO Tratar erro.
                                Log.e(this.getClass().getName(),
                                        "App Ad Failed to load: " + errorCode);
                            }
                        }).build();
            }

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("31051D77753BA28E46AC07017557C2A4")
                    .addTestDevice("FB028C3904ACD78FFA339019542A2522")
                    .build();

            mAdLoader.loadAd(adRequest);
        }
    }

    @Override
    public void showAd(AppInstallAdViewHolder holder) {
        if(mContentAd == null) {
            holder.hideView();
            return;
        }

        holder.populateView(mContentAd);
    }
}
