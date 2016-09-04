package ch.defiant.purplesky.activities.chatlist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.adapters.message.MessageAdapter;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.fragments.conversation.ConversationModelFragment;
import ch.defiant.purplesky.util.CompareUtility;
import ch.defiant.purplesky.util.NVLUtility;

/**
 * @author Patrick Bänziger
 */
public class ConversationActivity extends BaseFragmentActivity {

    private static final String SAVEINSTANCE_HASNOMORECACHED = "hasNoMoreCached";
    private static final String SAVEINSTANCE_HASNOMOREONLINE = "hasNoMoreOnline";
    private static final String SAVEINSTANCE_USERNAME = "username";

    private static final String MODEL_TAG = "conversation_model";
    private ConversationModelFragment m_modelFragment;
    private MessageAdapter m_messageAdapter;

    private String m_profileId;

    @Override
    public int getSelfNavigationIndex() {
        return NAVIGATION_INDEX_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_host);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();

        m_modelFragment = (ConversationModelFragment) fragmentManager.findFragmentByTag(MODEL_TAG);

        String profileId = getIntent().getStringExtra(ArgumentConstants.ARG_USERID);
        boolean isDifferentProfile = CompareUtility.notEquals(profileId, m_profileId);
        if(isDifferentProfile){
            m_profileId = profileId;
            if(m_modelFragment != null){
                trans.remove(m_modelFragment);
                m_modelFragment = null;
            }
        }

        if (m_modelFragment == null) {
            m_modelFragment = new ConversationModelFragment();
            trans.add(m_modelFragment, MODEL_TAG);
        }

        m_messageAdapter = new MessageAdapter(this, m_modelFragment);

        // TODO pbn Maybe recycle this fragment?
        ConversationFragment fragment = createConversationFragment();
        trans.add(R.id.fragment_host_frame, fragment);
        m_messageAdapter.setConversationFragment(fragment);

        if (!trans.isEmpty()) {
            trans.commit();
        }
    }

    @NonNull
    private ConversationFragment createConversationFragment() {
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString(ArgumentConstants.ARG_USERID, getIntent().getStringExtra(ArgumentConstants.ARG_USERID));
        args.putString(ArgumentConstants.ARG_NAME, getIntent().getStringExtra(ArgumentConstants.ARG_NAME));

        fragment.setArguments(args);
        return fragment;
    }

    public MessageAdapter getMessageAdapter(){
        return m_messageAdapter;
    }

}
