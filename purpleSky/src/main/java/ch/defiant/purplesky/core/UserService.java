package ch.defiant.purplesky.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;

import javax.inject.Inject;

import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.api.IPurplemoonAPIAdapter;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.constants.PreferenceConstants;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.StringUtility;

public class UserService {
    private static final String TAG = UserService.class.getSimpleName();

    @Inject
    protected IPurplemoonAPIAdapter apiAdapter;

    // Caches: ProfileId -> <User, RetrievalDate>
    private LruCache<String, MinimalUser> m_cache;
    private DetailedUser m_ownUserData;

    /**
     * Constructor but protected. Use the {@link PurpleSkyApplication#getUserService()} to retrieve an instance
     */
    protected UserService() {
        // Private constructor
        m_cache = new LruCache<>(500);
        PurpleSkyApplication.get().inject(this);
    }

    public Map<String, MinimalUser> getMinimalUsers(List<String> profileIds, boolean withOnlineStatus)
            throws IOException, PurpleSkyException {
        HashMap<String, MinimalUser> map = new HashMap<>();
        if (profileIds == null || profileIds.isEmpty()) {
            return map;
        }

        List<String> uncached = new ArrayList<>();
        for (String id : profileIds) {
            if (StringUtility.isNullOrEmpty(id)) {
                continue;
            }

            // Check the caches
            MinimalUser cachedVersion = getCachedUser(id, MinimalUser.class);
            if (cachedVersion != null) {
                map.put(id, cachedVersion);
            } else {
                uncached.add(id);
            }
        }

        if (withOnlineStatus) {
            // Add online status to all of them
            Map<String, Pair<OnlineStatus, String>> stati = apiAdapter.getOnlineStatus(new ArrayList<>(map.keySet()));
            for (String id : stati.keySet()) {
                Pair<OnlineStatus, String> stats = stati.get(id);
                if (stats == null) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "Could not retrieve the online status for id " + id);
                    }
                    continue;
                }
                MinimalUser u = map.get(id);
                u.setOnlineStatus(stats.first);
                u.setOnlineStatusText(stats.second);
            }
        }

        Map<String, MinimalUser> minimalRetrieved = getMinimalUserForceUpdate(uncached, withOnlineStatus);
        // Retrieve
        if (minimalRetrieved != null) {
            map.putAll(minimalRetrieved);
        }

        return map;
    }

    public MinimalUser getMinimalUser(String profileId, boolean withOnlineStat)
            throws IOException, PurpleSkyException {
        // Check the caches
        if (StringUtility.isNullOrEmpty(profileId))
            return null;

        MinimalUser cachedVersion = getCachedUser(profileId, MinimalUser.class);
        if (cachedVersion != null)
            return cachedVersion;

        return getMinimalUserForceUpdate(profileId, withOnlineStat);
    }

    public Map<String, PreviewUser> getPreviewUsers(List<String> profileIds, boolean withOnlineStatus) throws IOException, PurpleSkyException {
        Map<String, PreviewUser> map = new HashMap<>();
        if (profileIds == null) {
            return map;
        }

        List<String> uncached = new ArrayList<>();

        for (String id : profileIds) {
            if (StringUtility.isNullOrEmpty(id))
                continue;

            // Check the caches
            PreviewUser cachedVersion = getCachedUser(id, PreviewUser.class);
            if (cachedVersion != null) {
                map.put(id, cachedVersion);
            } else {
                uncached.add(id);
            }
        }

        if (withOnlineStatus) {
            // Add online status to all of them
            Map<String, Pair<OnlineStatus, String>> stati = apiAdapter.getOnlineStatus(new ArrayList<>(map.keySet()));
            for (String id : stati.keySet()) {
                Pair<OnlineStatus, String> stats = stati.get(id);
                if (stats == null) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG, "Could not retrieve the online status for id " + id);
                    }
                    continue;
                }
                MinimalUser u = map.get(id);
                u.setOnlineStatus(stats.first);
                u.setOnlineStatusText(stats.second);
            }
        }

        Map<String, PreviewUser> minimalRetrieved = getPreviewUserForceUpdate(uncached, withOnlineStatus);
        // Retrieve
        if (minimalRetrieved != null) {
            map.putAll(minimalRetrieved);
        }

        return map;
    }

    public PreviewUser getPreviewUser(String profileId, boolean withOnlineStatus) throws IOException, PurpleSkyException {
        // Check the caches
        if (StringUtility.isNullOrEmpty(profileId))
            return null;

        PreviewUser cachedVersion = getCachedUser(profileId, PreviewUser.class);
        if (cachedVersion != null)
            return cachedVersion;

        return getPreviewUserForceUpdate(profileId, withOnlineStatus);
    }

    /**
     * Retrieve the detailed user bean. Is always
     * 
     * @param profileId
     * @return
     * @throws IOException
     * @throws PurpleSkyException
     */
    public DetailedUser getDetailedUser(String profileId) throws IOException, PurpleSkyException {
        // Check the caches
        if (StringUtility.isNullOrEmpty(profileId))
            return null;

        DetailedUser cachedVersion = getCachedUser(profileId, DetailedUser.class);
        if (cachedVersion != null)
            return cachedVersion;

        return getDetailedUserForceUpdate(profileId);
    }

    private Map<String, MinimalUser> getMinimalUserForceUpdate(List<String> profileIds, boolean withOnlineStatus) throws IOException,
            PurpleSkyException {
        Map<String, MinimalUser> map = apiAdapter.getMinimalUserData(profileIds, withOnlineStatus);
        for (String id : map.keySet()) {
            m_cache.put(id, map.get(id));
        }
        return map;
    }

    private MinimalUser getMinimalUserForceUpdate(String profileId, boolean withOnlineStatus)
            throws IOException, PurpleSkyException {
        MinimalUser minimalUserData = apiAdapter.getMinimalUserData(profileId, withOnlineStatus);
        // Store in caches
        m_cache.put(profileId, minimalUserData);
        return minimalUserData;
    }

    private Map<String, PreviewUser> getPreviewUserForceUpdate(List<String> profileIds, boolean withOnlineStatus)
            throws IOException, PurpleSkyException {
        Map<String, PreviewUser> map = apiAdapter.getPreviewUserData(profileIds, withOnlineStatus);
        for (String id : map.keySet()) {
            m_cache.put(id, map.get(id));
        }
        return map;
    }

    private PreviewUser getPreviewUserForceUpdate(String profileId, boolean withOnlineStatus)
            throws IOException, PurpleSkyException {
        PreviewUser fullUserData = apiAdapter.getPreviewUserData(profileId, withOnlineStatus);
        m_cache.put(profileId, fullUserData);

        return fullUserData;
    }

    private DetailedUser getDetailedUserForceUpdate(String profileId)
            throws IOException, PurpleSkyException {
        DetailedUser fullUserData = apiAdapter.getDetailedUserData(profileId);
        m_cache.put(profileId, fullUserData);

        return fullUserData;
    }

    /**
     * Checks if the user is a poweruser (cached only).
     * 
     * @return <tt>true</tt> if the user still has a valid power user status. <tt>false</tt> if the status is not known or the user is not a
     *         poweruser.
     */
    public static boolean isCachedPowerUser() {
        if (PreferenceUtility.getPreferences().contains(PreferenceConstants.powerUserExpiry)) {
            long expiry = PreferenceUtility.getPreferences().getLong(PreferenceConstants.powerUserExpiry, 0);
            return (new Date(expiry).after(new Date()));
        } else {
            return false;
        }
    }

    private <T extends MinimalUser> T getCachedUser(String profileId, Class<T> clazz) {
        if (StringUtility.isNullOrEmpty(profileId)) {
            return null;
        }
        MinimalUser user = m_cache.get(profileId);
        if (user == null) {
            return null;
        }

        // Is this class we got, the one we requested or a subclass?
        if (clazz.isAssignableFrom(user.getClass())) {

            // Yes... So cast it
            @SuppressWarnings("unchecked")
            T tmp = (T) user;

            // Is it also valid?
            if (isStillValid(user)) {
                return tmp;
            } else {
                m_cache.remove(profileId);
                return null;
            }
        } else {
            // No... So fetch the one we wanted.
            return null;
        }
    }

    private boolean isStillValid(MinimalUser user) {
        return System.currentTimeMillis() - user.getRetrievalTime() <= user.getExpiryDuration();
    }

    public static URL getUserPreviewPictureUrl(MinimalUser u, UserPreviewPictureSize size) {
        if (u.getProfilePictureURLDirectory() == null) {
            Log.d(TAG, "No preview picture URL available for user with id " + u.getUserId());
            return null;
        }
        return getUserPreviewPictureUrl(u.getProfilePictureURLDirectory().toString(), size);
    }
    
    public static URL getUserPreviewPictureUrl(String urlPrefix, UserPreviewPictureSize size){
        if (size == null)
            return null;
        
        try {
            return new URL(urlPrefix + size.getAPIValue());
        } catch (MalformedURLException e) {
            Log.w(TAG, "Malformed URL when creating preview picture url.");
            return null;
        }
    }

    /**
     * Will add the passed user to the cache if
     * <ul>
     * <li>the cache does not contain a non-expired entry for the passed user's id.</li>
     * <li>the cache contains a valid entity and the passed bean's class is a subclass of (or same class as) the stored bean's class.</li>
     * </ul>
     * 
     * @param newBean
     *            User bean to be stored
     */
    public void addToCache(MinimalUser newBean) {
        // TODO Test

        if (newBean == null || StringUtility.isNullOrEmpty(newBean.getUserId())) {
            return;
        }

        MinimalUser cached = m_cache.get(newBean.getUserId());
        if (cached == null) {
            // Nop
        } else if (!isStillValid(cached)) {
            // Remove from cache
            m_cache.remove(cached.getUserId());
            cached = null; // Handle as if not found
        }

        if (cached == null) {
            m_cache.put(newBean.getUserId(), newBean);
        } else if (newBean.getClass() == cached.getClass() || cached.getClass().isAssignableFrom(newBean.getClass())) {
            // Can replace it
            m_cache.put(newBean.getUserId(), newBean);
        } else {
            // Nothing to do
        }
    }

    public void clearCache() {
        m_cache.evictAll();
    }

    public enum UserPreviewPictureSize {
        // Make sure they are in order of pixel size ascending!
        TINY(PurplemoonAPIConstantsV1.PREVIEWIMAGE_URLPOSTFIX_TINY, 35),
        SMALL(PurplemoonAPIConstantsV1.PREVIEWIMAGE_URLPOSTFIX_SMALL, 50),
        MEDIUM(PurplemoonAPIConstantsV1.PREVIEWIMAGE_URLPOSTFIX_MEDIUM, 100),
        LARGE(PurplemoonAPIConstantsV1.PREVIEWIMAGE_URLPOSTFIX_LARGE, 200);

        private String m_APIValue;
        private int m_size;

        UserPreviewPictureSize(String apiValue, int size) {
            m_APIValue = apiValue;
            m_size = size;
        }

        public String getAPIValue() {
            return m_APIValue;
        }

        public int getSize() {
            return m_size;
        }

        public static UserPreviewPictureSize getLargest() {
            return LARGE;
        }

        public static UserPreviewPictureSize getPictureSizeForDpi(int minDp, Resources r) {
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minDp, r.getDisplayMetrics());

            return getPictureForPx(px);
        }

        public static UserPreviewPictureSize getPictureForPx(float px) {
            for (UserPreviewPictureSize u : UserPreviewPictureSize.values()) {
                if (u.getSize() >= px) {
                    return u;
                }
            }

            // Nothing is large enough... Return largest
            return getLargest();
        }

    }

}
