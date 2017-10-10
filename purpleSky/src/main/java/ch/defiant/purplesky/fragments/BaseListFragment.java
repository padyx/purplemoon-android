package ch.defiant.purplesky.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import dagger.android.AndroidInjection;

/**
 * Created by Chakotay on 08.05.2014.
 */
public class BaseListFragment extends ListFragment {

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }
}
