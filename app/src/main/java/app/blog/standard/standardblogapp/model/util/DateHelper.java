package app.blog.standard.standardblogapp.model.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import app.blog.standard.standardblogapp.model.TimeAgo;

/**
 * @author victor
 */
public class DateHelper {

    public static String dateToTimestamp(Date date) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(date);
    }

    public static String dateToRSSString(Date date) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

        return sdf.format(date);
    }

    public static Date timestampToDate(String timestamp) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;

        try {
            date = formatter.parse(timestamp);
        } catch (ParseException e) {
            Log.e("Publication", "Error trying to parse the date string: " + timestamp);
            e.printStackTrace();
        }

        return date;
    }

    public static Date rssStringDateToDate(String timestamp) {
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        Date date = null;

        try {
            date = formatter.parse(timestamp);
        } catch (ParseException e) {
            Log.e("Publication", "Error trying to parse the date string: " + timestamp);
            e.printStackTrace();
        }

        return date;
    }

    public static long numberOfHoursAgo(Date date) {
        long diffInMillies = new Date().getTime() - date.getTime();
        long hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return hours;
    }

    public static long numberOfHoursAgo(String timestamp) {
        return numberOfHoursAgo(timestampToDate(timestamp));
    }



}
