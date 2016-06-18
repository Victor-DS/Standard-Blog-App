package app.blog.standard.standardblogapp.controller.Fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.util.GoogleAnalyticsHelper;
import app.blog.standard.standardblogapp.model.util.ImageHelper;
import app.blog.standard.standardblogapp.model.util.Util;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewImageFragment extends Fragment implements View.OnClickListener {

    //region Variables
    private ImageView imageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;
    private LinearLayout llSave, llShare;

    private ImageLoader oImageLoader;
    private DisplayImageOptions oDisplayImageOptions;
    //endregion

    //region Constructors
    public static ViewImageFragment newInstance(String url) {
        ViewImageFragment oFragment = new ViewImageFragment();

        Bundle args = new Bundle();
        args.putString("url", url);
        oFragment.setArguments(args);

        return oFragment;
    }

    public ViewImageFragment() {
        oImageLoader = ImageLoader.getInstance();

        oDisplayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }
    //endregion

    //region Lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_image, container, false);

        ImageLoaderConfiguration oImageConfiguration = new ImageLoaderConfiguration
                .Builder(getActivity()).build();

        oImageLoader.init(oImageConfiguration);

        progressBar = (ProgressBar) view.findViewById(R.id.idProgressViewImage);
        imageView = (ImageView) view.findViewById(R.id.iv_photo);
        mAttacher = new PhotoViewAttacher(imageView);
        llSave = (LinearLayout) view.findViewById(R.id.llSaveImage);
        llSave.setOnClickListener(this);
        llShare = (LinearLayout) view.findViewById(R.id.llShareImage);
        llShare.setOnClickListener(this);

        final String url = getArguments().getString("url");

        oImageLoader.displayImage(url, imageView, oDisplayImageOptions,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        super.onLoadingCancelled(imageUri, view);
                        imageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        imageView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        imageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        imageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GoogleAnalyticsHelper.track("Image View");
    }

    //endregion

    //region Clicks
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.llSaveImage:
                GoogleAnalyticsHelper.sendEvent("Button click", "Save Image");

                final String path = ImageHelper.saveImage(((BitmapDrawable) imageView.getDrawable())
                        .getBitmap());

                if(path == null) {
                    Toast.makeText(getActivity(), R.string.failed_to_save, Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getActivity(), R.string.image_saved, Toast.LENGTH_SHORT).show();
                break;

            case R.id.llShareImage:
                GoogleAnalyticsHelper.sendEvent("Button click", "Share Image");

                String imagePath = ImageHelper.saveImage(((BitmapDrawable) imageView.getDrawable())
                        .getBitmap());

                if(imagePath == null) {
                    Toast.makeText(getActivity(), R.string.failed_to_save, Toast.LENGTH_LONG).show();
                    return;
                }

                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse(imagePath)));

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));

                startActivity(Intent.createChooser(shareIntent,
                        Util.getStringById(R.string.share_image)));
                break;
        }
    }
    //endregion

}
