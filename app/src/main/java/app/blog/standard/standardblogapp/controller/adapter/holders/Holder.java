package app.blog.standard.standardblogapp.controller.adapter.holders;

import com.google.android.gms.ads.formats.NativeContentAd;

/**
 * @author victor
 */
public interface Holder<T> {

    void populateView(T ad);

    void hideView();
}
