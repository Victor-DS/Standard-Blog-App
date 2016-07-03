package app.blog.standard.standardblogapp.model.admob;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;

import app.blog.standard.standardblogapp.controller.adapter.holders.NativeExpressAdViewHolder;

/**
 * @author victor
 */
public class NativeExpressAdFetcher
        implements Fetcher<NativeExpressAdViewHolder>{

    private final Object mSyncObject = new Object();
    private AdLoader mAdLoader;
    private String mAdUnitId;

    public NativeExpressAdFetcher(String mAdUnitId) {
        this.mAdUnitId = mAdUnitId;
    }

    @Override
    public void fetchAd(Context context) {
        if (mAdLoader == null) {
            mAdLoader = new AdLoader.Builder(context, mAdUnitId)
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            //TODO Tratar erro.
                            Log.e(this.getClass().getName(),
                                    "Express Ad Failed to load: " + errorCode);
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

    @Override
    public void showAd(NativeExpressAdViewHolder holder) {
//        holder.populateView(null);
    }
}
