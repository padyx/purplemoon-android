package ch.defiant.purplesky.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public interface ILoadingFragment {

    /**
     * Update method for the fragments UI.
     * 
     * @param content
     *            Content which the fragment can read and put into its UI, when onCreateView() is called
     * @param rootView
     *            Root view of the fragment. If the fragment has not yet been fully created (i.e. the
     *            {@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)} method has not returned yet), this must be passed along. Otherwise
     *            it may be {@code null} and the fragment should obtain it using {@link Fragment#getView()}.
     */
    public void updateUI(Object content, boolean isVisible);

}
