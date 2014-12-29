package ch.defiant.purplesky.fragments;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.loaders.promotions.EventLoader;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Patrick Bänziger
 */
public class EventFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<Event>> {

    @Inject
    protected IPromotionAdapter m_promotionAdapter;
    private int eventId;
    private WebView m_webview;
    private MenuItem m_registerMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

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
            // FIXME Implement
        } else {
            Event event = data.getContainedObject();
            m_webview.loadData(promoToHtml(getActivity(), event), "text/html; charset=utf-8", "UTF-8");

            m_registerMenu.setEnabled(canRegister(event));
        }
    }

    private boolean canRegister(@NonNull Event event) {
        return !event.isPreliminary() && !event.isRegistered();
    }

    @Override
    public void onLoaderReset(Loader<Holder<Event>> loader) {}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_menu, menu);
        m_registerMenu = menu.findItem(R.id.register);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item); // FIXME Implement
    }

    private static String promoToHtml(Context c, Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
        sb.append("<style>td {padding: 8px} tr:nth-child(odd) {background-color:#BBE4FA;} " +
                "tr:nth-child(even) {background-color:#AFD1E3;}</style>");
        sb.append("</head>\n");
        sb.append("<body><h2>");
        sb.append(event.getEventName());
        sb.append("</h2><p style='font-face: Verdana,Arial,sans-serif;'>");
        sb.append(event.getDescriptionHtml());
        sb.append("</p>\n");

        sb.append("<p>");
        String date = DateUtility.getMediumDateTimeString(event.getStart());
        if(event.getEnd() != null){
            date += " - ";
            if (DateUtility.isSameDay(event.getStart(), event.getEnd())){
                date += android.text.format.DateFormat.getTimeFormat(PurpleSkyApplication.get()).format(event.getEnd());
            } else {
                date += DateUtility.getMediumDateTimeString(event.getEnd());
            }
        }
        sb.append(date);
        sb.append("</p><h3>");

        sb.append(c.getString(R.string.Admission));
        sb.append("</h3><table style='border:0'>");
        if(event.isPrivate()){
            // FIXME Other admission stuff such as gender
            addRow(sb, c.getString(R.string.Admission), "Limited: Private Event");
        }
        if(event.getMinAge() != null) {
            addRow(sb, c.getString(R.string.MinimumAge), String.valueOf(event.getMinAge()));
        }
        if(event.getMaxAge() != null) {
            addRow(sb, c.getString(R.string.MaximumAge), String.valueOf(event.getMaxAge()));
        }
        addRow(sb, "Registrations", String.valueOf(event.getRegistrations()));
        sb.append("</table></body></html>");

        return sb.toString();
    }

    private static void addRow(StringBuilder sb, String firstCol, String secondCol) {
        sb.append("<tr><td>");
        sb.append(firstCol);
        sb.append("</td><td>");
        sb.append(secondCol);
        sb.append("</td></tr>\n");
    }
}
