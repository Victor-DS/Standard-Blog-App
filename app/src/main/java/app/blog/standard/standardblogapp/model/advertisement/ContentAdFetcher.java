package app.blog.standard.standardblogapp.model.advertisement;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeContentAd;

import app.blog.standard.standardblogapp.controller.adapter.holders.ContentAdViewHolder;

/**
 * @author victor
 */
public class ContentAdFetcher implements Fetcher<ContentAdViewHolder> {

    private final Object mSyncObject = new Object();
    private AdLoader mAdLoader;
    private String mAdUnitId;
    private NativeContentAd mContentAd;

    /**
     * Creates an {@link ContentAdFetcher}.
     *
     * @param adUnitId The ad unit ID used to request ads.
     */
    public ContentAdFetcher(String adUnitId) {
        this.mAdUnitId = adUnitId;
    }

    @Override
    public void fetchAd(Context context) {
        synchronized (mSyncObject) {
            if ((mAdLoader != null) && mAdLoader.isLoading()) {
                Log.d(this.getClass().getName(), "ContentAdFetcher is already loading an ad.");
                return;
            }

            // If an ad previously loaded, do nothing.
            if (mContentAd != null) {
                return;
            }

            NativeContentAd.OnContentAdLoadedListener contentAdListener =
                    new NativeContentAd.OnContentAdLoadedListener() {
                        public void onContentAdLoaded(NativeContentAd ad) {
                            mContentAd = ad;
                        }
                    };

            if (mAdLoader == null) {
                mAdLoader = new AdLoader.Builder(context, mAdUnitId)
                        .forContentAd(contentAdListener)
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                //TODO Tratar erro.
                                Log.e(this.getClass().getName(),
                                        "Content Ad Failed to load: " + errorCode);
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
    public void showAd(ContentAdViewHolder holder) {
        if(mContentAd == null) {
            holder.hideView();
            return;
        }

        holder.populateView(mContentAd);
    }
}
