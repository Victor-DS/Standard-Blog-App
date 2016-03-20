package app.blog.standard.standardblogapp.model.util;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;

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

    public static void openGenericDialog(Context mContext, int title, int message, DialogInterface.OnClickListener ok) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(mContext);
        adBuilder.setMessage(message);
        adBuilder.setTitle(title);
        adBuilder.setCancelable(true);
        adBuilder.setPositiveButton(android.R.string.ok, ok);
        AlertDialog adFinal = adBuilder.create();
        adFinal.show();
    }

}
