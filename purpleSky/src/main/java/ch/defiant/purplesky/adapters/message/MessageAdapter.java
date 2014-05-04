package ch.defiant.purplesky.adapters.message;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessageHead;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;

public class MessageAdapter extends BaseAdapter {

        private static class ViewHolder {
        TextView messageLbl;
        TextView dateTimeLbl;
        ImageView newIndicator;
    
        LinearLayout outerLinearLayout;
        View leftSpacer;
        View rightSpacer;
    }

        private final ConversationFragment m_conversationFragment;

        private LinkedList<PrivateMessage> m_data = new LinkedList<PrivateMessage>();
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
                PrivateMessage o = getData().get(position - (m_showLoadMore ? 1 : 0));
                v = createMessageView(convertView, o);
            } else {
                v = createButtonView(convertView);
            }
            return v;
        }

        private View createButtonView(View convertView) {
            View buttonView;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(m_conversationFragment.getSherlockActivity());
                buttonView = vi.inflate(R.layout.conversation_button_loadmore, null);
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

        private View createMessageView(View convertView, PrivateMessage m) {
            View v;
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(m_conversationFragment.getSherlockActivity());
                v = vi.inflate(R.layout.conversation_item, null);

                holder = new ViewHolder();

                holder.messageLbl = (TextView) v.findViewById(R.id.conversation_item_messageTxtLbl);
                holder.dateTimeLbl = (TextView) v.findViewById(R.id.conversation_item_dateTimeLbl);
                holder.newIndicator = (ImageView) v.findViewById(R.id.conversation_item_stateNew);
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
                holder.newIndicator.setVisibility(View.GONE);
                holder.leftSpacer.setVisibility(View.GONE);
                holder.rightSpacer.setVisibility(View.GONE);

                if (m.getTimeSent() != null) {
                    holder.dateTimeLbl.setText(DateUtils.formatDateTime(m_conversationFragment.getSherlockActivity(), m.getTimeSent().getTime(), (DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE)));
                }

                if (m.getMessageText() != null) {
                    holder.messageLbl.setText(m.getMessageText());
                }

                PrivateMessageHead head = m.getMessageHead();
                holder.newIndicator.setVisibility(head.isUnopened() ? View.VISIBLE : View.GONE);
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
                        default: {
                            if (BuildConfig.DEBUG) {
                                assert (false);
                            }
                            break;
                        }
                    }
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
        public synchronized void add(int index, PrivateMessage m) {
            m_data.add(index, m);
        }

        public synchronized void add(PrivateMessage m) {
            m_data.add(m);
        }
        
        public synchronized void addAll(Collection<PrivateMessage> m){
            m_data.addAll(m);
        }
        public synchronized void prepend(List<PrivateMessage> c){
            for(int i=c.size(); i >= 0; i--){
                m_data.add(0, c.get(i));
            }
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

        public LinkedList<PrivateMessage> getData() {
            return m_data;
        }

        public void setData(LinkedList<PrivateMessage> data) {
            m_data = data;
            notifyDataSetChanged();
        }

    }