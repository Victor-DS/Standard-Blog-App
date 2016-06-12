package app.blog.standard.standardblogapp.controller.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;

import java.net.URL;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.controller.hacky.HackyClickListener;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.util.Util;
import app.blog.standard.standardblogapp.model.util.WebViewUtil;

/**
 * @author victor
 */
public class DefaultWebviewFragment extends Fragment {

    //region variables
    private WebView mWebView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean mIsWebViewAvailable;
    private Publication publication;
    private String url;
    private boolean isURL;
    private OnWebViewClickListener mListener;
    //endregion

    //region constructors
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
    //endregion

    //region Lifecycle Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

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
        mWebView.setOnTouchListener(new HackyClickListener() {
            @Override
            public boolean OnClickListener() {
                WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();

                if(hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE) {
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
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);

        return view;
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
        super.onResume();
        mWebView.onResume();
        ((Util) getActivity().getApplication()).track("Publication View");
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
    //endregion

    //region Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.webview_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_comments:
                ((Util) getActivity().getApplication())
                        .sendEvent("Button click", "Comments");

                mWebView.loadUrl(publication.getComments());
                break;

            case R.id.action_web:
                ((Util) getActivity().getApplication())
                        .sendEvent("Button click", "View on Web");

                String link = mWebView.getUrl().contains("<html>") ?
                        publication.getUrl() : mWebView.getUrl();
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                break;

            case R.id.action_share:
                ((Util) getActivity().getApplication())
                        .sendEvent("Button click", "Share Publication");

                String linkToShare = mWebView.getUrl().contains("<html>") ?
                        publication.getUrl() : mWebView.getUrl();
                shareLink(linkToShare);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareLink(String url) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getActivity()
                .getString(R.string.share_publication_text) + url);
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }
    //endregion

    //region Helper methods
    /**
     * Convenience method for loading a url. Will fail if {@link View} is not initialised (but won't throw an {@link Exception})
     *
     * @param html
     */
    public void loadUrl(String html) {
        if (mIsWebViewAvailable) getWebView().loadUrl(url = html);
        else
            Log.w("DefaultWebViewFragment", "WebView cannot be found. Check the view and fragment have been loaded.");
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
            if(url.contains("youtube.com") || url.contains("youtu.be")
                    || url.contains("market://")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {
                view.loadUrl(url);
            }
            return true;
        }
    }
    //endregion

    //region Interaction methods
    public interface OnWebViewClickListener {
        void onImageClicked(String url);
    }
    //endregion
}