package ch.defiant.purplesky.fragments.conversation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.adapters.message.MessageAdapter;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessageHead;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.customwidgets.ProgressFragmentDialog;
import ch.defiant.purplesky.dialogs.AlertDialogFragment;
import ch.defiant.purplesky.dialogs.IAlertDialogFragmentResponder;
import ch.defiant.purplesky.enums.MessageType;
import ch.defiant.purplesky.fragments.BaseFragment;
import ch.defiant.purplesky.fragments.ReportUserFragment;
import ch.defiant.purplesky.fragments.profile.DisplayProfileFragment;
import ch.defiant.purplesky.loaders.CachedUsernameLoader;
import ch.defiant.purplesky.loaders.SimpleAsyncLoader;
import ch.defiant.purplesky.loaders.conversations.ConversationStatusLoader;
import ch.defiant.purplesky.loaders.message.AbstractMessageLoader;
import ch.defiant.purplesky.loaders.message.ActionBarImageLoader;
import ch.defiant.purplesky.loaders.message.EmptyOnlineLoader;
import ch.defiant.purplesky.loaders.message.InitialDBMessageLoader;
import ch.defiant.purplesky.loaders.message.OlderMessageDBLoader;
import ch.defiant.purplesky.loaders.message.OlderMessageOnlineLoader;
import ch.defiant.purplesky.loaders.message.RefreshMessageLoader;
import ch.defiant.purplesky.loaders.message.SendMessageLoader;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.NVLUtility;
import ch.defiant.purplesky.util.StringUtility;

public class ConversationFragment extends BaseFragment implements LoaderCallbacks<Holder<MessageResult>> {

    @Inject
    protected IMessageService messageService;

    private final class NotifyAdapter implements Runnable {
        @Override
        public void run() {
            m_adapter.notifyDataSetChanged();
        }
    }

    private final class SendListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            final EditText messageField = (EditText) getView().findViewById(R.id.conversation_fragment_messageEditText);
            if (messageField.length() == 0 || messageField.toString().trim().length() == 0) {
                Toast.makeText(getSherlockActivity(), R.string.MustEnterTextMessage, Toast.LENGTH_LONG).show();
            }

            Long lastReceivedTS = messageService.getLatestReceivedMessageTimestamp(m_profileId);
            if (lastReceivedTS == null) {
                lastReceivedTS = 0L;
            }

            PrivateMessageHead head = new PrivateMessageHead();
            head.setRecipientProfileId(m_profileId);
            head.setMessageType(MessageType.SENT);
            head.setTimeSent(new Date());
            PrivateMessage message = new PrivateMessage();
            message.setMessageHead(head);
            message.setMessageText(messageField.getText().toString());

            Bundle bundle = new Bundle();
            bundle.putString(ArgumentConstants.ARG_USERID, m_profileId);
            bundle.putSerializable(ArgumentConstants.ARG_MESSAGE, message);
            bundle.putLong(ArgumentConstants.ARG_TIMESTAMP, lastReceivedTS);

            getLoaderManager().restartLoader(R.id.loader_message_send, bundle, ConversationFragment.this);

