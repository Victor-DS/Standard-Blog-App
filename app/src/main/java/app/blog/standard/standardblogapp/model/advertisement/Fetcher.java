package app.blog.standard.standardblogapp.model.advertisement;

import android.content.Context;

/**
 * @author victor
 */
public interface Fetcher<T> {

    void fetchAd(Context context);

    void showAd(T holder);
}
