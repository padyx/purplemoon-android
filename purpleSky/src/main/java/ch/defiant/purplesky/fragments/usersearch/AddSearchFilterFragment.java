package ch.defiant.purplesky.fragments.usersearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.enums.SearchCriteria;

import com.actionbarsherlock.app.SherlockFragment;

public class AddSearchFilterFragment extends SherlockFragment {

    public static final String EXTRA_AVAILABLEFILTERS = "availablefilters";

    private ArrayAdapter<SearchCriteria> m_adapter;
    private ListView m_listView;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<SearchCriteria> filters = new ArrayList<SearchCriteria>();
        if (getArguments() != null && getArguments().getSerializable(EXTRA_AVAILABLEFILTERS) != null) {
            filters.addAll((Collection<? extends SearchCriteria>) getArguments().getSerializable(EXTRA_AVAILABLEFILTERS));
        }

        m_adapter = new FilterAdapter(getSherlockActivity(), 0, filters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        m_listView = (ListView) inflater.inflate(R.layout.list_plain, null);
        m_listView.setAdapter(m_adapter);

        m_listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchCriteria crit = (SearchCriteria) parent.getItemAtPosition(position);

                handleSelect(crit);
            }

        });

        return m_listView;
    }

    private void handleSelect(SearchCriteria crit) {
        // Pass selection along
        PurpleSkyApplication.get().getFragmentTransferInstance().m_chosenCriterium = crit;

        // Go back, let the previous fragment handle the dialog
        getFragmentManager().popBackStack();
    }
    
    private class FilterAdapter extends ArrayAdapter<SearchCriteria>{

        public FilterAdapter(Context context, int resource, List<SearchCriteria> values) {
            super(context, resource, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v == null)
            {
                v = LayoutInflater.from(getContext()).inflate(R.layout.two_line_with_icon, null);
            }
            v.findViewById(R.id.two_line_item_with_icon_icon).setVisibility(View.GONE);
            v.findViewById(R.id.two_line_item_with_icon_text2).setVisibility(View.GONE);
            
            SearchCriteria crit = getItem(position);
            
            ((TextView) v.findViewById(R.id.two_line_item_with_icon_text1)).setText(crit.getStringResource());
            return v;
        }
        
    }

}
