package ch.defiant.purplesky.util;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ch.defiant.purplesky.core.PurpleSkyApplication;

public class DateUtility {

    private static final String TAG = DateUtility.class.getSimpleName();

    // FIXME Is this missing a 'y'?
    private static final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.US);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
    private static final java.text.DateFormat DATEFORMAT_SHORT = DateFormat.getDateFormat(PurpleSkyApplication.get());
    private static final java.text.DateFormat TIMEFORMAT = DateFormat.getTimeFormat(PurpleSkyApplication.get());

    /**
     * Returns the time string, if the day is today. Otherwise will return the time only.
     * @param d
     * @return
     */
    public static String getTimeOrDateString(Date d){
        if(d==null){
            return StringUtility.EMPTY_STRING;
        }
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.setTime(d);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        
        then.set(Calendar.HOUR_OF_DAY, 0);
        then.set(Calendar.MINUTE, 0);
        then.set(Calendar.SECOND, 0);
        then.set(Calendar.MILLISECOND, 0);

        if(now.equals(then)){
            return TIMEFORMAT.format(d);
        } else {
            return DATEFORMAT_SHORT.format(d);
        }
    }
    
    public static String getMediumDateString(Date d) {
        if (d == null)
            return StringUtility.EMPTY_STRING;

        java.text.DateFormat mediumDateFormat = DateFormat.getMediumDateFormat(PurpleSkyApplication.get());
        return mediumDateFormat.format(d);
    }

    public static String getMediumDateTimeString(Date d) {
        if (d == null)
            return StringUtility.EMPTY_STRING;

        java.text.DateFormat mediumDateFormat = DateFormat.getMediumDateFormat(PurpleSkyApplication.get());
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(PurpleSkyApplication.get());
        return mediumDateFormat.format(d) + " " + timeFormat.format(d);
    }

    public static Date getFromUnixTime(long seconds) {
        return new Date(1000L * seconds);
    }

    /**
     * Convert the passed date into its unix epoch equivalent.
     * 
     * @param d
     *            Date. May not be null
     * @return The elapsed time since the epoch (Jan 1, 1970, midnight GMT), in seconds.
     * @see Date#getTime()
     * @throws IllegalArgumentException
     *             if the date is null
     */
    public static long getUnixTime(Date d) {
        if (d == null) {
            throw new IllegalArgumentException("Date cannot be null!");
        }
        return d.getTime() / 1000;
    }

    /**
     * Parse a format in the form of <tt>yyyy-MM-dd</tt> from a string and return a date.
     * 
     * @param s
     *            String to parse
     * @return The parsed date, or <tt>null</tt> if unparseable.
     */
    public static Date parseJSONDate(String s) {
        if (s == null) {
            return null;
        }
        try {
            return jsonDateFormat.parse(s);
        } catch (ParseException e) {
            Log.e(TAG, "Error while parsing date: " + s, e);
            return null;
        }
    }

    public static boolean isSameDay(Date a, Date b){
        Calendar aDate = Calendar.getInstance();
        aDate.setTime(a);

        Calendar bDate = Calendar.getInstance();
        bDate.setTime(b);

        return aDate.get(Calendar.YEAR) == bDate.get(Calendar.YEAR) &&
                aDate.get(Calendar.DAY_OF_YEAR) == bDate.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isWithin24Hours(Date a, Date b){
        if(a == null || b == null){
            return false;
        }
        long diff =  Math.abs(a.getTime() - b.getTime());
        return diff <= 24*60*60*1000;
    }

    /**
     * Parse a format in the form of <tt>yyyy-MM-dd</tt> from a string and return a date.
     *
     * @param s
     *            String to parse
     * @return The parsed date, or <tt>null</tt> if unparseable.
     */
    public static Date parseSimpleDate(String s){
        try {
            return simpleDateFormat.parse(s);
        } catch (ParseException e) {
            Log.e(TAG, "Error while parsing date: " + s, e);
            return null;
        }
    }

}
