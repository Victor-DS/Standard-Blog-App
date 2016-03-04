package app.blog.standard.standardblogapp.model.util;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author victor
 */
public class SynchronizerThread extends AsyncTask<Void, Void, Boolean> {

    private PublicationHelper publicationHelper;
    private OnSynchronizerThreadListener mListener;

    public SynchronizerThread(Context mContext) {
        publicationHelper = PublicationHelper.getInstance(mContext);
        if (mContext instanceof OnSynchronizerThreadListener) {
            mListener = (OnSynchronizerThreadListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnSynchronizerThreadListener");
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return publicationHelper.sync();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mListener.onSynchronizationDone();
    }

    public interface OnSynchronizerThreadListener {
        void onSynchronizationDone();
        void onSynchronizarionFailed();
    }
}
