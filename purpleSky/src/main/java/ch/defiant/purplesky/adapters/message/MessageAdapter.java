package ch.defiant.purplesky.adapters.message;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PendingMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessageHead;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;

public class MessageAdapter extends BaseAdapter {

    private static class ViewHolder {
        TextView messageLbl;
        TextView dateTimeLbl;
        ImageView stateIndicator;

        LinearLayout outerLinearLayout;
        View leftSpacer;
        View rightSpacer;
    }

    private final ConversationFragment m_conversationFragment;

    private LinkedList<IPrivateMessage> m_data = new LinkedList<>();
    private boolean m_showLoadMore;

    /**
     * @param conversationFragment
     */
    public MessageAdapter(ConversationFragment conversationFragment) {
        m_conversationFragment = conversationFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (getItemViewType(position) == 0) {
            IPrivateMessage o = getData().get(position - (m_showLoadMore ? 1 : 0));
            v = createMessageView(convertView, o, parent);
        } else {
            v = createButtonView(convertView, parent);
        }
        return v;
    }

    private View createButtonView(View convertView, ViewGroup parent) {
        View buttonView;
        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(m_conversationFragment.getActivity());
            buttonView = vi.inflate(R.layout.conversation_button_loadmore, parent, false);
            Button b = (Button) buttonView.findViewById(R.id.conversation_button_loadmore_button);
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View clickedView) {
                    clickedView.setEnabled(false);
                    m_conversationFragment.startLoadMoreOldWithDB();
                }
            });
        } else {
            buttonView = convertView;
        }
        Button b = (Button) buttonView.findViewById(R.id.conversation_button_loadmore_button);
        b.setEnabled(isShowLoadMore() && getCount() > 1); // Need an id to load older stuff

        return buttonView;
    }

    private View createMessageView(View convertView, IPrivateMessage m, ViewGroup parent) {
        View v;
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(m_conversationFragment.getActivity());
            v = vi.inflate(R.layout.conversation_item, parent, false);

            holder = new ViewHolder();

            holder.messageLbl = (TextView) v.findViewById(R.id.conversation_item_messageTxtLbl);
            holder.dateTimeLbl = (TextView) v.findViewById(R.id.conversation_item_dateTimeLbl);
            holder.stateIndicator = (ImageView) v.findViewById(R.id.conversation_item_state);
            holder.leftSpacer = v.findViewById(R.id.conversation_item_leftspacer);
            holder.rightSpacer = v.findViewById(R.id.conversation_item_rightSpacer);
            holder.outerLinearLayout = (LinearLayout) v.findViewById(R.id.conversation_item_outerLinearLayout);

            v.setTag(holder);
        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }

        if (m != null) {
            // Wipe fields
            holder.messageLbl.setText("");
            holder.dateTimeLbl.setText("");

            holder.stateIndicator.setVisibility(m instanceof PendingMessage ? View.VISIBLE : View.GONE);

            holder.leftSpacer.setVisibility(View.GONE);
            holder.rightSpacer.setVisibility(View.GONE);

            if (m.getTimeSent() != null) {
                holder.dateTimeLbl.setText(DateUtils.formatDateTime(m_conversationFragment.getActivity(), m.getTimeSent().getTime(), (DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE)));
            }

            if (m.getMessageText() != null) {
                holder.messageLbl.setText(m.getMessageText());
            }

            if(m instanceof PrivateMessage){
                PrivateMessageHead head = ((PrivateMessage)m).getMessageHead();
                if (head.getMessageType() != null) {
                    switch (head.getMessageType()) {
                        case RECEIVED: {
                            // Put on
                            holder.outerLinearLayout.setBackgroundResource(R.drawable.messagerectangle_left);
                            holder.rightSpacer.setVisibility(View.VISIBLE);
                            break;
                        }
                        case SENT: {
                            holder.leftSpacer.setVisibility(View.VISIBLE);
                            holder.outerLinearLayout.setBackgroundResource(R.drawable.messagerectangle_right);
                            break;
                        }
                    }
                }
            } else if (m instanceof PendingMessage){
                PendingMessage message = (PendingMessage) m;

                holder.leftSpacer.setVisibility(View.VISIBLE);
                holder.outerLinearLayout.setBackgroundResource(R.drawable.messagerectangle_right);
            }
        }
        return v;
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

    @Override
    public int getCount() {
        return getData().size() + (isShowLoadMore() ? 1 : 0);
    }

    @Override
    public Object getItem(int position) {
        if (position == 0 && isShowLoadMore()) {
            return null;
        } else if (isShowLoadMore()) {
            return getData().get(position - 1);
        } else {
            return getData().get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isShowLoadMore()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public boolean isShowLoadMore() {
        return m_showLoadMore;
    }

    public void setShowLoadMore(boolean showLoadMore) {
        m_showLoadMore = showLoadMore;
    }

    public LinkedList<IPrivateMessage> getData() {
        return m_data;
    }

    public void setData(LinkedList<IPrivateMessage> data) {
        m_data = data;
        notifyDataSetChanged();
    }

}