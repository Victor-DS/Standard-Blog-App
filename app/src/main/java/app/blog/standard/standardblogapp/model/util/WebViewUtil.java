package app.blog.standard.standardblogapp.model.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.Publication;

/**
 * @author victor
 */
public class WebViewUtil {

    public static String prepareHTML(String HTML) {
        return removeFooter(HTML);
    }


    private static String removeFooter(String HTML) {
        return HTML.replaceAll(Util.getStringById(R.string.post_footer_remove), "");
    }

    private static String getHTMLModel(Context mContext) throws IOException {
        InputStream is = mContext.getAssets().open("default_publication.html");
        int size = is.available();

        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer);
    }

    private static String getCSSStyle(Context mContext) throws IOException {
        InputStream is = mContext.getAssets().open("default_publication_style.css");
        int size = is.available();

        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer);
    }

    public static String getPublicationHTML(Context mContext, Publication publication) {
        String model = null;
        try {
            model = getHTMLModel(mContext);
        } catch (IOException e) {
            e.printStackTrace();
            return publication.getContent();
        }
        model = model.replace("<%TITLE%>", publication.getTitle());
        model = model.replace("<%AUTHOR%>", publication.getCreator());
        model = model.replace("<%TIME_AGO%>", publication.getHowLongAgo().getTime()
                + " " + mContext.getString(publication.getHowLongAgo().getStringInt()));
        model = model.replace("<%CONTENT%>", publication.getContent());

        try {
            model = model.replace("<%STYLE%>", getCSSStyle(mContext));
        } catch(IOException e) {
            e.printStackTrace();
        }

        model = model.replace("<%TEXT_COLOR%>",
                Util.getStringById(R.color.default_text_color).replaceFirst("ff", ""));

        model = model.replaceAll("src=\"//", "src=\"http://");
        return model;
    }
}
