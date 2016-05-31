package app.blog.standard.standardblogapp.model.util;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.util.database.dao.CategoriesDAO;
import app.blog.standard.standardblogapp.model.util.database.dao.PublicationDAO;

/**
 * @author victor
 */
public class PublicationHelper {

    private static PublicationHelper ourInstance;

    public static final int DEFAULT_PAGE_SIZE = 10;
    private final String TAG = "PublicationHelper";
    private String FEED_URL;

    private Context mContext;
    private NetworkHelper mNetworkHelper;
    private PublicationDAO publicationDAO;
    private CategoriesDAO categoriesDAO;

    public static PublicationHelper getInstance(Context mContext) {
        if(ourInstance == null)
            ourInstance = new PublicationHelper(mContext);

        return ourInstance;
    }

    private PublicationHelper(Context mContext) {
        this.mContext = mContext;
        mNetworkHelper = new NetworkHelper(mContext);
        publicationDAO = PublicationDAO.getInstance(mContext);
        categoriesDAO = CategoriesDAO.getInstance(mContext);

        try {
            publicationDAO.open();
            categoriesDAO.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        FEED_URL = mContext.getString(R.string.feed_url);
    }

    public boolean sync() {
        String feed = null;

        try {
            feed = mNetworkHelper.run(FEED_URL);
        } catch (IOException e) {
            Log.e(TAG, "Error trying to download feed.");
            e.printStackTrace();
            return false;
        }

        ArrayList<Publication> aPublications = new ArrayList<>();

        try {
            aPublications.addAll(XMLParser.getPublicationsFromRSS(feed));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing feed data.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error with I/O on feed data.");
        }

        try {
            publicationDAO.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Publication latestSaved = publicationDAO.readLatest();

        int index = -1;

        if(latestSaved != null) {
            for(int publicationIndex = 0; publicationIndex < aPublications.size(); publicationIndex++) {
                if(aPublications.get(publicationIndex).getUrl().equals(latestSaved.getUrl())) {
                    index = publicationIndex;
                    break;
                }
            }
        }

        if(index > -1)
            aPublications.subList(index, aPublications.size()).clear();

        boolean response = aPublications.size() > 0;

        while(!aPublications.isEmpty())
                publicationDAO.create(aPublications.remove(aPublications.size()-1));

        //TODO Adicionar add no DB aqui!
        //Link: http://victor-ds.github.io/random/myAd.xml

        //TODO Better response type! Should return if it went well and how many (if any) new posts were synced.
        return response;
    }

    /**
     * Gets ALL publications from the Database.
     *
     * @return An array of ALL publications stored in the database.
     */
    public ArrayList<Publication> getAllPublicationsFromDatabase(boolean includeAds) {
        ArrayList<Publication> publications = publicationDAO.readAll(includeAds);

        //FIXME Not efficient
        for(int i = 0; i < publications.size(); i++)
            for(String s : publications.get(i).getCategory())
                if(s.equals(Util.getStringById(R.string.patrocinated_cateogry)))
                    publications.remove(i);

        return publications;
    }

    /**
     * Gets an array of publications in a specific range. (Ideal for infinite lists)
     *
     * @param skip Number of publications to skip when selecting.
     * @param take Number of publications to take from DB
     * @return
     */
    public ArrayList<Publication> getAllPublicationsFromDatabase(int skip, int take) {
        return publicationDAO.read(skip, take);
    }

    public int getNumberOfPublicationsSaved() { //FIXME Not efficient. AT ALL!
        return getAllPublicationsFromDatabase(true).size();
    }

    public boolean hasPublications() {
        return getAllPublicationsFromDatabase(0, 5).size() > 0;
    }

    public String[] getAllCategories() {
        return categoriesDAO.readAllCategories();
    }

    public ArrayList<Publication> getAllByCategory(String category) {
//        return publicationDAO.readAllByCategory(category); //FIXME
        return publicationDAO.readAllById(categoriesDAO.readAllPublicationsByCategory(category));
    }

    public ArrayList<Publication> getAllByCategory(String category, int skip, int take) {
        return publicationDAO.readAllById(categoriesDAO
                .readAllPublicationsByCategory(category, skip, take));
    }

    public void closeDBConnection() {
        publicationDAO.close();
        categoriesDAO.close();
    }

}
