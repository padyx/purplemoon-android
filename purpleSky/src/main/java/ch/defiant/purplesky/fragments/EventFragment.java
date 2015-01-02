package ch.defiant.purplesky.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.promotions.EventRegistrationResult;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.beans.promotion.EventLocation;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.loaders.promotions.EventLoader;
import ch.defiant.purplesky.loaders.promotions.RegisterUnregisterLoader;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.Holder;

/**
 * @author Patrick Bänziger
 */
public class EventFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<Event>> {

    private static final String LOADER_ISUNREGISTER = "isUnregister";
    private static final String LOADER_EVENTID = "m_eventId";
    private static final String LOADER_VISIBILITY_INDEX = "visibility";

    @Inject
    protected IPromotionAdapter m_promotionAdapter;
    private int m_eventId = 35;
    private WebView m_webview;
    private MenuItem m_registerMenu;
    private MenuItem m_unregisterMenu;
    private EventRegistrationListener m_registerUnregisterHandler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        m_registerUnregisterHandler = new EventRegistrationListener(m_promotionAdapter);
        m_registerUnregisterHandler.setFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        m_registerUnregisterHandler.setFragment(null);
        m_registerUnregisterHandler = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        if(!intent.hasExtra(ArgumentConstants.ARG_ID)){
            throw new IllegalArgumentException("Missing event id");
        }

