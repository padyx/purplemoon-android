package ch.defiant.purplesky.fragments.usersearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.FragmentTransfer;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.SearchCriteriaOptions;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.dialogs.CheckboxDialogFragment;
import ch.defiant.purplesky.dialogs.ListDialogFragment;
import ch.defiant.purplesky.dialogs.MinMaxDialogFragment;
import ch.defiant.purplesky.dialogs.ResultDialogFragment;
import ch.defiant.purplesky.enums.SearchCriteria;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class UserSearchFilterFragment extends SherlockFragment {

    /**
     * Argument: Use for Map of Filter values
     */
    public static final String EXTRA_FILTEROPTS = "filter";

    /**
     * If set, will restrict the available filters to these. Should be a collection of {@link SearchCriteria}
     */
    public static final String EXTRA_AVAILABLEFILTERS = "availablefilters";

    /**
     * If set, these filters will be displayed and allowed to be modified. Argument must be an instance of Map (SearchCriteria, Object)
     */
    public static final String EXTRA_FILTERVALUES = "arg_filtervalues";

    private static final String SAVEDINSTANCE_FILTERVALUES = "filterValues";
    private static final String SAVEDINSTANCE_CONFIGUREDFILTERS = "configuredfilters";

    private UserSearchOptions m_filteroptions;
    private ListView m_list;

    private List<SearchCriteria> m_availableFilters;
    private List<SearchCriteria> m_configuredFilters;
    private Map<SearchCriteria, Object> m_configuredFilterValues;

    private FilterAdapter m_filterAdapter;

    private MultiDeleteModeCallback m_actionModeCallback;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        setAvailableFilters(new ArrayList<SearchCriteria>(Arrays.asList(SearchCriteria.values())));
        setConfiguredFilters(new ArrayList<SearchCriteria>());
        setFilterValues(new HashMap<SearchCriteria, Object>());

        if (getArguments() != null) {
            if (getArguments().containsKey(EXTRA_AVAILABLEFILTERS)) {
                Collection<SearchCriteria> available = (Collection<SearchCriteria>) getArguments().getSerializable(EXTRA_AVAILABLEFILTERS);
                setAvailableFilters(new ArrayList<SearchCriteria>(available));
            }
            if (getArguments().containsKey(EXTRA_FILTERVALUES)) {
                Map<SearchCriteria, Object> filterValues = (Map<SearchCriteria, Object>) getArguments().getSerializable(EXTRA_FILTERVALUES);
                setFilterValues(filterValues);
                setConfiguredFilters(new ArrayList<SearchCriteria>(filterValues.keySet()));
            }
        }

        if (savedInstanceState != null) {
            // Restore. Check that all are here
            if (savedInstanceState.containsKey(SAVEDINSTANCE_FILTERVALUES)) {
                setFilterValues((Map<SearchCriteria, Object>) savedInstanceState.getSerializable(SAVEDINSTANCE_FILTERVALUES));
            }
            if (savedInstanceState.containsKey(SAVEDINSTANCE_CONFIGUREDFILTERS)) {
                setConfiguredFilters((List<SearchCriteria>) savedInstanceState.getSerializable(SAVEDINSTANCE_CONFIGUREDFILTERS));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final FragmentTransfer transfInst = PurpleSkyApplication.getContext().getFragmentTransferInstance();

        if (transfInst.m_chosenCriterium != null) {
            // This criterium was chosen in "Add filter".
            // Open dialog to choose its value
            handleCriterium(transfInst.m_chosenCriterium, null);
            // Must reset it
            transfInst.m_chosenCriterium = null;
        }

        // Reset it
        transfInst.m_searchFilterValues = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save all our filters in the transfer fragment, just in case
        // If this fragment goes away, it is going to need it!
        PurpleSkyApplication.getContext().getFragmentTransferInstance().m_searchFilterValues = m_configuredFilterValues;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_list = (ListView) inflater.inflate(R.layout.list_plain, null);
        m_filterAdapter = new FilterAdapter();
        m_list.setAdapter(m_filterAdapter);
        m_list.setOnItemClickListener(new FilterClickListener());
        m_list.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int arg2, long arg3) {
                if(m_actionModeCallback == null){
                    m_list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                    m_actionModeCallback = new MultiDeleteModeCallback();
                    getSherlockActivity().startActionMode(m_actionModeCallback);
                    m_list.setItemChecked(arg2, true);
                }
                return true;
            }
        });
        return m_list;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(EXTRA_FILTEROPTS, m_filteroptions);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.usersearchfilter_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.usersearchfilter_fragment_menu_ok:
                getFragmentManager().popBackStack();
                break;
            default:
                throw new IllegalArgumentException("Unknown menu entry");
        }
        return false;
    }

    /**
     * Handles select actions of a criterium.
     * 
     * @param crit
     *            Which criterium to choose the values for.
     * @param initialValue
     *            Initial / previous values for this criterium.
     */
    private void handleCriterium(final SearchCriteria crit, Object initialValue) {
        switch (crit.getType()) {
            case BOOLEAN:
                // TODO Test this
                CheckboxDialogFragment cdf = new CheckboxDialogFragment();
                cdf.setCheckboxLblRes(crit.getStringResource());

                cdf.setTargetFragment(this, 0);
                cdf.show(getFragmentManager(), "Bool");
                break;
            case INT:
                // TODO Implement when needed
                // break;
            case LIST:
                // First element - label, second - value
                android.util.Pair<String[], String[]> opts = SearchCriteriaOptions.createListOptions(crit);

                // TODO Implement Singleselect when needed

                ListDialogFragment<String> ldf = new ListDialogFragment<String>();
                Bundle args = new Bundle();
                args.putBoolean(ListDialogFragment.ARG_MULTISELECT, true);
                args.putStringArray(ListDialogFragment.ARG_STRINGS, opts.first);
                args.putSerializable(ListDialogFragment.ARG_KEYS, opts.second);
                args.putInt(ArgumentConstants.ARG_ENUMORDINAL, crit.ordinal());

                // Our initialvalue is a list of objects. Must convert that to a boolean array
                if (initialValue != null) {
                    @SuppressWarnings("unchecked")
                    List<String> init = (List<String>) initialValue;
                    boolean[] checked = new boolean[opts.second.length];
                    int i = 0;
                    for (String s : opts.second) {
                        if (init.contains(s)) {
                            checked[i] = true;
                        }
                        i++;
                    }
                    args.putBooleanArray(ListDialogFragment.ARG_INITIALCHECKED, checked);
                }

                ldf.setArguments(args);
                ldf.setTargetFragment(this, 0);
                ldf.show(getFragmentManager(), "LIST");
                break;
            case MIN_MAX_INT:
                @SuppressWarnings("unchecked")
                Pair<Integer, Integer> ini = (Pair<Integer, Integer>) initialValue;

                MinMaxDialogFragment.Builder builder = new MinMaxDialogFragment.Builder();
                if (ini != null) {
                    builder.setLowerInitialValue(ini.getFirst());
                    builder.setUpperInitialValue(ini.getSecond());
                }
                builder.setTitleResource(crit.getStringResource());
                // TODO Get these values from somewhere...
                builder.setMin(12); // TODO Extract constants
                builder.setMax(80); // TODO Extract constants
                MinMaxDialogFragment mmdf = builder.build();
                mmdf.setTargetFragment(this, 0);
                mmdf.show(getFragmentManager(), "MINMAX_DIALOG");
                break;
            default:
                throw new IllegalArgumentException("Unknown type" + crit.getType());
        }

    }

    /**
     * Sets the value for the criteria in the internal data structures. Notifies the adapter to refresh
     * 
     * @param crit
     * @param value
     */
    private void setFilterValue(SearchCriteria crit, Object value) {
        if (value == null) {
            m_configuredFilterValues.remove(crit);
            m_configuredFilters.remove(crit);
        } else {
            if (!m_configuredFilterValues.containsKey(crit)) {
                m_configuredFilters.add(crit);
            }
            m_configuredFilterValues.put(crit, value);
        }
        m_filterAdapter.notifyDataSetChanged();
    }

    private List<SearchCriteria> getAvailableFilters() {
        return m_availableFilters;
    }

    private void setAvailableFilters(List<SearchCriteria> availableFilters) {
        m_availableFilters = availableFilters;
    }

    private Map<SearchCriteria, Object> getFilterValues() {
        return m_configuredFilterValues;
    }

    private void setFilterValues(Map<SearchCriteria, Object> filterValues) {
        m_configuredFilterValues = filterValues;
    }

    private List<SearchCriteria> getConfiguredFilters() {
        return m_configuredFilters;
    }

    private void setConfiguredFilters(List<SearchCriteria> configuredFilters) {
        m_configuredFilters = configuredFilters;
    }

    /**
     * Deletes all selected items.
     */
    private void actionModeDelete(){
        SparseBooleanArray checkedPos = m_list.getCheckedItemPositions();

        // Delete all these from the filters.
        int i = checkedPos.size();
        while(i>0){ // Never handle zero here - thats the "add filter"!
            if(checkedPos.get(i)){
                int realpos=i-1;
                SearchCriteria crit = m_configuredFilters.get(realpos);
                m_configuredFilters.remove(realpos);
                m_configuredFilterValues.remove(crit);
            }
            i--;
        }
        m_filterAdapter.notifyDataSetChanged();
    }


    /**
     * Receives result callbacks from fragment dialogs. Cannot be avoided to handle it like this (rotation would drop any responder interface).
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ResultDialogFragment.SUCCESS) {
            int critOrdinal = data.getIntExtra(ArgumentConstants.ARG_ENUMORDINAL, -1);
            SearchCriteria crit = SearchCriteria.values()[critOrdinal];
            Serializable obj = data.getSerializableExtra(ArgumentConstants.ARG_SERIALIZABLEOBJECT);
            setFilterValue(crit, obj);
        }
    }

    private class FilterAdapter extends BaseAdapter {

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getCount() {
            // One "Add filter" entry
            return getConfiguredFilters().size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            }
            else
                return getConfiguredFilters().get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(getActivity());
                View inflated = inflater.inflate(R.layout.two_line_with_icon, null);
                v = inflated;
            }

            TextView firstline = (TextView) v.findViewById(R.id.two_line_item_with_icon_text1);
            TextView secondline = (TextView) v.findViewById(R.id.two_line_item_with_icon_text2);
            View iconView = v.findViewById(R.id.two_line_item_with_icon_icon);
            if (position == 0) {
                firstline.setText(R.string.AddFilter);
                secondline.setVisibility(View.GONE);
                iconView.setVisibility(View.VISIBLE);
            } else {
                SearchCriteria crit = getConfiguredFilters().get(position - 1);
                firstline.setText(crit.getStringResource());
                secondline.setVisibility(View.VISIBLE);
                iconView.setVisibility(View.GONE);
                String secStr = "";
                switch (crit.getType()) {
                    case BOOLEAN:
                        // TODO Implement
                        break;
                    case INT:
                        // TODO Implement
                        break;
                    case LIST:
                        @SuppressWarnings("unchecked")
                        List<Object> object = (List<Object>) m_configuredFilterValues.get(crit);
                        final int size = object.size();
                        assert (object != null);
                        secStr = getResources().getQuantityString(R.plurals.XElementsSelected, size, size);
                        break;
                    case MIN_MAX_INT:
                        @SuppressWarnings("unchecked")
                        Pair<Integer, Integer> pair = (Pair<Integer, Integer>) getFilterValues().get(crit);
                        secStr = getString(R.string.XtoY_Integer, pair.getFirst(), pair.getSecond());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown criteria type");

                }
                secondline.setText(secStr);
            }

            return v;
        }
    }

    private class FilterClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterV, View arg1, int position, long arg3) {
            if(m_actionModeCallback != null){
                return;
            }
            if (position == 0) {
                // Add filter!
                showAddFilter();
            } else {
                SearchCriteria crit = (SearchCriteria) adapterV.getItemAtPosition(position);
                handleCriterium(crit, m_configuredFilterValues.get(crit));
            }
        }

        private void showAddFilter() {
            AddSearchFilterFragment f = new AddSearchFilterFragment();

            // Unused filters
            ArrayList<SearchCriteria> filters = new ArrayList<SearchCriteria>();

            Set<SearchCriteria> keySet = getFilterValues().keySet();
            for (SearchCriteria c : getAvailableFilters()) {
                if (!keySet.contains(c)) {
                    filters.add(c);
                }
            }

            Bundle args = new Bundle();
            args.putSerializable(AddSearchFilterFragment.EXTRA_AVAILABLEFILTERS, filters);
            f.setArguments(args);

            final FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
        }
    }

    private class MultiDeleteModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.usersearchfilter_contextualmenu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()){
                case R.id.usersearchfilter_contextualmenu_delete:
                    actionModeDelete();
                    mode.finish();
                    // WORKAROUND This is due to a bug: When an item is clicked - onDestroy is not called
                    onDestroyActionMode(mode);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown menu item!");
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            m_actionModeCallback = null;
            
            // These are all responsible for clearing the selected state. Very complicated -.-
            m_list.clearChoices();

            for (int i = 0; i < m_list.getChildCount(); i++){
                m_list.setItemChecked(i, false);
            }
            m_list.post(new Runnable() {
                @Override
                public void run() {
                    m_list.setChoiceMode(ListView.CHOICE_MODE_NONE);
                }
            });
        }

    }

}
