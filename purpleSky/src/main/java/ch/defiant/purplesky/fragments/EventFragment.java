package ch.defiant.purplesky.fragments;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.loaders.promotions.EventLoader;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Patrick Bänziger
 */
public class EventFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<Event>> {

    @Inject
    protected IPromotionAdapter m_promotionAdapter;
    private int eventId;
    private WebView m_webview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if(!intent.hasExtra(ArgumentConstants.ARG_ID)){
            throw new IllegalArgumentException("Missing event id");
        }

        eventId = intent.getIntExtra(ArgumentConstants.ARG_ID, 0);

        loadData();
    }

    private void loadData() {
        getLoaderManager().restartLoader(R.id.loader_event, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_full, container);

        m_webview = (WebView) view.findViewById(R.id.webview_full_webview);
        setUpWebview(m_webview);

        return view;
    }

    private void setUpWebview(WebView webview) {

    }

    @Override
    public Loader<Holder<Event>> onCreateLoader(int id, Bundle args) {
        getActivity().setProgressBarIndeterminateVisibility(true);

        return new EventLoader(eventId, m_promotionAdapter, getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Holder<Event>> loader, Holder<Event> data) {
        if(getView() == null){
            return;
        }
        getActivity().setProgressBarIndeterminateVisibility(false);
        getLoaderManager().destroyLoader(R.id.loader_event);

        if(data.isException()){

        } else {
            // FIXME Implement translation of event to html
           m_webview.loadData(data.getContainedObject().toString(), "text/html", "UTF-8");
        }
    }

    @Override
    public void onLoaderReset(Loader<Holder<Event>> loader) {

    }
}
