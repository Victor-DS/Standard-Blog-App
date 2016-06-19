package app.blog.standard.standardblogapp.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.Fragments.PublicationListFragment.OnListFragmentInteractionListener;
import app.blog.standard.standardblogapp.controller.adapter.holders.PublicationViewHolder;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.controller.adapter.holders.AppInstallAdViewHolder;
import app.blog.standard.standardblogapp.controller.adapter.holders.ContentAdViewHolder;
import app.blog.standard.standardblogapp.controller.adapter.holders.Holder;
import app.blog.standard.standardblogapp.model.advertisement.MultiAdFetcher;

import java.util.ArrayList;

public class MyPublicationRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int PUBLICATION = 0;
    private final int AD_CONTENT = 1;
    private final int AD_APP = 2;

    private final ArrayList<Publication> mPublications;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    private ImageLoader oImageLoader;
    private DisplayImageOptions oDisplayImageOptions;

    public MyPublicationRecyclerViewAdapter(Context context, ArrayList<Publication> items,
                                            OnListFragmentInteractionListener listener) {
        mPublications = items;
        mListener = listener;
        mContext = context;

        oImageLoader = ImageLoader.getInstance();

        oDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration oImageConfiguration = new ImageLoaderConfiguration
                .Builder(mContext).build();

        oImageLoader.init(oImageConfiguration);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType != PUBLICATION) {
            FrameLayout frameLayout = new FrameLayout(parent.getContext());

            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(viewType == AD_APP) {
                NativeAppInstallAdView adViewApp = (NativeAppInstallAdView) inflater
                        .inflate(R.layout.row_native_ad_app, frameLayout, false);

                AppInstallAdViewHolder holder = new AppInstallAdViewHolder(adViewApp);

                return holder;
            }

            NativeContentAdView adViewContent = (NativeContentAdView) inflater
                    .inflate(R.layout.row_native_ad_content, frameLayout, false);

            ContentAdViewHolder holder = new ContentAdViewHolder(adViewContent);

            return holder;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_publication_short,
                        parent, false);
        return new PublicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == AD_CONTENT ||
                holder.getItemViewType() == AD_APP) {
            MultiAdFetcher adFetcher = mPublications.get(position).getAd();
            adFetcher.showAd((Holder) holder);
            return;
        }

        PublicationViewHolder pHolder = (PublicationViewHolder) holder;
        pHolder.populateView(mPublications.get(position), oImageLoader,
                mListener, oDisplayImageOptions);

    }

    @Override
    public int getItemViewType(int position) {
        if(!mPublications.get(position).hasNativeAds())
            return PUBLICATION;

        return mPublications.get(position).getAd().isContentAd() ?AD_CONTENT : AD_APP;
    }

    @Override
    public int getItemCount() {
        return mPublications.size();
    }
}