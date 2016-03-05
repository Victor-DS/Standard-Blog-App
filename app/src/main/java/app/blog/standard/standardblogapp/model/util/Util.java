package app.blog.standard.standardblogapp.model.util;

import android.app.Application;
import android.content.Context;

/**
 * @author victor
 */
public class Util extends Application{

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static float pxToDp(float px) {
        return px / getContext().getResources().getDisplayMetrics().density;
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

}
