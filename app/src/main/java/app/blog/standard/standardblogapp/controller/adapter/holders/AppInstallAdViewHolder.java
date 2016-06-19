package app.blog.standard.standardblogapp.controller.adapter.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;

import java.util.List;

import app.blog.standard.standardblogapp.R;

/**
 * @author victor
 */
public class AppInstallAdViewHolder extends RecyclerView.ViewHolder
        implements Holder<NativeAppInstallAd> {

    public NativeAppInstallAdView mAdView;

    public AppInstallAdViewHolder(NativeAppInstallAdView adView) {
        super(adView);

        mAdView = adView;

        mAdView.setHeadlineView(mAdView.findViewById(R.id.contentad_headline));
        mAdView.setImageView(mAdView.findViewById(R.id.contentad_image));
        mAdView.setBodyView(mAdView.findViewById(R.id.contentad_body));
        mAdView.setCallToActionView(mAdView.findViewById(R.id.contentad_call_to_action));
        mAdView.setIconView(mAdView.findViewById(R.id.appinstall_app_icon));
        mAdView.setPriceView(mAdView.findViewById(R.id.appinstall_price));
        mAdView.setStarRatingView(mAdView.findViewById(R.id.appinstall_stars));
        mAdView.setStoreView(mAdView.findViewById(R.id.appinstall_store));
    }

    @Override
    public void populateView(NativeAppInstallAd ad) {
        ((TextView) mAdView.getHeadlineView()).setText(ad.getHeadline());
        ((TextView) mAdView.getBodyView()).setText(ad.getBody());
        ((TextView) mAdView.getPriceView()).setText(ad.getPrice());
        ((TextView) mAdView.getStoreView()).setText(ad.getStore());
        ((Button) mAdView.getCallToActionView()).setText(ad.getCallToAction());
        ((ImageView) mAdView.getIconView()).setImageDrawable(ad.getIcon().getDrawable());
        ((RatingBar) mAdView.getStarRatingView())
                .setRating(ad.getStarRating().floatValue());

        List<NativeAd.Image> images = ad.getImages();

        if (images.size() > 0) {
            ((ImageView) mAdView.getImageView())
                    .setImageDrawable(images.get(0).getDrawable());
        }

        // assign native ad object to the native view and make visible
        mAdView.setNativeAd(ad);
        mAdView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideView() {
        mAdView.setVisibility(View.GONE);
    }
}
