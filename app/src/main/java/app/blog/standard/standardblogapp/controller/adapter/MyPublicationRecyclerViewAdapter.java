package app.blog.standard.standardblogapp.controller.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.Fragments.PublicationListFragment.OnListFragmentInteractionListener;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.TimeAgo;

import java.net.URL;
import java.util.ArrayList;

public class MyPublicationRecyclerViewAdapter extends RecyclerView.Adapter<MyPublicationRecyclerViewAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_publication_short, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mPublications.get(position);
        holder.mTitle.setText(mPublications.get(position).getTitle());

        String description = Publication.trimTrailingWhitespace(
                Html.fromHtml(mPublications.get(position).getShortDescription(150))).toString();

        Log.i("Value description", description);
        holder.mDescription.setText(description);

        TimeAgo timeAgo = mPublications.get(position).getHowLongAgo();
        holder.mTimeAgo.setText(timeAgo.getTime() + " " +
                mContext.getString(timeAgo.getStringInt()).trim());

        ImageLoadingListener oImageLoadingListener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
//                holder.progressBar.setVisibility(View.GONE);
                holder.imageIcon.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
//                holder.progressBar.setVisibility(View.GONE);
                holder.imageIcon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
//                holder.progressBar.setVisibility(View.GONE);
                holder.imageIcon.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
//                holder.progressBar.setVisibility(View.VISIBLE);
                holder.imageIcon.setVisibility(View.GONE);
            }
        };

        String mIcon = mPublications.get(position).getPublicationImage();

//        if(mIcon != null && !mIcon.isEmpty()) {
//            oImageLoader.displayImage(mIcon, holder.imageIcon, oDisplayImageOptions, oImageLoadingListener);
//        } else {
//            holder.imageIcon.setVisibility(View.GONE);
//        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPublications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitle;
        public final TextView mDescription;
        public final TextView mTimeAgo;
        public final ImageView imageIcon;
//        public final ProgressBar progressBar;
        public Publication mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitle = (TextView) view.findViewById(R.id.textTitle);
            mDescription = (TextView) view.findViewById(R.id.textDescription);
            mTimeAgo = (TextView) view.findViewById(R.id.textTimeAgo);
            imageIcon = (ImageView) view.findViewById(R.id.imageViewCard);
//            progressBar = (ProgressBar) view.findViewById(R.id.progressImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescription.getText() + "'";
        }
    }
}