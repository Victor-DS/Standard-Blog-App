package app.blog.standard.standardblogapp.model.util;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.SyncResponse;
import app.blog.standard.standardblogapp.model.advertisement.MultiAdFetcher;
import app.blog.standard.standardblogapp.model.util.database.dao.CategoriesDAO;
import app.blog.standard.standardblogapp.model.util.database.dao.PublicationDAO;

/**
 * @author victor
 */
public class PublicationHelper {

    private static PublicationHelper ourInstance;

    private final String MY_AD_URL = "http://victor-ds.github.io/random/myAd.xml";
    private final int ADS_EVERY_N_POSTS = 10;

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

    public SyncResponse sync() {
        int errorType = 0, newPosts = 0;

        String feed = null;

        try {
            feed = mNetworkHelper.run(FEED_URL);
        } catch (IOException e) {
            Log.e(TAG, "Error trying to download feed.");
            e.printStackTrace();
            errorType = SyncResponse.ERROR_DOWNLOADING;
        }

        ArrayList<Publication> aPublications = new ArrayList<>();

        try {
            aPublications.addAll(XMLParser.getPublicationsFromRSS(feed));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(TAG, "Error parsing feed data.");
            errorType = SyncResponse.ERROR_PARSING_DATA;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error with I/O on feed data.");
            errorType = SyncResponse.ERROR_DOWNLOADING;
        }

        try {
            publicationDAO.open();
        } catch (SQLException e) {
            e.printStackTrace();
            errorType = SyncResponse.ERROR_ON_DATABASE;
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

        newPosts = aPublications.size();

        while(!aPublications.isEmpty())
                publicationDAO.create(aPublications.remove(aPublications.size()-1));

        PreferenceHelper.saveMyAd(getMyAdXML());

        PreferenceHelper.hasSynced();

        errorType = errorType == 0 ? errorType = SyncResponse.NO_ERROR : errorType;

        return new SyncResponse(errorType, newPosts);
    }

    /**
     * Gets ALL publications from the Database.
     *
     * @return An array of ALL publications stored in the database.
     */
    public ArrayList<Publication> getAllPublicationsFromDatabase(boolean includeAds) {
        ArrayList<Publication> publications = publicationDAO.readAll(includeAds);

        //I'm not checking for patrocinated posts anymore, but I'll let it here for now.
//        for(int i = 0; i < publications.size(); i++)
//            for(String s : publications.get(i).getCategory())
//                if(s.equals(Util.getStringById(R.string.patrocinated_cateogry)))
//                    publications.remove(i);

        if(publications.size() > ADS_EVERY_N_POSTS) {
            int nAds = publications.size() / ADS_EVERY_N_POSTS;

            while(nAds > 0) {
                Publication ad = new Publication();
                MultiAdFetcher adFetcher = new MultiAdFetcher(
                        Util.getStringById(R.string.native_ad_unit_id));
                adFetcher.fetchAd(mContext);
                ad.setAd(adFetcher);
                publications.add(nAds * ADS_EVERY_N_POSTS, ad);
                nAds--;
            }
        } else {
            Publication ad = new Publication();
            MultiAdFetcher adFetcher = new MultiAdFetcher(
                    Util.getStringById(R.string.native_ad_unit_id));
            adFetcher.fetchAd(mContext);
            ad.setAd(adFetcher);
            publications.add(ad);
        }

        return addAd(publications);
    }

    /**
     * Gets an array of publications in a specific range. (Ideal for infinite lists)
     *
     * @param skip Number of publications to skip when selecting.
     * @param take Number of publications to take from DB
     * @return
     */
    public ArrayList<Publication> getAllPublicationsFromDatabase(int skip, int take) {
        ArrayList<Publication> publications = publicationDAO.read(skip, take);

        return skip == 0 ? addAd(publications) : publications;
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

    //region MyAd
    private String getMyAdXML() {
        ArrayList<Publication> ads = new ArrayList<Publication>();

        String feed = null;

        try {
            feed = mNetworkHelper.run(MY_AD_URL);
        } catch (IOException e) {
            Log.e(TAG, "Error trying to download ads.");
            e.printStackTrace();
        }

        return feed;
    }

    private ArrayList<Publication> addAd(ArrayList<Publication> publications) {
        try {
            publications.add(0, PreferenceHelper.getMyAd());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return publications;
    }
    //endregion

}
