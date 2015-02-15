package ch.defiant.purplesky.interfaces;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * @author Patrick Bänziger
 */
public interface IDateProvider {

    @NonNull
    Date getDate();

}
