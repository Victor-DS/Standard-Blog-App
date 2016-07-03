package app.blog.standard.standardblogapp.controller.adapter.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.AdChoicesView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.util.Util;

/**
 * @author victor
 */
public class FacebookAdHolder extends RecyclerView.ViewHolder implements Holder<NativeAd> {

    private View adView;

    public FacebookAdHolder(View itemView) {
        super(itemView);
        this.adView = itemView;
    }

    @Override
    public void populateView(NativeAd nativeAd) {
        // Create native UI using the ad metadata.
        adView.findViewById(R.id.cardView).setVisibility(View.VISIBLE);
        ImageView nativeAdIcon = (ImageView)adView.findViewById(R.id.ivAdIcon);
        TextView nativeAdTitle = (TextView)adView.findViewById(R.id.adTitle);
        TextView nativeAdBody = (TextView)adView.findViewById(R.id.adDescription);
        MediaView nativeAdMedia = (MediaView)adView.findViewById(R.id.mediaView);
        TextView nativeAdSocialContext = (TextView)adView.findViewById(R.id.tvSocialContext);
        Button nativeAdCallToAction = (Button)adView.findViewById(R.id.adButton);

        // Setting the Text.
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdTitle.setText(nativeAd.getAdTitle());
        nativeAdBody.setText(nativeAd.getAdBody());

        // Downloading and setting the ad icon.
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

        // Download and setting the cover image.
        NativeAd.Image adCoverImage = nativeAd.getAdCoverImage();
        nativeAdMedia.setNativeAd(nativeAd);

        // Add adChoices icon
//        AdChoicesView adChoicesView = new AdChoicesView(Util.getContext(), nativeAd, true);
//        adView.addView(adChoicesView, 0);

        nativeAd.registerViewForInteraction(adView);
    }

    @Override
    public void hideView() {
        adView.findViewById(R.id.cardView).setVisibility(View.GONE);
    }
}
