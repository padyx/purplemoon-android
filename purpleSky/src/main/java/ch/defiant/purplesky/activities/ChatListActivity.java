package ch.defiant.purplesky.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.fragments.ChatListFragment;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.interfaces.IChatListActivity;

public class ChatListActivity extends BaseFragmentActivity implements IChatListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chatlist);

        View containerFrame = findViewById(R.id.fragment_container_frame);
        // Check whether the activity is using the layout version with the container frame
        if (containerFrame != null) {
            // Single layout

            // If we are restored, no need to created the fragment again
            if (savedInstanceState != null) {
                return;
            }

            // Create the conversation fragment
            ChatListFragment fragment = new ChatListFragment();

            // Add the fragment
            getFragmentManager().beginTransaction().add(R.id.fragment_container_frame, fragment).commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final ImageView img = (ImageView) findViewById(R.id.image);

        WeightAnimation anim = new WeightAnimation(0, 1, img);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(1000);
        anim.setStartOffset(1000);
        img.startAnimation(anim);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeightAnimation anim = new WeightAnimation(1, 0, img);
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(1000);
                img.startAnimation(anim);
            }
        });
    }

    @Override
    public int getSelfNavigationIndex() {
        return 0;
    }

    @Override
    public boolean isSelfSelectionReloads() {
        // Self selection reloads
        return true;
    }

    @Override
    public void conversationSelected(String userId) {
        final FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.conversation_fragment);
        if(fragment != null){
            // Two pane layout
            // Update it with the new conversation
            ConversationFragment conversationFragment = (ConversationFragment) fragment;
            conversationFragment.showConversationWithUser(userId);
        } else {
            // Not available... One pane layout, so make a transaction
            fragment = new ConversationFragment();
            Bundle args = new Bundle();
            args.putString(ArgumentConstants.ARG_USERID, userId);
            fragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.fragment_container_frame, fragment).addToBackStack(null).commit();
        }
    }

    private static class WeightAnimation extends Animation {
        private final float mStartWeight;
        private final float mDeltaWeight;
        private final View mContent;

        public WeightAnimation(float startWeight, float endWeight, View v) {
            mStartWeight = startWeight;
            mDeltaWeight = endWeight - startWeight;
            mContent = v;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContent.getLayoutParams();
            lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
            mContent.setLayoutParams(lp);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}