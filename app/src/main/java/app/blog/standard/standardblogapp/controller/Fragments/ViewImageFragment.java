package app.blog.standard.standardblogapp.controller.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;

import app.blog.standard.standardblogapp.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewImageFragment extends Fragment {

    private ImageView imageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;

    private ImageLoader oImageLoader;
    private DisplayImageOptions oDisplayImageOptions;

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

        final String url = getArguments().getString("url");

        oImageLoader.displayImage(url, imageView, oDisplayImageOptions,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        super.onLoadingCancelled(imageUri, view);
                        imageView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        //TODO Toast com mensagem de erro.
                        //TODO Toque para tentar novamente.
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
                        //TODO Toast com mensagem de erro.
                        //TODO Toque para tentar novamente.
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

}
