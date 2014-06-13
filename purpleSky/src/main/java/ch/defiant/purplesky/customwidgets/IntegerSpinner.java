package ch.defiant.purplesky.customwidgets;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.util.LayoutUtility;

/**
 * A spinner for integer providing bounds and having a 'Don't care' at the top of the list.
 * 
 * @author padyx
 * 
 */
public class IntegerSpinner extends Spinner {

    private AtomicInteger m_lowerBound = new AtomicInteger(0);
    private AtomicInteger m_upperBound = new AtomicInteger(100);
    private static final int POSITION_DONTCARE = 0;

    private IntegerSpinnerAdapter m_adapter;

    public IntegerSpinner(Context context) {
        super(context);
        custominitialize(context, null);
    }

    public IntegerSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        custominitialize(context, attrs);
    }

    public IntegerSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        custominitialize(context, attrs);
    }

    private void custominitialize(Context context, AttributeSet attrs) {
        m_adapter = new IntegerSpinnerAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item);
        setAdapter(m_adapter);
        if (attrs != null) {
            TypedArray theAttrs = context.obtainStyledAttributes(attrs, R.styleable.IntegerSpinner);

            // Gets the value "as is", with no further conversions
            Integer lowerBound = theAttrs.getInteger(R.styleable.IntegerSpinner_lowerBound, -1);
            Integer upperBound = theAttrs.getInteger(R.styleable.IntegerSpinner_upperBound, -1);

            // Release attributes
            theAttrs.recycle();
            theAttrs = null; // If we try to reuse, will cause NPE

            if (lowerBound != -1) {
                m_lowerBound.set(lowerBound);
            }
            if (upperBound != 1) {
                m_upperBound.set(upperBound);
            }
            if (lowerBound != -1 || upperBound != -1) {
                m_adapter.boundsChanged();
            }
        }
    }

    public int getUpperBound() {
        return m_upperBound.get();
    }

    public void setUpperBound(int upperBound) {
        if (upperBound < getLowerBound()) {
            throw new IllegalArgumentException("Tried to set new upper bound (" + upperBound + ") lower than current lower bound (" + getLowerBound()
                    + ")");
        }
        m_upperBound.set(upperBound);
        m_adapter.boundsChanged();
    }

    public int getLowerBound() {
        return m_lowerBound.get();
    }

    public void setLowerBound(int lowerBound) {
        if (lowerBound > getUpperBound()) {
            throw new IllegalArgumentException("Tried to set new lower bound (" + lowerBound + ") higher than current upper bound ("
                    + getUpperBound() + ")");
        }
        m_lowerBound.set(lowerBound);
        m_adapter.boundsChanged();
    }

    public void selectValue(Integer value){
        if(value == null) {
            setSelection(POSITION_DONTCARE);
            return;
        }
        for(int i = 0; i < m_adapter.getCount(); i++){
            if (value.equals(m_adapter.getItem(i))){
                setSelection(i);
                return;
            }
        }
    }

    private class IntegerSpinnerAdapter extends ArrayAdapter<Integer> {
        private static final int LISTITEM_MIN_HEIGHT_DP = 48;

        public IntegerSpinnerAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            boundsChanged();
        }

        public void boundsChanged() {
            int lowerBound = getLowerBound();
            int upperBound = getUpperBound();

            // Try to keep selection
            Integer selection = (Integer) getSelectedItem();

            clear();
            add(null); // Don't care value
            for (int i = lowerBound; i <= upperBound; i++) { // Both bounds inclusive!
                add(i);
            }

            // Restore selection if possible
            if (selection == null) {
                setSelection(POSITION_DONTCARE);
            } else if (selection < lowerBound) {
                setSelection(1); // First item after don't care
            } else if (selection > upperBound) {
                setSelection(getCount() - 1); // Last one = Max
            } else {
                setSelection(getPosition(selection));
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(getContext());
                v = vi.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                holder = createViewHolder(v);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            v.setMinimumHeight(LayoutUtility.dpToPx(getResources(), LISTITEM_MIN_HEIGHT_DP));
            if (position == POSITION_DONTCARE) {
                holder.textView.setText(R.string.Select_DontCare);
            } else {
                holder.textView.setText(getItemAtPosition(position).toString());
            }

            return v;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        private ViewHolder createViewHolder(View v) {
            ViewHolder holder = new ViewHolder();
            holder.textView = (TextView) v.findViewById(android.R.id.text1);
            return holder;
        }

        private class ViewHolder {
            TextView textView;
        }
    }

}
