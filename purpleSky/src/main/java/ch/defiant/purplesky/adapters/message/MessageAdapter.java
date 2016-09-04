package ch.defiant.purplesky.adapters.message;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
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

import com.google.common.eventbus.EventBus;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessageHead;
import ch.defiant.purplesky.enums.MessageStatus;
import ch.defiant.purplesky.enums.MessageType;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.fragments.conversation.ConversationModelFragment;

public class MessageAdapter extends BaseAdapter {

    @NonNull
    private final ConversationModelFragment m_model;
    @NonNull
    private final Context m_context;

    private ConversationFragment m_conversationFragment;

    private static class ViewHolder {
        TextView messageLbl;
        TextView dateTimeLbl;
        ImageView stateIndicator;

        LinearLayout outerLinearLayout;
        View leftSpacer;
        View rightSpacer;
    }

    private boolean m_showLoadMore;

    public MessageAdapter(@NonNull Context context,  @NonNull ConversationModelFragment modelFragment) {
        m_context = context;
        m_model = modelFragment;
        org.greenrobot.eventbus.EventBus.getDefault().register(this);
    }

    public void setConversationFragment(ConversationFragment fragment){
        m_conversationFragment = fragment;
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
            LayoutInflater vi = LayoutInflater.from(m_context);
            buttonView = vi.inflate(R.layout.conversation_button_loadmore, parent, false);
            Button b = (Button) buttonView.findViewById(R.id.conversation_button_loadmore_button);
            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View clickedView) {
                    clickedView.setEnabled(false);
                    // FIXME pbn Use event instead?
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
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(m_context);
            v = vi.inflate(R.layout.conversation_item, parent, false);

            holder = createMessageViewHolder(v);

            v.setTag(holder);
        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }

        if (m != null) {
            // Wipe fields
            holder.messageLbl.setText("");
            holder.dateTimeLbl.setText("");

            holder.leftSpacer.setVisibility(View.GONE);
            holder.rightSpacer.setVisibility(View.GONE);

            if (m.getTimeSent() != null) {
                holder.dateTimeLbl.setText(DateUtils.formatDateTime(m_conversationFragment.getActivity(), m.getTimeSent().getTime(), (DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE)));
            }

            if (m.getMessageText() != null) {
                holder.messageLbl.setText(m.getMessageText());
            }

            if (m.getMessageType() != null) {
                switch (m.getMessageType()) {
                    case RECEIVED: {
                        holder.stateIndicator.setVisibility(View.GONE);
                        holder.outerLinearLayout.setBackgroundResource(R.drawable.messagerectangle_left);
                        holder.rightSpacer.setVisibility(View.VISIBLE);
                        break;
                    }
                    case SENT: {
                        MessageStatus messageStatus = m.getStatus();
                        @DrawableRes
                        int image = R.drawable.ic_file_upload_black_24dp;
                        if (MessageStatus.NEW == messageStatus){
                            image = R.drawable.ic_file_upload_black_24dp;
                        } else if (MessageStatus.SENT == messageStatus){
                            image = R.drawable.ic_check_black_24dp;
                        } else if (MessageStatus.RETRY_NEEDED == messageStatus){
                            image = R.drawable.ic_clock_black_24dp;
                        } else if(MessageStatus.FAILED == messageStatus){
                            image = R.drawable.ic_error_outline_black_24px;
                        }

                        holder.stateIndicator.setImageResource(image);
                        holder.stateIndicator.setVisibility(View.VISIBLE);
                        holder.leftSpacer.setVisibility(View.VISIBLE);
                        holder.outerLinearLayout.setBackgroundResource(R.drawable.messagerectangle_right);
                        break;
                    }
                }
            }

        }
        return v;
    }

    @NonNull
    private ViewHolder createMessageViewHolder(View v) {
        ViewHolder holder;
        holder = new ViewHolder();

        holder.messageLbl = (TextView) v.findViewById(R.id.conversation_item_messageTxtLbl);
        holder.dateTimeLbl = (TextView) v.findViewById(R.id.conversation_item_dateTimeLbl);
        holder.stateIndicator = (ImageView) v.findViewById(R.id.conversation_item_state);
        holder.leftSpacer = v.findViewById(R.id.conversation_item_leftspacer);
        holder.rightSpacer = v.findViewById(R.id.conversation_item_rightSpacer);
        holder.outerLinearLayout = (LinearLayout) v.findViewById(R.id.conversation_item_outerLinearLayout);
        return holder;
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

    public List<IPrivateMessage> getData() {
        return m_model.getData();
    }

    public void setData(List<IPrivateMessage> data) {
        m_model.setData(data);
    }

    @Subscribe
    public void onEvent(Object arg){

    }
}