package ch.defiant.purplesky.activities.chatlist;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.util.NVLUtility;

/**
 * @author Patrick Bänziger
 */
public class ConversationActivity extends BaseFragmentActivity {

    @Override
    public int getSelfNavigationIndex() {
        return NAVIGATION_INDEX_INVALID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_host);

        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString(ArgumentConstants.ARG_USERID, getIntent().getStringExtra(ArgumentConstants.ARG_USERID));
        args.putString(ArgumentConstants.ARG_NAME, getIntent().getStringExtra(ArgumentConstants.ARG_NAME));

        fragment.setArguments(args);
        getFragmentManager().beginTransaction().add(R.id.fragment_host_frame, fragment ).commit();

    }

}
