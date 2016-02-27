package ch.defiant.purplesky.fragments.conversation;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.DisplayProfileActivity;
import ch.defiant.purplesky.activities.ReportActivity;
import ch.defiant.purplesky.activities.chatlist.ConversationActivity;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.adapters.message.MessageAdapter;
import ch.defiant.purplesky.api.conversation.IConversationAdapter;
import ch.defiant.purplesky.beans.IPrivateMessage;
import ch.defiant.purplesky.beans.PendingMessage;
import ch.defiant.purplesky.beans.PrivateMessage;
import ch.defiant.purplesky.beans.PrivateMessageHead;
import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.core.IMessageService;
import ch.defiant.purplesky.core.MessageResult;
import ch.defiant.purplesky.customwidgets.ProgressFragmentDialog;
import ch.defiant.purplesky.dao.IPendingMessageDao;
import ch.defiant.purplesky.dialogs.AlertDialogFragment;
import ch.defiant.purplesky.dialogs.IAlertDialogFragmentResponder;
import ch.defiant.purplesky.enums.MessageType;
import ch.defiant.purplesky.fragments.BaseFragment;
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
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.DateUtility;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.LayoutUtility;
import ch.defiant.purplesky.util.NVLUtility;
import ch.defiant.purplesky.util.StringUtility;

public class ConversationFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Holder<MessageResult>> {

    @Inject
    protected IMessageService messageService;
    @Inject
    protected IConversationAdapter conversationAdapter;
    @Inject
    protected IPendingMessageDao pendingMessageDao;

