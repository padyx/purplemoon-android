package ch.defiant.purplesky.activities;

import android.app.Activity;

import javax.inject.Inject;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;

/**
 * @author Patrick Bänziger
 */
public abstract class BaseFragmentActivity extends Activity { // TODO PBN Rename

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;


}
