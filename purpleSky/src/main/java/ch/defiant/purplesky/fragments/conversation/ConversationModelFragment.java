package ch.defiant.purplesky.fragments.conversation;

import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.defiant.purplesky.beans.IPrivateMessage;

/**
 *
 * @author Patrick Bänziger
 */
public class ConversationModelFragment extends Fragment {

    private static final String SAVEINSTANCE_DATA = "saveinstance_data";
    private final List<IPrivateMessage> m_data = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            restoreData(savedInstanceState);
        }
    }

    private void restoreData(Bundle savedInstanceState) {
        List<IPrivateMessage> data = (List<IPrivateMessage>) savedInstanceState.getSerializable(SAVEINSTANCE_DATA);
        if(data != null) {
            m_data.clear();
            m_data.addAll(data);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(SAVEINSTANCE_DATA, new ArrayList<>(m_data));
    }

    /**
     * Adds the item at the specified position.
     *
     * @param index
     *            At which index the item shall be inserted.
     * @param m
     *            Item to add
     */
    public synchronized void add(int index, IPrivateMessage m) {
        m_data.add(index, m);
    }

    public synchronized void add(IPrivateMessage m) {
        m_data.add(m);
    }

    public synchronized void addAll(Collection<IPrivateMessage> m){
        m_data.addAll(m);
    }
    public synchronized void prepend(List<IPrivateMessage> c){
        for(int i=c.size(); i >= 0; i--){
            m_data.add(0, c.get(i));
        }
    }

    /**
     * Removes all data from the adapter.
     */
    public synchronized void clear() {
        m_data.clear();
    }

    public List<IPrivateMessage> getData(){
        return m_data;
    }

    public void setData(List<IPrivateMessage> data) {
        m_data.clear();
        m_data.addAll(data);
    }

}
