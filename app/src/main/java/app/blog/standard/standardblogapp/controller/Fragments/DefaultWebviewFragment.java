package app.blog.standard.standardblogapp.controller.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;

import java.net.URL;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.util.WebViewUtil;

/**
 * @author victor
 */
public class DefaultWebviewFragment extends Fragment {

    //TODO Comments and Share Button
    //TODO Swipe to refresh layout

    private WebView mWebView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean mIsWebViewAvailable;
    private Publication publication;
    private String url;
    private boolean isURL;
    private OnWebViewClickListener mListener;

    public static final DefaultWebviewFragment newInstance(Publication publication) {
        DefaultWebviewFragment oFragment = new DefaultWebviewFragment();
        Bundle oBundle = new Bundle(2);
        oBundle.putParcelable("publication", publication);
        oBundle.putBoolean("isURL", false);
        oFragment.setArguments(oBundle);
        return oFragment;
    }

    public static final DefaultWebviewFragment newInstance(String url) {
        DefaultWebviewFragment oFragment = new DefaultWebviewFragment();
        Bundle oBundle = new Bundle(2);
        oBundle.putString("url", url);
        oBundle.putBoolean("isURL", true);
        oFragment.setArguments(oBundle);
        return oFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        publication = getArguments().getParcelable("publication");
        isURL = getArguments().getBoolean("isURL");
        url = getArguments().getString("url");
    }

    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default_webview, container, false);

        if (mWebView != null) {
            mWebView.destroy();
        }

        mWebView = (WebView) view.findViewById(R.id.idWebView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.idWebViewRefresh);
        TypedValue typed_value = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize,
                typed_value, true);
        swipeRefreshLayout.setProgressViewOffset(false, 0, getResources()
                .getDimensionPixelSize(typed_value.resourceId));

        mWebView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }

        });
        mWebView.setWebViewClient(new InnerWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();

                if(hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE) {
                    //TODO
                    mListener.onImageClicked(hitTestResult.getExtra());
                    return true;
                }

                return false;
            }
        });

        if(isURL)
            mWebView.loadUrl(url);
        else
            mWebView.loadData(WebViewUtil.getPublicationHTML(getActivity(), publication),
                    "text/html; charset=UTF-8", null);

        mIsWebViewAvailable = true;

        WebSettings settings = mWebView.getSettings();
        settings.setAppCachePath(getActivity().getCacheDir().getAbsolutePath());
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
//        int cacheMode = NetworkHelper.hasConnection(getActivity()) ? WebSettings.LOAD_DEFAULT :
//                WebSettings.LOAD_CACHE_ELSE_NETWORK;
//        settings.setCacheMode(cacheMode);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);

        return view;
//        return mWebView;
    }

    /**
     * Convenience method for loading a url. Will fail if {@link View} is not initialised (but won't throw an {@link Exception})
     *
     * @param html
     */
    public void loadUrl(String html) {
        if (mIsWebViewAvailable) getWebView().loadUrl(url = html);
        else
            Log.w("ImprovedWebViewFragment", "WebView cannot be found. Check the view and fragment have been loaded.");
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWebViewClickListener) {
            mListener = (OnWebViewClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWebViewClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }

    /* To ensure links open within the application */
    private class InnerWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //TODO Open youtube videos on youtube app!
            view.loadUrl(url);
            return true;
        }
    }

    public interface OnWebViewClickListener {
        void onImageClicked(String url);
    }
}