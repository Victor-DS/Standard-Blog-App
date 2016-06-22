package app.blog.standard.standardblogapp.controller.adapter.holders;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.util.Util;

/**
 * @author victor
 */
public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

    public NativeExpressAdView mAdView;

    public NativeExpressAdViewHolder(View view) {
        super(view);

        LinearLayoutCompat contentLayout = (LinearLayoutCompat) view.findViewById(R.id.contentLayout);

        //FIXME Clean up
        WindowManager wm = (WindowManager) Util.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = ((int) Util.pxToDp(size.x)) - 20;

        this.mAdView = new NativeExpressAdView(Util.getContext());
        this.mAdView.setAdSize(new AdSize(width, 250));
        this.mAdView.setAdUnitId(Util.getStringById(R.string.native_ad_unit_id));

        contentLayout.addView(mAdView);
    }

    public void fetchAndPopulateAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("31051D77753BA28E46AC07017557C2A4")
                .addTestDevice("FB028C3904ACD78FFA339019542A2522")
                .build();

        mAdView.setVisibility(View.GONE);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        mAdView.loadAd(adRequest);
    }
}
