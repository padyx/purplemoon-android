package ch.defiant.purplesky.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import javax.inject.Inject;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;

/**
 * Created by Chakotay on 08.05.2014.
 */
public abstract class BaseFragmentActivity extends SherlockFragmentActivity {

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;


}
