package ch.defiant.purplesky.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.core.INullObject;

/**
 * Adapter which contains an {@link INullObject} and displays a single view which is not enabled.
 * @author Patrick Bänziger
 *
 * @param <T> Null object
 */
public class NullAdapter<T extends INullObject> extends BaseAdapter {
    
    private final T nullObject;
    private final Context context;
    private final int overrideTextResource;
    
    public NullAdapter(Context c, T nullObject){
        this(c, nullObject, 0);
    }

    public NullAdapter(Context c, T nullObject, int textRes){
        this.nullObject = nullObject;
        this.context = c;
        this.overrideTextResource = textRes;
    }
    
    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int arg0) {
        return nullObject;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        View v = convertView;
        if(convertView == null) {
            v = LayoutInflater.from(context).inflate(R.layout.emptyadapter_element, null);
        }
        if(overrideTextResource != 0){
            TextView text = (TextView) v.findViewById(R.id.emptyadapter_element_text);
            text.setText(overrideTextResource);
        }
        return v;
    }
    
    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
