package ch.defiant.purplesky.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.defiant.purplesky.R;

public class ErrorAdapter extends BaseAdapter {

    private static final int DEFAULT = 0;

    private int m_resource = DEFAULT;
    private Context m_context;

    public ErrorAdapter(Context c) {
        super();
        m_context = c;
    }

    public ErrorAdapter(int resource, Context c) {
        this(c);
        m_resource = resource;
    }

    @Override
    public int getCount() {
        return 1; // Always only one
    }

    @Override
    public Object getItem(int arg0) {
        return null; // No actual object behind it
    }

    @Override
    public long getItemId(int position) {
        return 0; // All the same
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        LayoutInflater vi = (LayoutInflater) LayoutInflater.from(m_context);
        View inflated = vi.inflate(R.layout.error_listitem, null);
        if (m_resource != 0) {
            ((TextView) inflated.findViewById(R.id.textView1)).setText(m_resource);
        }
        return inflated;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
