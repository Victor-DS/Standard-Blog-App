package app.blog.standard.standardblogapp.controller.adapter.holders;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.NativeExpressAdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.Fragments.PublicationListFragment;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.TimeAgo;
import app.blog.standard.standardblogapp.model.util.Util;

/**
 * @author victor
 */
public class PublicationViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mTitle;
    public final TextView mDescription;
    public final TextView mTimeAgo;
    public final ImageView imageIcon;
    public final LinearLayoutCompat contentLayout;
    public final CardView cardView;
    public Publication mItem;

    public PublicationViewHolder(View view) {
        super(view);
        mView = view;
        mTitle = (TextView) view.findViewById(R.id.textTitle);
        mDescription = (TextView) view.findViewById(R.id.textDescription);
        mTimeAgo = (TextView) view.findViewById(R.id.textTimeAgo);
        imageIcon = (ImageView) view.findViewById(R.id.imageViewCard);
        contentLayout = (LinearLayoutCompat) view.findViewById(R.id.contentLayout);
        cardView = (CardView) view.findViewById(R.id.cardView);
    }

    public void populateView(Publication publication, ImageLoader oImageLoader,
                             final PublicationListFragment.OnListFragmentInteractionListener mListener,
                             DisplayImageOptions oDisplayImageOptions) {
        mItem = publication;
        mTitle.setText(publication.getTitle());

//        String description = Publication.trimTrailingWhitespace(
//                Html.fromHtml(mPublications.get(position).getShortDescription(150))).toString();
        String description = publication.getDescriptionWithoutHTML();

        Log.i("Value description", description);
        mDescription.setText(description);

        if(publication.isPatrocinated())
            contentLayout.setBackgroundResource(R.color.ad_background);

        TimeAgo timeAgo = publication.getHowLongAgo();
        mTimeAgo.setText(timeAgo.getTime() + " " +
                Util.getStringById(timeAgo.getStringInt()).trim());

        ImageLoadingListener oImageLoadingListener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
//                holder.progressBar.setVisibility(View.GONE);
                imageIcon.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
//                holder.progressBar.setVisibility(View.GONE);
                imageIcon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
//                holder.progressBar.setVisibility(View.GONE);
                imageIcon.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
//                holder.progressBar.setVisibility(View.VISIBLE);
                imageIcon.setVisibility(View.GONE);
            }
        };

        String mIcon = publication.getPublicationImage();

        if(mIcon != null && !mIcon.isEmpty()) {
            oImageLoader.displayImage(mIcon, imageIcon, oDisplayImageOptions, oImageLoadingListener);
        } else {
            imageIcon.setVisibility(View.GONE);
        }

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(mItem);
                }
            }
        });

    }

    @Override
    public String toString() {
        return super.toString() + " '" + mTitle.getText() + "'";
    }
}