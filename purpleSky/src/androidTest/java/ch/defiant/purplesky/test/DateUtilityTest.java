package ch.defiant.purplesky.test;

import junit.framework.Assert;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 */
public class DateUtilityTest {

    /**
     * Testmethod for {@link ch.defiant.purplesky.util.DateUtility#isSameDay(java.util.Date, java.util.Date)}
     */
    public void testSameDay(){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2014, 4, 3, 13, 11, 59);

        Calendar calendar2 = GregorianCalendar.getInstance();
        calendar2.set(2014, 4, 3, 23, 11, 59);

        Assert.assertTrue(DateUtility.isSameDay(calendar.getTime(), calendar2.getTime()));

        // Not same day
        calendar.set(2014, 4, 2, 23, 59, 59);
        Assert.assertFalse(DateUtility.isSameDay(calendar.getTime(), calendar2.getTime()));
    }

}
