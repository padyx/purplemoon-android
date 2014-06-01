package ch.defiant.purplesky.fragments;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockFragment;

import javax.inject.Inject;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.core.PurpleSkyApplication;

/**
 * Created by Chakotay on 08.05.2014.
 */
public class BaseFragment extends SherlockFragment {

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PurpleSkyApplication.get().inject(this);
    }
}
