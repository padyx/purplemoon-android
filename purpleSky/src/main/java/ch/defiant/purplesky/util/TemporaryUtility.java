package ch.defiant.purplesky.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import ch.defiant.purplesky.interfaces.IDateProvider;
import ch.defiant.purplesky.interfaces.ITemporary;

/**
 * @author Patrick Bänziger
 */
public class TemporaryUtility {

    public static boolean isValid(@Nullable ITemporary temporary, @NonNull IDateProvider dateProvider){
        final Date date = dateProvider.getDate();
            return temporary != null
                    && NVLUtility.nvl(temporary.getValidFrom(), date).compareTo(date) <= 0
                    && NVLUtility.nvl(temporary.getValidTo(), date).compareTo(date) >= 0;

    }

}