        m_eventId = intent.getIntExtra(ArgumentConstants.ARG_ID, 0);
    }

    private void loadData() {
        getLoaderManager().restartLoader(R.id.loader_event, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.webview_full, container);

        m_webview = (WebView) view.findViewById(R.id.webview_full_webview);
        loadData();

        return view;
    }

    @Override
    public Loader<Holder<Event>> onCreateLoader(int id, Bundle args) {
        getActivity().setProgressBarIndeterminateVisibility(true);
                return new EventLoader(m_eventId, m_promotionAdapter, getActivity());
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
            m_registerMenu.setEnabled(false);
        } else {
            Event event = data.getContainedObject();
            m_webview.loadData(promoToHtml(getActivity(), event), "text/html; charset=utf-8", "UTF-8");

            m_registerMenu.setEnabled(canRegister(event));
            m_registerMenu.setVisible(!event.isRegistered());
            m_unregisterMenu.setVisible(event.isRegistered());
        }
    }

    private boolean canRegister(@NonNull Event event) {
        return true; // FIXME Hardcoded
        // return !event.isPreliminary() && !event.isRegistered();
    }

    @Override
    public void onLoaderReset(Loader<Holder<Event>> loader) {}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_menu, menu);
        m_registerMenu = menu.findItem(R.id.register);
        m_unregisterMenu = menu.findItem(R.id.unregister);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.register){
            ChooserDialog fragm = new ChooserDialog();
            fragm.show(getFragmentManager(), "registerDialog");
            return true;
        } else if (item.getItemId() == R.id.unregister) {
            unregister();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

        if(event.isPreliminary()) {
            sb.append("<p><em>");
            sb.append(c.getString(R.string.PreliminaryEventNoRegistration));
            sb.append("</em></p>");
        }

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
            addRow(sb, c.getString(R.string.Admission), c.getString(R.string.Limited_PrivateEvent));
        }
        if(event.getGenders() != null){
            Event.Genders genders = event.getGenders();
            if(Arrays.asList(Event.Genders.MEN_ONLY, Event.Genders.WOMEN_ONLY).contains(genders)){
                addRow(sb, c.getString(R.string.EntranceOnlyFor), c.getString(genders.resourceId));
            }
            else if (genders != Event.Genders.ALL) {
                // We don't care about all
                addRow(sb, c.getString(R.string.Audience), c.getString(genders.resourceId));
            }
        }
        if(event.getMinAge() != null) {
            addRow(sb, c.getString(R.string.MinimumAge), String.valueOf(event.getMinAge()));
        }
        if(event.getMaxAge() != null) {
            addRow(sb, c.getString(R.string.MaximumAge), String.valueOf(event.getMaxAge()));
        }
        addRow(sb, c.getString(R.string.Registrations), String.valueOf(event.getRegistrations()));
        sb.append("</table>");



        if(event.getLocation() != null) {
            EventLocation location = event.getLocation();
            sb.append("<h3>");
            sb.append(c.getString(R.string.EventLocation));
            sb.append("</h3><table style='border:0'>");

            addRow(sb, c.getString(R.string.Name), location.getLocationName());
            addRow(sb, c.getString(R.string.Address), location.getAddress());
            addRow(sb, c.getString(R.string.Country), new Locale("", location.getCountryCode().toUpperCase()).getDisplayCountry());

            if(location.getWebsite() != null) {
                addRow(sb,
                        c.getString(R.string.Information),
                        "<a href='"+location.getWebsite()+"'>"+c.getString(R.string.Website)+"</a>");
            }

            sb.append("</table>");
        }
        sb.append("</body></html>");

        return sb.toString();
    }

    private static void addRow(StringBuilder sb, String firstCol, String secondCol) {
        sb.append("<tr><td>");
        sb.append(firstCol);
        sb.append("</td><td>");
        sb.append(secondCol);
        sb.append("</td></tr>\n");
    }


    public static class ChooserDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.ShowAttendance);
            CharSequence[] values = {getString(R.string.Everyone), getString(R.string.Nobody)};
            return builder.setItems(values, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment);
                    if(fragment instanceof EventFragment){
                        ((EventFragment) fragment).register(which);
                    }
                }
            }).create();
        }

    }

    private void register(int index) {
        Event.RegistrationVisibility visibility;
        if(index == 0){
            // Everybody
            visibility = Event.RegistrationVisibility.ALL;
        } else {
            // Nobody
            visibility = Event.RegistrationVisibility.NONE;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(LOADER_EVENTID, m_eventId);
        bundle.putInt(LOADER_VISIBILITY_INDEX, visibility.ordinal());
        bundle.putBoolean(LOADER_ISUNREGISTER, false);

        getLoaderManager().restartLoader(R.id.loader_eventRegisterUnregister, bundle, m_registerUnregisterHandler);
    }

    private void unregister() {
        Bundle bundle = new Bundle();
        bundle.putInt(LOADER_EVENTID, m_eventId);
        bundle.putBoolean(LOADER_ISUNREGISTER, true);

        getLoaderManager().restartLoader(R.id.loader_eventRegisterUnregister, bundle, m_registerUnregisterHandler);
    }


    private static class EventRegistrationListener implements Serializable, LoaderManager.LoaderCallbacks<Holder<EventRegistrationResult>> {

        public EventRegistrationListener(IPromotionAdapter adapter){
            m_promotionAdapter = adapter;
        }

        private final IPromotionAdapter m_promotionAdapter;
        private EventFragment m_fragment;

        public void setFragment(EventFragment fragment) {
            m_fragment = fragment;
        }

        @Override
        public Loader<Holder<EventRegistrationResult>> onCreateLoader(int id, Bundle args) {
            m_fragment.getActivity().setProgressBarIndeterminateVisibility(true);
            return new RegisterUnregisterLoader(
                    m_fragment.getActivity(),
                    m_promotionAdapter,
                    args.getBoolean(LOADER_ISUNREGISTER),
                    args.getInt(LOADER_EVENTID),
                    Event.RegistrationVisibility.values()[args.getInt(LOADER_VISIBILITY_INDEX)]
            );
        }

        @Override
        public void onLoadFinished(Loader<Holder<EventRegistrationResult>> loader, Holder<EventRegistrationResult> data) {
            if (m_fragment == null || m_fragment.getActivity().isFinishing()){
                return;
            }
            m_fragment.getActivity().setProgressBarIndeterminateVisibility(false);
            m_fragment.getLoaderManager().destroyLoader(R.id.loader_eventRegisterUnregister);
            m_fragment.registerUnregisterFinished(data);
        }

        @Override
        public void onLoaderReset(Loader<Holder<EventRegistrationResult>> loader) {}
    }

    private void registerUnregisterFinished(Holder<EventRegistrationResult> data) {}

}
