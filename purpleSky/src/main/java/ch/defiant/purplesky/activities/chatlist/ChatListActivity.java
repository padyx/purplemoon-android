package ch.defiant.purplesky.activities.chatlist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.activities.EventActivity;
import ch.defiant.purplesky.activities.common.BaseFragmentActivity;
import ch.defiant.purplesky.animation.WeightAnimation;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Promotion;
import ch.defiant.purplesky.beans.promotion.PromotionPicture;
import ch.defiant.purplesky.constants.ArgumentConstants;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.core.PreferenceUtility;
import ch.defiant.purplesky.core.UpgradeHandler;
import ch.defiant.purplesky.fragments.conversation.ConversationFragment;
import ch.defiant.purplesky.gcm.GcmRegisterTask;
import ch.defiant.purplesky.interfaces.IChatListActivity;
import ch.defiant.purplesky.interfaces.IDateProvider;
import ch.defiant.purplesky.loaders.UpgradeTask;
import ch.defiant.purplesky.loaders.promotions.PromotionLoader;
import ch.defiant.purplesky.util.CollectionUtil;
import ch.defiant.purplesky.util.Holder;
import ch.defiant.purplesky.util.TemporaryUtility;

public class ChatListActivity extends BaseFragmentActivity
        implements IChatListActivity, LoaderManager.LoaderCallbacks<Holder<List<Promotion>>> {

    private static final String TAG = ChatListActivity.class.getSimpleName();
    /**
     * Interval in which the promotions should be shown
     */
    private static final long PROMO_SHOW_INTERVAL = 12*60*60*1000;

    @Inject
    protected IPromotionAdapter m_promotionAdapter;
    @Inject
    protected IDateProvider m_dateProvider;
    @Inject
    protected IPurplemoonAPIAdapter m_apiAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chatlist);
        setActionBarTitle(getString(R.string.Messages), null);
        if(getActionBar() != null) {
            getActionBar().setIcon(R.drawable.ic_launcher);
        }

        UpgradeHandler upgradeHandler = new UpgradeHandler(m_apiAdapter);
        if(upgradeHandler.needsUpgrade(this)){
            UpgradeTask task = new UpgradeTask(this, m_apiAdapter);
            if(UpgradeTask.INSTANCE.compareAndSet(null, task)){
                task.execute();
            }
        } else {
            // Self healing: Execute once every 24 hours
            SharedPreferences preferences = PreferenceUtility.getPreferences();
            if(preferences.getBoolean(PreferenceConstants.updateEnabled, false)){
                long lastAttempt = preferences.getLong(PreferenceConstants.lastPushRegistrationAttempt, 0);
                long currentTime = System.currentTimeMillis();
                if(isPushSelfHealAttemptNeeded(lastAttempt, currentTime)) {
                    Log.i(TAG, "Starting notification self-healing task");

                    GcmRegisterTask task = new GcmRegisterTask(m_apiAdapter);
                    if (GcmRegisterTask.INSTANCE.compareAndSet(null, task)) {
                        task.execute();
                    }
                    preferences.edit().putLong(PreferenceConstants.lastPushRegistrationAttempt, currentTime).apply();
                }
            }
            // Don't check for promos when upgrading.
            startPromotionLoading();
        }


    }

    private boolean isPushSelfHealAttemptNeeded(long lastAttempt, long currentTime) {
        return currentTime -  lastAttempt > 24 * 60 *60 * 1000;
    }

    private void startPromotionLoading() {
        SharedPreferences preferences = PreferenceUtility.getPreferences();
        if(preferences.getBoolean(PreferenceConstants.promotionEnabled, true)){
            long lastSeen = preferences.getLong(PreferenceConstants.promotionLastShown, 0);
            if(BuildConfig.DEBUG || System.currentTimeMillis() - lastSeen > PROMO_SHOW_INTERVAL) {
                getLoaderManager().restartLoader(R.id.loader_promotions, null, this);
            }
        }
    }

    @Override
    public int getSelfNavigationIndex() {
        return 0;
    }

    @Override
    public boolean isSelfSelectionReloads() {
        return true;
    }

    @Override
    public void conversationSelected(String userId, String username) {
        final FragmentManager fragmentManager = getFragmentManager();
        final View container = findViewById(R.id.promotionContainer);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) container.getLayoutParams();
        lp.weight = 0;
        container.setLayoutParams(lp);

        Fragment f = fragmentManager.findFragmentById(R.id.conversation_fragment);
        if(f != null && f.isInLayout()){
            // Two pane layout
            // Update it with the new conversation
            ConversationFragment conversationFragment = (ConversationFragment) f;
            conversationFragment.showConversationWithUser(userId, username);
        } else {
            // Not available... Open activity
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra(ArgumentConstants.ARG_USERID, userId);
            intent.putExtra(ArgumentConstants.ARG_NAME, username);
            startActivity(intent);
        }
    }

    @Override
    public Loader<Holder<List<Promotion>>> onCreateLoader(int id, Bundle args) {
        return new PromotionLoader(this, m_promotionAdapter);
    }

    @Override
    public void onLoadFinished(Loader<Holder<List<Promotion>>> loader, Holder<List<Promotion>> data) {
        if(data == null) {
            Log.i(TAG, "No data received for promotions");
        }
        else if(data.isException() ) {
            Log.i(TAG, "Could not retrieve promotions due to exceptions", data.getException());
        }
        else if (data.getContainedObject() != null && !data.getContainedObject().isEmpty()) {
            selectAndShowPromotion(data.getContainedObject());
        }

        getLoaderManager().destroyLoader(R.id.loader_promotions);
    }

    private void selectAndShowPromotion(@NonNull List<Promotion> promos) {
        List<Promotion> list = filterPromotions(promos, m_dateProvider);
        if (CollectionUtil.safeSize(list) == 0) return;

        int totalWeights = 0;
        for (Promotion p : list){
            totalWeights += p.getImportance();
        }

        // Generates an int between 0 and totalWeights-1 (including)
        final int i = new Random().nextInt(totalWeights);
        // Find the proper one now...
        Promotion chosen = null;
        int curr = 0;
        for (Promotion p:list){
            if (curr + p.getImportance() > i){
                // Match!
                chosen = p;
                break;
            }
            curr += p.getImportance();
        }
        if(chosen == null){
            Log.e(TAG, "Random choice of promotion item failed! (total: "+totalWeights + ", chosen: "+i+")");
            Log.d(TAG, "Promotions were: " + promos);
            return;
        }

        showPromotion(chosen);
    }

    private static List<Promotion> filterPromotions(List<Promotion> promos, IDateProvider dateProvider) {
        ArrayList<Promotion> list = new ArrayList<>();
        for(Promotion p : promos){
            if(TemporaryUtility.isValid(p, dateProvider)){
                list.add(p);
            }
        }
        return list;
    }

    private void showPromotion(@NonNull final Promotion promo) {
        final WebView webview = (WebView) findViewById(R.id.promotionWebView);
        final View clickOverlay = findViewById(R.id.webview_clickOverlay);
        final View container = findViewById(R.id.promotionContainer);
        if(webview == null || container == null){
            return;
        }

        String data = promoToHtml(promo).toString();
        webview.loadData(data, "text/html; charset=utf-8", "UTF-8");

        final View dismissButton = findViewById(R.id.dismissLabel);
        final int eventId = promo.getEventId();
        clickOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check that the view is fully extended - ignore accidental click
                if ( ((LinearLayout.LayoutParams) container.getLayoutParams()).weight != 1 ){
                    return;
                }

                if (eventId != 0) {
                    Intent intent = new Intent(ChatListActivity.this, EventActivity.class);
                    intent.putExtra(ArgumentConstants.ARG_ID, eventId);
                    startActivity(intent);
                } else if (promo.getEventUri() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(promo.getEventUri());
                    startActivity(intent);
                }
            }
        });

        PreferenceUtility.getPreferences().edit().
                putLong(PreferenceConstants.promotionLastShown, System.currentTimeMillis()).
                apply();
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeightAnimation anim = new WeightAnimation(1, 0, container);
                anim.setInterpolator(new AccelerateInterpolator());
                anim.setDuration(1000);
                container.startAnimation(anim);
            }
        });

        WeightAnimation anim = new WeightAnimation(0, 1, container);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(1000);
        anim.setStartOffset(1000);
        container.startAnimation(anim);
    }

    @Override
    public void onLoaderReset(Loader<Holder<List<Promotion>>> loader) { }


    @NonNull
    private static CharSequence promoToHtml(@NonNull Promotion p){
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head>\n");
        sb.append("<body style='font-family: Arial, Verdana, sans-serif;'>");
        // TODO Choose properly
        PromotionPicture promotionPicture = CollectionUtil.firstElement(p.getPromotionPictures());
        sb.append("<h3>");
        sb.append(TextUtils.htmlEncode(p.getTitle()));
        sb.append("</h3>");
        if(promotionPicture != null){
            // TODO Add link to largest picture
            sb.append("<img style='float:left; margin-right: 8px; margin-bottom: 8px;' src='");
            sb.append(promotionPicture.getUri().toString());
            sb.append("' width='25%'/>");
        }
        sb.append("<p>");
        sb.append(TextUtils.htmlEncode(p.getText()));
        sb.append("</p>");
        sb.append("</body></html>");
        return sb;
    }
}