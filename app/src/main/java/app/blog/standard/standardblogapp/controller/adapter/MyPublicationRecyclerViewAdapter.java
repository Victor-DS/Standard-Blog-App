package app.blog.standard.standardblogapp.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.formats.NativeContentAdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.Fragments.PublicationListFragment.OnListFragmentInteractionListener;
import app.blog.standard.standardblogapp.controller.adapter.holders.PublicationViewHolder;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.ads.ContentAdViewHolder;
import app.blog.standard.standardblogapp.model.advertisement.AdFetcher;

import java.util.ArrayList;

public class MyPublicationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int PUBLICATION = 0;
    private final int AD = 1;

    private final ArrayList<Publication> mPublications;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    private ImageLoader oImageLoader;
    private DisplayImageOptions oDisplayImageOptions;

    public MyPublicationRecyclerViewAdapter(Context context, ArrayList<Publication> items, OnListFragmentInteractionListener listener) {
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
        if(viewType == AD) {
            FrameLayout frameLayout = new FrameLayout(parent.getContext());

            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            NativeContentAdView adView = (NativeContentAdView) inflater
                    .inflate(R.layout.row_native_ad_content, frameLayout, false);

            ContentAdViewHolder holder = new ContentAdViewHolder(adView);

            return holder;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_publication_short,
                        parent, false);
        return new PublicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == AD) {
            AdFetcher adFetcher = mPublications.get(position).getAd();
            adFetcher.showAd((ContentAdViewHolder) holder);
            return;
        }

        PublicationViewHolder pHolder = (PublicationViewHolder) holder;
        pHolder.populateView(mPublications.get(position), oImageLoader,
                mListener, oDisplayImageOptions);

    }

    @Override
    public int getItemViewType(int position) {
        return mPublications.get(position).hasNativeAds() ? AD : PUBLICATION;
    }

    @Override
    public int getItemCount() {
        return mPublications.size();
    }
}