package ch.defiant.purplesky.fragments;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.adapters.ErrorAdapter;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.beans.util.MessageHistoryDisplaySorter;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.constants.NotificationConstants;
import ch.defiant.purplesky.constants.UIConstants;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.PersistantModel;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.core.UserService;
import ch.defiant.purplesky.core.UserService.UserPreviewPictureSize;
import ch.defiant.purplesky.enums.NavigationDrawerEventType;
import ch.defiant.purplesky.exceptions.WrongCredentialsException;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.loaders.conversations.AbstractConversationLoader;
import ch.defiant.purplesky.loaders.conversations.OfflineConversationLoader;
import ch.defiant.purplesky.loaders.conversations.OnlineConversationLoader;
import ch.defiant.purplesky.util.ConversationReconciler;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.NVLUtility;
import ch.defiant.purplesky.util.StringUtility;

public class ChatListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<List<UserMessageHistoryBean>>> {

    @Inject
    protected IMessageService messageService;

    private class MessageListItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (m_adapter == null) {
                return;
            }
            final UserMessageHistoryBean item = m_adapter.getItem(position);
            if (item != null) {
                openChatFragment(item.getProfileId(), item);
            }
        }
    }

    private class MessageHistoryBeanAdapter extends ArrayAdapter<UserMessageHistoryBean> {
    
        public class ViewHolder {
            TextView usernameLbl;
            TextView dateTimeLbl;
            TextView unopenedLbl;
            TextView excerpt;
    
            ImageView imageView;
        }
    
        public MessageHistoryBeanAdapter(Context context, List<UserMessageHistoryBean> list) {
            super(context,  R.layout.messagebyuserlist_item, list);
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String unknown = getContext().getResources().getString(R.string.Unknown);
    
            View v = convertView;
            ViewHolder holder = null;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.messagebyuserlist_item, null);
    
                holder = createViewHolder(v);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
    
            UserMessageHistoryBean o = m_adapter.getItem(position);
    
            if (o != null) {
                // Wipe fields
                holder.usernameLbl.setText(StringUtility.EMPTY_STRING);
                holder.dateTimeLbl.setText(StringUtility.EMPTY_STRING);
                holder.unopenedLbl.setText(StringUtility.EMPTY_STRING);
                
    
                if (o.getLastContact() != null) {
                    holder.dateTimeLbl.setText(DateUtility.getTimeOrDateString(o.getLastContact()));
                }
    
                if (o.getUnopenedMessageCount() > 0) {
                    holder.unopenedLbl.setText(String.valueOf(o.getUnopenedMessageCount()));
                    holder.unopenedLbl.setVisibility(View.VISIBLE);
                } else {
                    holder.unopenedLbl.setVisibility(View.INVISIBLE);
                }
    
                // Get cached name if present, otherwise bean
                String username = o.getCachedUsername();
                if (username == null){
                    if(o.getUserBean() != null){
                        username = o.getUserBean().getUsername();
                    }
                }
                // Last resort - unknown
                username = NVLUtility.nvl(username, unknown);
                holder.usernameLbl.setText(username);
    
                URL url = null;
                int imgPixelSize = LayoutUtility.dpToPx(getResources(), 50);
                UserPreviewPictureSize picturesize = UserService.UserPreviewPictureSize.getPictureForPx(imgPixelSize);
                if(o.getUserBean() != null){
                    url = UserService.getUserPreviewPictureUrl(o.getUserBean(), picturesize);
                } else if (o.getCachedProfilePictureUrl() != null){
                    url = UserService.getUserPreviewPictureUrl(o.getCachedProfilePictureUrl(), picturesize);
                } 
                
                if (url != null) {
                    Picasso.with(getActivity()).load(url.toString()).
                    error(R.drawable.no_image).placeholder(R.drawable.social_person).
                    resize(imgPixelSize, imgPixelSize).centerCrop().into(holder.imageView);
                }  else {
                    setImagePlaceHolder(holder);
                }
                
                String excerpt = NVLUtility.nvl(o.getLastMessageExcerpt(), StringUtility.EMPTY_STRING);
                excerpt = excerpt.replace('\n', ' ');
                holder.excerpt.setText(excerpt);
            }
            return v;
        }
    
        private ViewHolder createViewHolder(View v) {
            ViewHolder holder;
            holder = new ViewHolder();
            holder.usernameLbl = (TextView) v.findViewById(R.id.messagebyuserlist_item_usernameLbl);
            holder.dateTimeLbl = (TextView) v.findViewById(R.id.messagebyuserlist_item_lastContectDateLbl);
            holder.unopenedLbl = (TextView) v.findViewById(R.id.messagebyuserlist_item_unopenedMessageCountLbl);
            holder.imageView = (ImageView) v.findViewById(R.id.messagebyuserlist_item_userImageView);
            holder.excerpt = (TextView) v.findViewById(R.id.messagebyuserlist_item_excerpt);
            return holder;
        }
    
        private void setImagePlaceHolder(ViewHolder holder) {
            holder.imageView.setImageResource(UIConstants.PLACEHOLDER_USERIMG_RESID);
        }
    
    }

    private class EmptyAdapter extends BaseAdapter {
    
        @Override
        public int getCount() {
            return 1;
        }
    
        @Override
        public Object getItem(int position) {
            return null;
        }
    
        @Override
        public long getItemId(int position) {
            return 0;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.emptyadapter_element, null);
            ((TextView) v.findViewById(R.id.emptyadapter_element_text)).setText(R.string.NoMessagesYet);
            return v;
        }
    
    }

    /**
     * Argument to directly go to the chat.
     */
    public static final String EXTRA_STRING_GOCHAT = "open_chat";

    public static final String TAG = ChatListFragment.class.getSimpleName();

    private static final String SAVESTATE_MESSAGEARRAY = "messagearray";

    private MessageHistoryBeanAdapter m_adapter;
    private ListView m_listView;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (m_adapter != null) {
            ArrayList<UserMessageHistoryBean> l = getAdapterData();
            outState.putSerializable(SAVESTATE_MESSAGEARRAY, l);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.Messages);
        getOrUpdateData();
        cancelMessageNotifications();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(EXTRA_STRING_GOCHAT)) {
            openChatFragment(getArguments().getString(EXTRA_STRING_GOCHAT), null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_plain, container, false);
        m_listView = (ListView) v.findViewById(R.id.list);
        m_listView.setOnItemClickListener(new MessageListItemClickListener());
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVESTATE_MESSAGEARRAY)) {
            @SuppressWarnings("unchecked")
            ArrayList<UserMessageHistoryBean> l = (ArrayList<UserMessageHistoryBean>) savedInstanceState.getSerializable(SAVESTATE_MESSAGEARRAY);
            m_adapter = new MessageHistoryBeanAdapter(getActivity(), l);
            m_listView.setAdapter(m_adapter);
        }

        return v;
    }

    @Override
    public Loader<Holder<List<UserMessageHistoryBean>>> onCreateLoader(int type, Bundle arg1) {
        getActivity().setProgressBarIndeterminateVisibility(true);
        switch(type){
            case R.id.loader_chatlist_offline:
                return new OfflineConversationLoader(getActivity(), messageService);
            case R.id.loader_chatlist_online:
                return new OnlineConversationLoader(getActivity(), messageService);
                default:
                    throw new IllegalArgumentException("Unknown conversation loader "+type);
        }
    }

    @Override
    public void onLoadFinished(Loader<Holder<List<UserMessageHistoryBean>>> loader, Holder<List<UserMessageHistoryBean>> result) {
        try {
            final Exception exception = result.getException();
            if(exception != null){
                handleLoaderException(exception);
            } else if (getActivity() != null) {
                AbstractConversationLoader convLoader = (AbstractConversationLoader) loader;
                List<UserMessageHistoryBean> list = Collections.emptyList();
                switch (convLoader.getType()) {
                    case R.id.loader_chatlist_offline:
                        list = new ArrayList<UserMessageHistoryBean>(result.getContainedObject());
                        break;
                    case R.id.loader_chatlist_online:
                        ArrayList<UserMessageHistoryBean> offlineData = getAdapterData();
                        List<UserMessageHistoryBean> onlineData = result.getContainedObject();
                        List<UserMessageHistoryBean> merged = ConversationReconciler.reconcile(offlineData, onlineData);
                        list = merged;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown conversation loader " + convLoader.getType());
                }
                Collections.sort(list, new MessageHistoryDisplaySorter());
                publishMessageCount(list);
                m_adapter = new MessageHistoryBeanAdapter(getActivity(), list);

                if (m_listView != null) {
                    if (list.isEmpty()) {
                        m_adapter = null;
                        m_listView.setAdapter(new EmptyAdapter());
                    } else {
                        m_listView.setAdapter(m_adapter);
                    }
                }

                if (convLoader.getType() == R.id.loader_chatlist_offline) {
                    // Start online
                    getLoaderManager().restartLoader(R.id.loader_chatlist_online, null, this);
                }
            }
        } finally {
            loaderFinished();
        }
    }

    @Override
    public void onLoaderReset(Loader<Holder<List<UserMessageHistoryBean>>> arg0) {
    }

    private void handleLoaderException(Exception exception) {
        if(exception instanceof WrongCredentialsException){
            PersistantModel.getInstance().handleWrongCredentials(getActivity());
        } else if (exception instanceof IOException){
            // TODO Handle better?
            // Ignore
        } else {
            Log.e(TAG, "Unknown error when loading messages online", exception);
            m_adapter = null;
            if (m_listView != null) {
                m_listView.setAdapter(new ErrorAdapter(R.string.UnknownErrorOccured, getActivity()));
            }
        }
    }

    private ArrayList<UserMessageHistoryBean> getAdapterData() {
        if(m_adapter == null){
            return new ArrayList<UserMessageHistoryBean>();  
        }
        ArrayList<UserMessageHistoryBean> l = new ArrayList<UserMessageHistoryBean>();
        for (int i = 0, size = m_adapter.getCount(); i < size; i++) {
            l.add(m_adapter.getItem(i));
        }
        return l;
    }

    private void getOrUpdateData() {
        getActivity().getLoaderManager().restartLoader(R.id.loader_chatlist_offline, null, this);
    }

    private void cancelMessageNotifications() {
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NotificationConstants.NEWMESSAGES);
    }

    private void publishMessageCount(List<UserMessageHistoryBean> list) {
        int c = 0;
        for (UserMessageHistoryBean b : list) {
            c += b.getUnopenedMessageCount();
        }
    
        PurpleSkyApplication.get().setEventCount(NavigationDrawerEventType.MESSAGE, c);
    }

    private void loaderFinished() {
        if (getActivity() != null) {
            getActivity().setProgressBarIndeterminateVisibility(false);
        }
    }

    /**
     * Opens the conversation with the specified user
     * @param profileId Must not be null!
     * @param item User bean, if available.
     */
    private void openChatFragment(String profileId, UserMessageHistoryBean item) {
        // Start fragment
        ConversationFragment f = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString(ArgumentConstants.ARG_USERID, profileId);
        if (item != null) {
            args.putSerializable(ArgumentConstants.ARG_MESSAGEHISTORYBEAN, item);
        }
        f.setArguments(args);

        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
    }
}
