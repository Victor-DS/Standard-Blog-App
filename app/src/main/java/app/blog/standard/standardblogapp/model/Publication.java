package app.blog.standard.standardblogapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import app.blog.standard.standardblogapp.R;
import app.blog.standard.standardblogapp.model.util.Util;
import app.blog.standard.standardblogapp.model.util.WebViewUtil;

/**
 * @author victor
 */
public class Publication implements Parcelable {

    private String title, url, comments, creator, category[], description, content;
    private Date date;

    public Publication(String[] category, String content, String creator, String date, String description, String title, String url) {
        this.category = category;
        this.content = WebViewUtil.prepareHTML(content);
        this.creator = creator;
        this.description = WebViewUtil.prepareHTML(description);
        this.title = title;
        setUrl(url);
        setDate(date);
    }

    public Publication() {
    }

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public String getComments() {
        return comments;
    }

    public String getContent() {
        if(content == null || content.isEmpty()) return description;

        return content;
    }

    public void setContent(String content) {
        this.content = WebViewUtil.prepareHTML(content);
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String sDate) {
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        Date date = null;

        try {
            date = formatter.parse(sDate);
        } catch (ParseException e) {
            Log.e("Publication", "Error trying to parse the date string: " + sDate);
            e.printStackTrace();
        }

        this.date = date;
    }

    public String getTimestamp() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(this.date);
    }

    public void setDateFromTimestamp(String timestamp) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = formatter.parse(timestamp);
        } catch (ParseException e) {
            Log.e("Publication", "Error trying to parse the date string: " + timestamp);
            e.printStackTrace();
        }

        this.date = date;
    }

    public TimeAgo getHowLongAgo() {
        long diffInMillies = new Date().getTime() - date.getTime();
        long hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if(hours < 24) return new TimeAgo(hours, TimeAgo.HOURS);

        long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return new TimeAgo(days, TimeAgo.DAYS);
    }

//    public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
//        long diffInMillies = date2.getTime() - date1.getTime();
//        List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
//        Collections.reverse(units);
//        Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
//        long milliesRest = diffInMillies;
//        for ( TimeUnit unit : units ) {
//            long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
//            long diffInMilliesForUnit = unit.toMillis(diff);
//            milliesRest = milliesRest - diffInMilliesForUnit;
//            result.put(unit,diff);
//        }
//        return result;
//    }

    public String getDescription() {
        return description;
    }

    public String getDescriptionWithoutHTML() {
        return Html.fromHtml(description).toString();
    }

    public String getShortDescription(int nCharacters) {
        String shortDescription = description.substring(0, nCharacters);
        shortDescription = shortDescription.trim() + "...";
        return shortDescription.trim().replaceAll("\n", "");
    }

    public static CharSequence trimTrailingWhitespace(CharSequence source) {

        if(source == null)
            return "";

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i+1);
    }

    public void setDescription(String description) {
        this.description = WebViewUtil.prepareHTML(description);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.comments = "https://www.facebook.com/plugins/comments.php?api_key=" +
                "&channel_url=http%3A%2F%2Fstaticxx.facebook.com%2Fconnect%2Fxd_arbiter.php" +
                "%3Fversion%3D42%23cb%3Df1f6da56b4%26domain%3Dwww.naosalvo.com.br%26origin%3D" +
                "http%253A%252F%252Fwww.naosalvo.com.br%252Ff37722db68%26relation%3Dparent.parent" +
                "&colorscheme=light&href="+url+"&locale=pt_BR&numposts=30" +
                "&sdk=joey&skin=light&width=747";
        this.url = url;
    }

    public String getPublicationImage() { //No video icon for now...
        Document document = Jsoup.parse(this.content);
        Elements aElements = document.getElementsByTag("img");

        if(aElements.isEmpty()) return null;

        return aElements.first().absUrl("abs:src");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeArray(new Object[] {title, url, creator,
                category, description, content, date});
    }

    public static final Parcelable.Creator<Publication> CREATOR =
            new Parcelable.Creator<Publication>() {

        @Override
        public Publication createFromParcel(Parcel parcel) {
            return new Publication(parcel);
        }

        @Override
        public Publication[] newArray(int i) {
            return new Publication[i];
        }
    };

    private Publication(Parcel in) {
        Object[] objects = in.readArray(null);

        title = (String) objects[0];
        setUrl((String) objects[1]);
        creator = (String) objects[2];
        category = (String []) objects[3];
        description = WebViewUtil.prepareHTML((String) objects[4]);
        content = WebViewUtil.prepareHTML((String) objects[5]);
        date = (Date) objects[6];
    }

    public boolean isPatrocinated() {
        if(this.category == null) return false;

        for(String cateogry : this.category)
            if(cateogry.toLowerCase()
                    .equals(Util.getStringById(R.string.patrocinated_cateogry).toLowerCase()))
                return true;

        return false;
    }
}
