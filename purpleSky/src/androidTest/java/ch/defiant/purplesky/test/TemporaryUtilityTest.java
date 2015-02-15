package ch.defiant.purplesky.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mockito.Mockito;

import java.util.Date;

import ch.defiant.purplesky.interfaces.IDateProvider;
import ch.defiant.purplesky.interfaces.ITemporary;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.TemporaryUtility;

/**
 * Test class for {@link ch.defiant.purplesky.util.TemporaryUtility}.
 *
 * @author Patrick Bänziger
 */
public class TemporaryUtilityTest extends TestCase {

    private Date JAN_1_2015 = DateUtility.parseSimpleDate("20150101");
    private Date DEC_31_2014 = DateUtility.parseSimpleDate("20141231");
    private Date FEB_28_2014 = DateUtility.parseSimpleDate("20140228");


    public void testIsValidWithin(){
        ITemporary temporary = createTemporary(FEB_28_2014, JAN_1_2015);
        // Between
        Assert.assertTrue(TemporaryUtility.isValid(temporary, createDateProvider(DEC_31_2014)));
        // On the bounds
        Assert.assertTrue(TemporaryUtility.isValid(temporary, createDateProvider(FEB_28_2014)));
        Assert.assertTrue(TemporaryUtility.isValid(temporary, createDateProvider(JAN_1_2015)));
    }

    public void testIsValidNullSafety(){
        ITemporary temporary = createTemporary(null, JAN_1_2015);
        Assert.assertTrue(TemporaryUtility.isValid(temporary, createDateProvider(FEB_28_2014)));

        temporary = createTemporary(FEB_28_2014, null);
        Assert.assertTrue(TemporaryUtility.isValid(temporary, createDateProvider(JAN_1_2015)));

        temporary = createTemporary(null, null);
        Assert.assertTrue(TemporaryUtility.isValid(temporary, createDateProvider(DEC_31_2014)));
    }

    public void testIsValidOutside(){
        ITemporary temporary = createTemporary(FEB_28_2014, DEC_31_2014);
        Assert.assertFalse(TemporaryUtility.isValid(temporary, createDateProvider(JAN_1_2015)));

        // Check directly after/before bounds
        Assert.assertFalse(TemporaryUtility.isValid(temporary, createDateProvider(new Date(JAN_1_2015.getTime()+1))));
        Assert.assertFalse(TemporaryUtility.isValid(temporary, createDateProvider(new Date(FEB_28_2014.getTime()-1))));
    }


    private IDateProvider createDateProvider(Date date){
        IDateProvider mock = Mockito.mock(IDateProvider.class);
        Mockito.when(mock.getDate()).thenReturn(date);
        return mock;
    }

    private ITemporary createTemporary(Date validFrom, Date validTo){
        ITemporary mock = Mockito.mock(ITemporary.class);
        Mockito.when(mock.getValidFrom()).thenReturn(validFrom);
        Mockito.when(mock.getValidTo()).thenReturn(validTo);
        return mock;
    }
}