            InputMethodManager imm = (InputMethodManager) getSherlockActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageField.getWindowToken(), 0);
        }
    }

    private class ImageLoaderCallback implements LoaderCallbacks<Drawable> {

        @Override
        public Loader<Drawable> onCreateLoader(int arg0, Bundle arg1) {
            Bundle b = new Bundle();
            b.putString(ArgumentConstants.ARG_USERID, m_profileId);
            return new ActionBarImageLoader(getSherlockActivity(), b, apiAdapter);
        }

        @Override
        public void onLoadFinished(Loader<Drawable> arg0, Drawable result) {
            if (getSherlockActivity() != null && result != null) {
                getSherlockActivity().getSupportActionBar().setIcon(result);
            }
        }

        @Override
        public void onLoaderReset(Loader<Drawable> arg0) { }

    }

    private class ProfileNameCallback implements LoaderCallbacks<String>{

        @Override
        public Loader<String> onCreateLoader(int arg0, Bundle arg1) {
            Bundle b = new Bundle();
            b.putString(ArgumentConstants.ARG_USERID, m_profileId);
            return new CachedUsernameLoader(getSherlockActivity(), b, messageService);
        }

        @Override
        public void onLoadFinished(Loader<String> arg0, String result) {
            setTitle(result);
        }

        @Override
        public void onLoaderReset(Loader<String> arg0) { }
    }

    private class ConversationStatusCallback implements LoaderCallbacks<Holder<UserMessageHistoryBean>>{
        @Override
        public Loader<Holder<UserMessageHistoryBean>> onCreateLoader(int arg0, Bundle arg1) {
            Bundle b = new Bundle();
            b.putString(ArgumentConstants.ARG_USERID, m_profileId);
            return new ConversationStatusLoader(getSherlockActivity(), b, apiAdapter);
        }

        @Override
        public void onLoadFinished(Loader<Holder<UserMessageHistoryBean>> arg0, Holder<UserMessageHistoryBean> result) {
            if(result.getException() != null || result.getContainedObject() == null){
                if (!(result.getException() instanceof IOException)) {
                    Log.i(TAG, "Could not obtain status for conversation. Exception:",result.getException());
                }
            } else if(result.getContainedObject() != null){
                m_conversationState = result.getContainedObject();
            }
            updateConversationSubtitle();
        }

        @Override
        public void onLoaderReset(Loader<Holder<UserMessageHistoryBean>> arg0) { }
    }

    /**
     * Opens a discard confirmation dialog.
     * 
     * @see AlertDialogFragment#newDiscardCancelDialog(int, int, int)
     */
    public static final int DIALOG_DISCARD_ON_EXIT = 0;

    private static final String TAG = ConversationFragment.class.getSimpleName();
    private final static String FRAGMENT_TAG = "sendDialogFragment";

    private static final String SAVEINSTANCE_DATA = "saveinstance_data";
    private static final String SAVEINSTANCE_HASNOMORECACHED = "hasNoMoreCached";
    private static final String SAVEINSTANCE_HASNOMOREONLINE = "hasNoMoreOnline";
    private static final String SAVEINSTANCE_USERNAME = "username";

    private UserMessageHistoryBean m_conversationState;

    private MessageAdapter m_adapter;
    private String m_profileId;
    private ListView m_listView;
    private String m_title;

    private boolean m_hasNoMoreCached = false;
    private boolean m_hasNoMoreOnline = false;

    private ImageLoaderCallback m_imgLoaderCallback;
    private ProfileNameCallback m_usernameCallback;
    private final ConversationStatusCallback m_conversationStatusCallback = new ConversationStatusCallback(); 

    private CharSequence m_previousActionBarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        m_profileId = args.getString(ArgumentConstants.ARG_USERID);
        m_conversationState = (UserMessageHistoryBean) args.getSerializable(ArgumentConstants.ARG_MESSAGEHISTORYBEAN);
        MinimalUser userBean = null;
        m_adapter = new MessageAdapter(this);
        // If actually filled...
        if(m_conversationState != null){
            if(m_conversationState.getLastReceived() != null || m_conversationState.getLastSent() != null){
                userBean = (MinimalUser) args.getSerializable(ArgumentConstants.ARG_USER);
            }
        }
        if(userBean != null){
            m_title = userBean.getUsername();
        }
        if (StringUtility.isNullOrEmpty(m_profileId)) {
            Log.e(TAG, "Tried to open conversation without user id");
            if (getSherlockActivity() != null) {
                getSherlockActivity().finish();
            }
        }

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        if (m_adapter.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString(ArgumentConstants.ARG_USERID, m_profileId);
            getLoaderManager().restartLoader(R.id.loader_message_initial, bundle, this);
        } else {
            startRefresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.conversation_fragment, container, false);
        m_listView = (ListView) inflated.findViewById(R.id.conversation_fragment_list);
        
        m_listView.setAdapter(m_adapter);

        inflated.findViewById(R.id.conversation_fragment_sendImgV).setOnClickListener(new SendListener());

        ImageView sendBtn = (ImageView) inflated.findViewById(R.id.conversation_fragment_sendImgV);
        EditText messageField = (EditText) inflated.findViewById(R.id.conversation_fragment_messageEditText);
        messageField.addTextChangedListener(new MessageWatcher(sendBtn));

        getSherlockActivity().getSupportActionBar().setIcon(R.drawable.social_person);
        return inflated;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.conversation_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.conversation_menu_showprofile: {
                openProfileFragment();
                return true;
            }
            case R.id.conversation_menu_refresh: {
                startRefresh();
                return true;
            }
            case R.id.conversation_menu_report : {
                openReportFragment();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (m_adapter != null) {
            outState.putSerializable(SAVEINSTANCE_DATA, m_adapter.getData());
        }
        outState.putBoolean(SAVEINSTANCE_HASNOMOREONLINE, m_hasNoMoreOnline);
        outState.putBoolean(SAVEINSTANCE_HASNOMORECACHED, m_hasNoMoreCached);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Restore (because it is not done by stack)
        ActionBar actionbar = getSherlockActivity().getSupportActionBar();
        actionbar.setIcon(R.drawable.ic_launcher);
        actionbar.setTitle(m_previousActionBarTitle);
        actionbar.setSubtitle(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        m_previousActionBarTitle = getSherlockActivity().getSupportActionBar().getTitle();
        if(m_imgLoaderCallback == null){
            m_imgLoaderCallback = new ImageLoaderCallback();
        }
        getSherlockActivity().getSupportLoaderManager().restartLoader(R.id.loader_message_profileImage, null,
                m_imgLoaderCallback);
        if(m_title == null){
            if(m_usernameCallback == null){
                m_usernameCallback = new ProfileNameCallback();
            }
            getLoaderManager().restartLoader(R.id.loader_username, null, m_usernameCallback);
        }
        else {
            setTitle(m_title);
        }
        updateConversationSubtitle();
    }
    
    /**
     * Check if the user wants to discard the entered text (if such text exists). Will open a dialog confirmation dialog
     * to prompt the user. Note that the dialog will use the callbacks of {@link IAlertDialogFragmentResponder}.
     * 
     * @return True, if the text can be discarded immediately. False otherwise (you may still receieve the callbacks
     *         later!)
     */
    // TODO pbn this is never used!!!
    public boolean checkDiscardOnExit() {
        View view = getView();
        if (view == null) {
            return true;
        }
        EditText text = (EditText) view.findViewById(R.id.conversation_fragment_messageEditText);
        if (text != null && text.getText().length() > 0) {
            AlertDialogFragment.newDiscardCancelDialog(R.string.Discard_, R.string.ConfirmDiscardMessage,
                    DIALOG_DISCARD_ON_EXIT).show(
                            getFragmentManager(), String.valueOf(DIALOG_DISCARD_ON_EXIT));
            return false;
        }
        return true;
    }

    @Override
    public AbstractMessageLoader onCreateLoader(final int type, final Bundle bundle) {
        final SherlockFragmentActivity context = getSherlockActivity();
        context.setProgressBarIndeterminateVisibility(true);
        switch (type) {
            case R.id.loader_message_empty:
                return new EmptyOnlineLoader(context, bundle, apiAdapter, messageService);
            case R.id.loader_message_initial:
                return new InitialDBMessageLoader(context, bundle, apiAdapter, messageService);
            case R.id.loader_message_moreoldDB:
                return new OlderMessageDBLoader(context, bundle, apiAdapter, messageService);
            case R.id.loader_message_moreoldOnline:
                return new OlderMessageOnlineLoader(context, bundle, apiAdapter, messageService);
            case R.id.loader_message_send:
                sendPreActions();
                return new SendMessageLoader(context, bundle, apiAdapter, messageService);
            case R.id.loader_message_refresh:
                return new RefreshMessageLoader(context, bundle, apiAdapter, messageService);
            default:
                throw new UnsupportedOperationException("Unknown loader");
        }
    }

    @Override
    public void onLoadFinished(Loader<Holder<MessageResult>> loader, Holder<MessageResult> holder) {
        if (getSherlockActivity() != null) {
            getSherlockActivity().setProgressBarIndeterminateVisibility(false);
        }

        int type = ((SimpleAsyncLoader<?>) loader).getType();
        if (holder.getException() != null) {
            handleExceptionPostLoad(loader, type, holder.getException());
            updateAdapterButtonState();
            return;
        }

        MessageResult res = holder.getContainedObject();
        if (res == null) {
            Log.w(TAG, "No message result received (null) for loader type " + type);
            showToastError();
            return;
        }

        getLoaderManager().destroyLoader(type);
        
        List<PrivateMessage> unreadMessages = res.getUnreadMessages();
        switch (type) {
            case R.id.loader_message_moreoldDB: {
                // More old from db. If result non-empty, we might have more
                if (unreadMessages == null) {
                    Log.w(TAG, "Loader MoreOld(DB): Unread messages was NULL");
                    showToastError();
                    return;
                }
                if (unreadMessages.isEmpty()) {
                    m_hasNoMoreCached = true;
                    // Launch an online loader
                    startMoreOldOnline();
                }
                for (int i = unreadMessages.size() - 1; i >= 0; i--) {
                    // Oldest first, ideally we'd prepend it.
                    // But like this, we have to insert at beginning, starting with last (newest)
                    m_adapter.add(0, unreadMessages.get(i));
                }
            }
            case R.id.loader_message_empty: { // Online load when nothing in DB
                if (unreadMessages == null) {
                    Log.w(TAG, "Loader MoreOld(DB): Unread messages was NULL");
                    showToastError();
                    return;
                }
                if (unreadMessages.isEmpty()) {
                    m_hasNoMoreCached = true;
                    m_hasNoMoreOnline = true;
                }
                for (PrivateMessage pm : unreadMessages) {
                    m_adapter.add(pm);
                }
                break;
            }
            case R.id.loader_message_initial: { // Latest from DB, when opening
                if (unreadMessages == null) {
                    Log.w(TAG, "Initial load: Unread messages was NULL");
                    showToastError();
                    return;
                }
                m_adapter.addAll(unreadMessages);
                // If we found nothing, request online
                if (unreadMessages.isEmpty()) {
                    startEmpty();
                } else {
                    // Just get new stuff
                    startRefresh();
                }
                break;
            }
            case R.id.loader_message_moreoldOnline: {
                // Can we load any others? No, if empty
                m_hasNoMoreOnline = unreadMessages.isEmpty();
                for (int i = unreadMessages.size() - 1; i >= 0; i--) {
                    m_adapter.add(0, unreadMessages.get(i)); // Add at the beginning of the list
                }
                break;
            }
            case R.id.loader_message_refresh: {
                // Drop last dummy message again, should be replaced by a real one
                if (!m_adapter.isEmpty()) {
                    int position = m_adapter.getCount() - 1;
                    PrivateMessage item = (PrivateMessage) m_adapter.getItem(position);
                    if (item != null && item.isDummy()) {
                        m_adapter.getData().remove(position);
                    }
                }
                for (PrivateMessage pm : unreadMessages) {
                    m_adapter.add(pm);
                }

                break;
            }
            case R.id.loader_message_send: {
                doSendPostActions(res);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown loader type " + type);
        }
        updateAdapterButtonState();
        getSherlockActivity().runOnUiThread(new NotifyAdapter());
    }

    @Override
    public void onLoaderReset(Loader<Holder<MessageResult>> arg0) {
    }

    /**
     * Loads more old messages Only public s.t. message apiAdapter can call this - all others shouldn't!
     */
    // NICE This is not pretty.
    public void startLoadMoreOldWithDB() {
        int count = m_adapter.getCount();
        if (count < 2) {
            Log.w(TAG, "State is inconsistent. Load more button clicked, although no message present to use the id.");
            return;
        }

        PrivateMessage item = (PrivateMessage) m_adapter.getItem(1);
        if (item == null) {
            Log.w(ConversationFragment.TAG, "Oldest message in apiAdapter was null, cannot load older messages!");
            return;
        }
        Long lastid = item.getMessageHead().getMessageId();

        Bundle b = new Bundle();
        b.putString(ArgumentConstants.ARG_USERID, m_profileId);
        b.putLong(ArgumentConstants.ARG_ID, lastid);
        // Try DB. It will check for itself
        getLoaderManager().restartLoader(R.id.loader_message_moreoldDB, b, ConversationFragment.this);
    }

    private void setTitle(String result) {
        if(getSherlockActivity() != null && result != null){
            getSherlockActivity().getSupportActionBar().setTitle(result);
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SAVEINSTANCE_DATA)) {
            @SuppressWarnings("unchecked")
            List<PrivateMessage> adapterdata = (List<PrivateMessage>) savedInstanceState
            .getSerializable(SAVEINSTANCE_DATA);
            // WORKAROUND: Note that Android deserializes any list as an ArrayList from a bundle.
            // See http://stackoverflow.com/questions/12300886/
            m_adapter.setData(new LinkedList<PrivateMessage>(adapterdata));
        }
        if (savedInstanceState.containsKey(SAVEINSTANCE_HASNOMORECACHED)) {
            m_hasNoMoreCached = savedInstanceState.getBoolean(SAVEINSTANCE_HASNOMORECACHED);
        }
        if (savedInstanceState.containsKey(SAVEINSTANCE_HASNOMOREONLINE)) {
            m_hasNoMoreOnline = savedInstanceState.getBoolean(SAVEINSTANCE_HASNOMOREONLINE);
        }
        m_title = savedInstanceState.getString(SAVEINSTANCE_USERNAME);
    }

    private void updateConversationSubtitle(){
        if(m_conversationState != null && m_conversationState.getOtherUserLastRead() != null){
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.ReadOn));
            sb.append(StringUtility.WHITE_SPACE);
            sb.append(DateUtility.getTimeOrDateString(m_conversationState.getOtherUserLastRead()));
            getSherlockActivity().getSupportActionBar().setSubtitle(sb.toString());
        } else {
            getSherlockActivity().getSupportActionBar().setSubtitle(null);
        }
    }

    private void updateAdapterButtonState() {
        boolean noneToLoad = m_hasNoMoreCached && m_hasNoMoreOnline;
        m_adapter.setShowLoadMore(!noneToLoad);
    }

    private void openProfileFragment() {
        DisplayProfileFragment f = new DisplayProfileFragment();
        Bundle b = new Bundle();
        b.putSerializable(ArgumentConstants.ARG_USERID, m_profileId);
        f.setArguments(b);

        FragmentManager manager = getSherlockActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
    }

    private void openReportFragment() {
        ReportUserFragment f = new ReportUserFragment();
        Bundle b = new Bundle();
        b.putSerializable(ArgumentConstants.ARG_USERID, m_profileId);
        f.setArguments(b);
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.replace(R.id.fragment_container_frame, f).addToBackStack(null).commit();
    }

    private void sendPreActions() {
        ProgressFragmentDialog f = (ProgressFragmentDialog) getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (f == null) {
            ProgressFragmentDialog dialog = new ProgressFragmentDialog();
            dialog.setMessageResource(R.string.WaitSendMessage);
            dialog.setRetainInstance(true);
            dialog.setCancelable(false); // TODO
            f = dialog;
        }
        f.show(getSherlockActivity().getSupportFragmentManager(), FRAGMENT_TAG);
    }

    private void handleExceptionPostLoad(Loader<?> loader, int type, Exception e) {
        Log.w(TAG, "Message loader " + type + " failed with exception", e);

        switch (type) {
            // All offline loaders: Any exception is bad, should be logged
            case R.id.loader_message_initial: 
            case R.id.loader_message_moreoldDB: {
                Log.w(TAG, "Offline loader " + loader.getClass().getSimpleName() + "  failed with exception", e);
                Toast.makeText(getSherlockActivity(), R.string.ErrorGeneric, Toast.LENGTH_SHORT).show();
            }
            // Online loaders: Anything else than an IOException should be logged and is bad.
            case R.id.loader_message_empty:{
                loaderExceptionHandling(loader, e);
                break;
            }
            case R.id.loader_message_moreoldOnline: {
                loaderExceptionHandling(loader, e);
                m_hasNoMoreOnline = false;
                break;
            }
            case R.id.loader_message_refresh: {
                loaderExceptionHandling(loader, e);
                break;
            }
            case R.id.loader_message_send: {
                loaderExceptionHandling(loader, e);
                dismissSendingDialog();
                break;
            }
            default:
                throw new IllegalArgumentException("Unknwon loader finished");
        }
    }

    private void loaderExceptionHandling(Loader<?> loader, Exception e) {
        if(e instanceof IOException){
            showToastNetworkError(e);
        } else {
            Log.w(TAG, "Loader " + loader.getClass().getSimpleName() + "  failed with exception", e);
            Toast.makeText(getSherlockActivity(), R.string.ErrorGeneric, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows a toast message that the network is unavailable.
     */
    private void showToastNetworkError(Exception e) {
        String string = getString(R.string.ErrorNoNetworkGenericShort);
        
        // FIXME Remove debugging info
        string = string + "\n(" + e.getClass().getSimpleName() + "\n" + e.getMessage() +")";
        
        Toast.makeText(getSherlockActivity(), string, Toast.LENGTH_LONG).show();
    }

    private void startEmpty() {
        Bundle b = new Bundle();
        b.putString(ArgumentConstants.ARG_USERID, m_profileId);

        getLoaderManager().restartLoader(R.id.loader_message_empty, b, this);
    }

    private void startRefresh() {
        Bundle b = new Bundle();
        b.putString(ArgumentConstants.ARG_USERID, m_profileId);
        Long lastRId = NVLUtility.nvl(messageService.getLatestMessageId(m_profileId), 0L);
        b.putLong(ArgumentConstants.ARG_ID, lastRId);
        getLoaderManager().restartLoader(R.id.loader_message_refresh, b, this);
        startRefreshConversationState();
    }
    
    private void startRefreshConversationState() {
        getLoaderManager().restartLoader(R.id.loader_conversationstatus, null, m_conversationStatusCallback);
    }

    private void startMoreOldOnline() {
        int count = m_adapter.getCount();
        if (count < 2) {
            Log.w(TAG, "State is inconsistent. Load more button clicked, although no message present to use the id.");
            return;
        }

        PrivateMessage item = (PrivateMessage) m_adapter.getItem(1);
        if (item == null) {
            Log.w(ConversationFragment.TAG, "Oldest message in apiAdapter was null, cannot load older messages!");
            return;
        }
        Long lastid = item.getMessageHead().getMessageId();

        Bundle b = new Bundle();
        b.putString(ArgumentConstants.ARG_USERID, m_profileId);
        b.putLong(ArgumentConstants.ARG_ID, lastid);
        // Try DB. It will check for itself
        getLoaderManager().restartLoader(R.id.loader_message_moreoldOnline, b, ConversationFragment.this);
    }

    private void showToastError() {
        Toast.makeText(getSherlockActivity(), R.string.ErrorGeneric, Toast.LENGTH_SHORT).show();
    }

    private void doSendPostActions(MessageResult res) {
        List<PrivateMessage> unreadMessages = res.getUnreadMessages();
        if (unreadMessages != null) {
            // There are unread messages!
            // Append them!
            for (PrivateMessage privateMessage : unreadMessages) {
                m_adapter.add(privateMessage);
            }

        }
        if (res.getSentMessage() != null) {
            m_adapter.add(res.getSentMessage());
        }
        dismissSendingDialog();
        // Clear text
        ((EditText) getView().findViewById(R.id.conversation_fragment_messageEditText)).setText("");
        m_listView.smoothScrollToPosition(m_adapter.getCount() - 1);
    }

    private void dismissSendingDialog() {
        DialogFragment dialog = (DialogFragment) getSherlockActivity().getSupportFragmentManager().findFragmentByTag(
                FRAGMENT_TAG);
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

}
