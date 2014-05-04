package ch.defiant.purplesky.dialogs;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.enums.SearchCriteria;

public class MinMaxDialogFragment extends ResultDialogFragment<Pair<Integer, Integer>> {
    
    private static final String ARG_MIN = "lowerbound";
    private static final String ARG_MAX= "upperbound";
    private static final String ARG_INITIALMIN = "initialMin";
    private static final String ARG_INITIALMAX = "initialMax";
    private static final String ARG_GRANULARITY = "granularity";

    private static final String TAG = MinMaxDialogFragment.class.getSimpleName();


    private Spinner m_minSpinner;
    private Spinner m_maxSpinner;

    private ArrayAdapter<Integer> m_spinnerAdapter;
    
    public static class Builder{
        
        private int m_min;
        private int m_max;
        private int m_granularity = 1;
        private int m_titleResource;
        private Integer m_lowerInitialValue;
        private Integer m_upperInitialValue;
        private int m_criteriaOrdinal;
        
        public int getMin() {
            return m_min;
        }
        public Builder setMin(int min) {
            m_min = min;
            return this;
        }
        public int getMax() {
            return m_max;
        }
        public Builder setMax(int max) {
            m_max = max;
            return this;
        }
        public int getGranularity() {
            return m_granularity;
        }
        public Builder setGranularity(int granularity) {
            m_granularity = granularity;
            return this;
        }
        public int getTitleResource() {
            return m_titleResource;
        }
        public Builder setTitleResource(int titleResource) {
            m_titleResource = titleResource;
            return this;
        }
        public Integer getLowerInitialValue() {
            return m_lowerInitialValue;
        }
        public Builder setLowerInitialValue(Integer lowerInitialValue) {
            m_lowerInitialValue = lowerInitialValue;
            return this;
        }
        public Integer getUpperInitialValue() {
            return m_upperInitialValue;
        }
        public Builder setUpperInitialValue(Integer upperInitialValue) {
            m_upperInitialValue = upperInitialValue;
            return this;
        }
        
        public Builder setSearchCriteria(SearchCriteria sc){
            m_criteriaOrdinal = sc.ordinal();
            return this;
        }
        
        public int get_criteriaOrdinal() {
            return m_criteriaOrdinal;
        }
        public MinMaxDialogFragment build(){
            Bundle b = new Bundle();
            
            b.putInt(ARG_GRANULARITY, m_granularity);
            b.putInt(ARG_MIN, m_min);
            b.putInt(ARG_MAX, m_max);

            if(m_lowerInitialValue != null){
                b.putInt(ARG_INITIALMIN, m_lowerInitialValue);
            }
            if(m_upperInitialValue != null){
                b.putInt(ARG_INITIALMAX, m_upperInitialValue);
            }
            if(m_titleResource != 0){
                b.putInt(EXTRA_TITLE_RES, m_titleResource);
            }
            
            b.putInt(ArgumentConstants.ARG_ENUMORDINAL, m_criteriaOrdinal);

            MinMaxDialogFragment f = new MinMaxDialogFragment();
            f.setArguments(b);
            return f;
         }
        
    }
    
    
    @Override
    protected boolean createContent(AlertDialog.Builder builder) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View minmax = inflater.inflate(R.layout.minmax_dialogfragment, null);
        setupSpinners(minmax);
        builder.setView(minmax);

        return true;
    }

    @Override
    protected void createTitle(AlertDialog.Builder builder) {
        super.createTitle(builder);
    }

    @Override
    protected void createButtons(AlertDialog.Builder builder) {
        builder.setPositiveButton(android.R.string.ok, new OnConfirmListener());
        builder.setNegativeButton(android.R.string.cancel, new AlertDialogFragment.NegativeClickListener());
    }

    private void setupSpinners(View container) {
        m_minSpinner = (Spinner) container.findViewById(R.id.minmax_dialogfragment_spinnerMin);
        m_maxSpinner = (Spinner) container.findViewById(R.id.minmax_dialogfragment_spinnerMax);
        
        Bundle args = getArguments();
        final int granularity = args.getInt(ARG_GRANULARITY,1);
        int min = args.getInt(ARG_MIN,1);
        int max = args.getInt(ARG_MAX,1);

        if (max < min) {
            Log.w(TAG, "Min value greater than max value for selection dialog: " + min + " - " + max);

            if (BuildConfig.DEBUG && max < min) {
                assert false : "Min value greater than max value for selection dialog: " + min + " - " + max;
            } else {
                // Graceful
                int tmp = min; 
                min = max;
                max = tmp;
            }
        }

        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = min; i != max; i += granularity) {
            list.add(i);
        }

        m_spinnerAdapter = new ArrayAdapter<Integer>(getActivity(), R.layout.textview_centered, R.id.textView1, list);
        m_minSpinner.setAdapter(m_spinnerAdapter);
        m_maxSpinner.setAdapter(m_spinnerAdapter);

        if(args.containsKey(ARG_INITIALMIN)){
            Integer iniMin = args.getInt(ARG_INITIALMIN);
            for(int i=0; i<m_minSpinner.getCount(); i++){
                if(iniMin.equals(m_spinnerAdapter.getItem(i))){
                    m_minSpinner.setSelection(i);
                    break;
                }
            }
        }

        if(args.containsKey(ARG_INITIALMAX)){
            Integer iniMax = args.getInt(ARG_INITIALMAX);
            if (iniMax != null) {
                for(int i=0; i<m_maxSpinner.getCount(); i++){
                    if(iniMax.equals(m_spinnerAdapter.getItem(i))){
                        m_maxSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }
        
    }
    private Pair<Integer, Integer> result;

    private class OnConfirmListener implements DialogInterface.OnClickListener {


        @Override
        public void onClick(DialogInterface dialog, int which) {

            int minpos = m_minSpinner.getSelectedItemPosition();
            int maxpos = m_maxSpinner.getSelectedItemPosition();

            int first = 0;
            int second = 0;

            if (minpos != Spinner.INVALID_POSITION) {
                first = m_spinnerAdapter.getItem(minpos);
            }
            if (maxpos != Spinner.INVALID_POSITION) {
                second = m_spinnerAdapter.getItem(maxpos);
            }

            if (first > second) {
                // Well, lets swap that
                int tmp = first;
                first = second;
                second = tmp;
            }

            result = new Pair<Integer, Integer>(first, second);
            deliverResult(result);
        }

    }
    
    @Override
    protected void addResultElements(Intent i) {
        super.addResultElements(i);
        int ordinal = getArguments().getInt(ArgumentConstants.ARG_ENUMORDINAL, -1);
        i.putExtra(ArgumentConstants.ARG_ENUMORDINAL, ordinal);
    }

}