    private ViewGroup m_chatGroupBox;

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
                Toast.makeText(getActivity(), R.string.MustEnterTextMessage, Toast.LENGTH_LONG).show();
                return;
            }

            Long lastReceivedTS = messageService.getLatestReceivedMessageTimestamp(m_profileId);
            if (lastReceivedTS == null) {
                lastReceivedTS = 0L;
            }

            PendingMessage message = new PendingMessage();
            message.setRecipientId(Long.valueOf(m_profileId));
            message.setMessageText(messageField.getText().toString());

            Bundle bundle = new Bundle();
            bundle.putString(ArgumentConstants.ARG_USERID, m_profileId);
            bundle.putSerializable(ArgumentConstants.ARG_MESSAGE, message);
            bundle.putLong(ArgumentConstants.ARG_TIMESTAMP, lastReceivedTS);

            getLoaderManager().restartLoader(R.id.loader_message_send, bundle, ConversationFragment.this);

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageField.getWindowToken(), 0);
        }
    }

    private class ImageLoaderCallback implements LoaderManager.LoaderCallbacks<Drawable> {

        @Override
        public Loader<Drawable> onCreateLoader(int arg0, Bundle arg1) {
            Bundle b = new Bundle();
            b.putString(ArgumentConstants.ARG_USERID, m_profileId);
            return new ActionBarImageLoader(getActivity(), b, apiAdapter);
        }

        @Override
        public void onLoadFinished(Loader<Drawable> arg0, Drawable result) {
            Activity activity = getActivity();
            if (activity instanceof BaseFragmentActivity) {
                BaseFragmentActivity fragmentActivity = (BaseFragmentActivity) activity;
                Toolbar toolbar = fragmentActivity.getActionbarToolbar();
                if(toolbar != null) {
                    if (result != null) {
                        fragmentActivity.getActionbarToolbar().setLogo(result);
                    } else {
                        fragmentActivity.getActionbarToolbar().setLogo(R.drawable.ic_launcher);
                    }
                }
            }
            getLoaderManager().destroyLoader(R.id.loader_message_profileImage);
        }

        @Override
        public void onLoaderReset(Loader<Drawable> arg0) { }

    }

    private class ConversationStatusCallback implements LoaderManager.LoaderCallbacks<Holder<UserMessageHistoryBean>> {
        @Override
        public Loader<Holder<UserMessageHistoryBean>> onCreateLoader(int arg0, Bundle arg1) {
            Bundle b = new Bundle();
            b.putString(ArgumentConstants.ARG_USERID, m_profileId);
            return new ConversationStatusLoader(getActivity(), b, conversationAdapter);
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
    private final ConversationStatusCallback m_conversationStatusCallback = new ConversationStatusCallback();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(getActivity() instanceof ConversationActivity);
        String profileId = null;
        String title = null;
        Bundle args = getArguments();
        if(args != null) {
            title = args.getString(ArgumentConstants.ARG_NAME);
            if(title == null){
                Log.e(TAG, "Missing username!");
            }
            profileId = args.getString(ArgumentConstants.ARG_USERID);
            m_conversationState = (UserMessageHistoryBean) args.getSerializable(ArgumentConstants.ARG_MESSAGEHISTORYBEAN);
        }
        m_adapter = new MessageAdapter(this);

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        if(profileId != null){
            showConversationWithUser(profileId, title);
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
        m_chatGroupBox = (ViewGroup) inflated.findViewById(R.id.conversation_fragment_chatGroupBox);
        LayoutUtility.setEnabledRecursive(m_chatGroupBox, StringUtility.isNotNullOrEmpty(m_profileId));

        Toolbar toolbar = (((BaseFragmentActivity) getActivity()).getActionbarToolbar());
        if(toolbar != null) {
            toolbar.setLogo(R.drawable.social_person);
        }
        return inflated;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.conversation_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
    public void onResume() {
        super.onResume();
        if(m_profileId != null) {
            if (m_imgLoaderCallback == null) {
                m_imgLoaderCallback = new ImageLoaderCallback();
            }
            getActivity().getLoaderManager().restartLoader(R.id.loader_message_profileImage, null,
                    m_imgLoaderCallback);
        }
        setTitle(m_title);
    }

    public void showConversationWithUser(String userId, String username){
        if(m_chatGroupBox != null){
            LayoutUtility.setEnabledRecursive(m_chatGroupBox, StringUtility.isNotNullOrEmpty(userId));
        }
        if(CompareUtility.notEquals(userId, m_profileId)) {
            m_adapter.clear();
            m_hasNoMoreCached = false;
            m_hasNoMoreOnline = false;

            m_profileId = userId;
            m_title = username;
            setTitle(m_title);
            Bundle args = new Bundle();
            args.putString(ArgumentConstants.ARG_USERID, m_profileId);
            if (m_profileId != null) {
                // TODO PBN Change adapter for easier calculation?
                if (m_adapter.getCount() - (m_adapter.isShowLoadMore() ? 1 : 0) == 0) {
                    getLoaderManager().restartLoader(R.id.loader_message_initial, args, this);
                } else {
                    startRefresh();
                }
            }

            if(m_profileId != null) {
                if (m_imgLoaderCallback == null) {
                    m_imgLoaderCallback = new ImageLoaderCallback();
                }

                updateConversationSubtitle();
            }
        }
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
    public Loader<Holder<MessageResult>> onCreateLoader(final int type, final Bundle bundle) {
        final Activity context = getActivity();
        context.setProgressBarIndeterminateVisibility(true);
        switch (type) {
            case R.id.loader_message_empty:
                return new EmptyOnlineLoader(context, bundle, conversationAdapter, messageService);
            case R.id.loader_message_initial:
                return new InitialDBMessageLoader(context, bundle, conversationAdapter, messageService);
            case R.id.loader_message_moreoldDB:
                return new OlderMessageDBLoader(context, bundle, conversationAdapter, messageService);
            case R.id.loader_message_moreoldOnline:
                return new OlderMessageOnlineLoader(context, bundle, conversationAdapter, messageService);
            case R.id.loader_message_send:
                sendPreActions();
                return new SendMessageLoader(context, bundle, pendingMessageDao);
            case R.id.loader_message_refresh:
                return new RefreshMessageLoader(context, bundle, conversationAdapter, messageService);
            default:
                throw new UnsupportedOperationException("Unknown loader");
        }
    }

    @Override
    public void onLoadFinished(Loader<Holder<MessageResult>> loader, Holder<MessageResult> holder) {
        if (getActivity() != null) {
            getActivity().setProgressBarIndeterminateVisibility(false);
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

        List<IPrivateMessage> unreadMessages = res.getUnreadMessages();
        IPrivateMessage sendMessage = res.getSentMessage();
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
                for (IPrivateMessage pm : unreadMessages) {
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
                for (IPrivateMessage pm : unreadMessages) {
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
        getActivity().runOnUiThread(new NotifyAdapter());
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
        if(getActivity() instanceof BaseFragmentActivity && result != null){
            ((BaseFragmentActivity) getActivity()).setActionBarTitle(result);
        }
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(SAVEINSTANCE_DATA)) {
            @SuppressWarnings("unchecked")
            List<PrivateMessage> adapterdata = (List<PrivateMessage>) savedInstanceState
            .getSerializable(SAVEINSTANCE_DATA);
            // WORKAROUND: Note that Android deserializes any list as an ArrayList from a bundle.
            // See http://stackoverflow.com/questions/12300886/
            m_adapter.setData(new LinkedList<IPrivateMessage>(adapterdata));
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
        Toolbar toolbar = ((BaseFragmentActivity) getActivity()).getActionbarToolbar();
        if(toolbar==null){
            return;
        }
        if(m_conversationState != null && m_conversationState.getOtherUserLastRead() != null){
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.ReadOn));
            sb.append(StringUtility.WHITE_SPACE);
            sb.append(DateUtility.getTimeOrDateString(m_conversationState.getOtherUserLastRead()));
            toolbar.setSubtitle(sb.toString());
        } else {
            toolbar.setSubtitle(null);
        }
    }

    private void updateAdapterButtonState() {
        boolean noneToLoad = m_hasNoMoreCached && m_hasNoMoreOnline;
        m_adapter.setShowLoadMore(!noneToLoad);
    }

    private void openProfileFragment() {
        Intent intent = new Intent(getActivity(), DisplayProfileActivity.class);
        intent.putExtra(ArgumentConstants.ARG_USERID, m_profileId);
        getActivity().startActivity(intent);
    }

    private void openReportFragment() {
        Intent intent = new Intent(getActivity(), ReportActivity.class);
        intent.putExtra(ArgumentConstants.ARG_USERID, m_profileId);
        getActivity().startActivity(intent);
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
        f.show(getActivity().getFragmentManager(), FRAGMENT_TAG);
    }

    private void handleExceptionPostLoad(Loader<?> loader, int type, Exception e) {
        Log.w(TAG, "Message loader " + type + " failed with exception", e);

        switch (type) {
            // All offline loaders: Any exception is bad, should be logged
            case R.id.loader_message_initial: 
            case R.id.loader_message_moreoldDB: {
                Log.w(TAG, "Offline loader " + loader.getClass().getSimpleName() + "  failed with exception", e);
                Toast.makeText(getActivity(), R.string.ErrorGeneric, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), R.string.ErrorGeneric, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows a toast message that the network is unavailable.
     */
    private void showToastNetworkError(Exception e) {
        String string = getString(R.string.ErrorNoNetworkGenericShort);
        
        // FIXME Remove debugging info
        string = string + "\n(" + e.getClass().getSimpleName() + "\n" + e.getMessage() +")";
        
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
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
        Toast.makeText(getActivity(), R.string.ErrorGeneric, Toast.LENGTH_SHORT).show();
    }

    private void doSendPostActions(MessageResult res) {
        List<IPrivateMessage> unreadMessages = res.getUnreadMessages();
        if (unreadMessages != null) {
            // There are unread messages!
            // Append them!
            for (IPrivateMessage privateMessage : unreadMessages) {
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
        DialogFragment dialog = (DialogFragment) getActivity().getFragmentManager().findFragmentByTag(
                FRAGMENT_TAG);
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

}
