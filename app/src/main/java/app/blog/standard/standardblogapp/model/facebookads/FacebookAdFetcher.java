package app.blog.standard.standardblogapp.model.facebookads;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import app.blog.standard.standardblogapp.controller.adapter.holders.Holder;
import app.blog.standard.standardblogapp.model.admob.Fetcher;
import app.blog.standard.standardblogapp.model.util.GoogleAnalyticsHelper;

/**
 * @author victor
 */
public class FacebookAdFetcher implements AdListener, Fetcher<Holder> {

    private String adPlacementId;
    private NativeAd nativeAd;
    private Ad ad;
    private boolean success;

    public FacebookAdFetcher(String adPlacementId) {
        this.adPlacementId = adPlacementId;
    }

    @Override
    public void fetchAd(Context context) {
        if(success && ad != null) { //Already has an ad
            return;
        }

        nativeAd = new NativeAd(context, adPlacementId);
        nativeAd.setAdListener(this);
        nativeAd.loadAd();
    }

    @Override
    public void showAd(Holder holder) {
        if(!success || ad == null) {
            holder.hideView();
            return;
        }

        holder.populateView(nativeAd);
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        success = false;
        this.ad = ad;

        Log.e(getClass().getName(), adError.getErrorMessage());

        GoogleAnalyticsHelper.sendEvent("Ad", "Ad fail to load");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        this.ad = ad;
        success = true;

        GoogleAnalyticsHelper.sendEvent("Ad", "Ad succesfully loaded");
    }

    @Override
    public void onAdClicked(Ad ad) {
        GoogleAnalyticsHelper.sendEvent("Ad", "Ad clicked");
    }
}
