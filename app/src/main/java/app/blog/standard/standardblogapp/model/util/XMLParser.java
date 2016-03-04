package app.blog.standard.standardblogapp.model.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import app.blog.standard.standardblogapp.model.Publication;

/**
 * @author victor
 */
public class XMLParser {

    /**
     * Parses an RSS String in XML format into an usable array of Publication objects.
     *
     * @param rss The XML file from the RSS feed in a String format
     * @return An ArrayList containing every publication in a Publication format
     * @throws XmlPullParserException Is thrown in case of no XML string or an invalid file.
     * @throws IOException In case there's no next text (mismatch type)
     */
    public static ArrayList<Publication> getPublicationsFromRSS(String rss)
            throws XmlPullParserException, IOException {
        if(rss == null || rss.isEmpty()) throw new XmlPullParserException("No XML file");
        if(rss.startsWith("<html>")) throw new XmlPullParserException("Not a valid XML feed");

        ArrayList<Publication> aPublications = new ArrayList<>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);

        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(rss));

        int eventType = xpp.getEventType();

        Publication oCurrentPublication = null;
        ArrayList<String> aTempCategories = null;

        while(eventType != XmlPullParser.END_DOCUMENT) {
            String TAG = null;

            switch(eventType) {
                case XmlPullParser.START_TAG:
                    TAG = xpp.getName();

                    if(TAG.equals("item")) {
                        oCurrentPublication = new Publication();
                    } else if(oCurrentPublication != null) {
                        if(TAG.equals("title")) {
                            oCurrentPublication.setTitle(xpp.nextText().trim());
                        } else if(TAG.equals("link")) {
                            oCurrentPublication.setUrl(xpp.nextText().trim());
                        } else if(TAG.equals("pubDate")) {
                            oCurrentPublication.setDate(xpp.nextText().trim());
                        } else if(TAG.equals("creator")) {
                            oCurrentPublication.setCreator(xpp.nextText().trim());
                        } else if(TAG.equals("category")) {
                            if(aTempCategories == null)
                                aTempCategories = new ArrayList<>();

                            aTempCategories.add(xpp.nextText().trim());
                        } else if(TAG.equals("description")) {
                            oCurrentPublication.setDescription(xpp.nextText().trim());
                        } else if(TAG.equals("encoded")) {
                            oCurrentPublication.setContent(xpp.nextText().trim());
                        }
                    }

                    break;

                case XmlPullParser.END_TAG:
                    TAG = xpp.getName();

                    if(TAG.equals("item") && oCurrentPublication != null) {
                        if(aTempCategories != null) {
                            String[] aCategories = new String[aTempCategories.size()];
                            aCategories = aTempCategories.toArray(aCategories);
                            oCurrentPublication.setCategory(aCategories);
                            aTempCategories = null;
                        }

                        aPublications.add(oCurrentPublication);
                    }
                    break;
            }
            eventType = xpp.next();
        }

        return aPublications;
    }
}
