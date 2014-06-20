package ch.defiant.purplesky.constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.util.Log;
import ch.defiant.purplesky.BuildConfig;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1.ProfileDetails;
import ch.defiant.purplesky.core.PurpleSkyApplication;

/**
 * List of user details, indicating which groups exist and which items belong to that group.
 */
public final class ProfileListMap {

    private static final String TAG = ProfileListMap.class.getSimpleName();

    private static ProfileListMap m_instance;

    public static ProfileListMap getInstance() {
        if (m_instance == null) {
            m_instance = new ProfileListMap();
        }
        return m_instance;
    }

    /**
     * List of all groups, containing the string resource id's of the title for this group.
     */
    public final List<Integer> GROUPS;
    /**
     * List of all items in each group, containing the api keys for items. Same order of the groups as in {@link #GROUPS}!
     */
    public final List<List<String>> GROUP_LIST_APIKEYS;

    private final Set<String> SPECIAL = new HashSet<String>();

    private ProfileListMap() {
        GROUPS = new ArrayList<Integer>();
        GROUP_LIST_APIKEYS = new ArrayList<List<String>>();

        // Groups
        GROUPS.add(R.string.profileGroup_General);
        GROUPS.add(R.string.profileGroup_Body);
        GROUPS.add(R.string.profileGroup_Occupation);
        GROUPS.add(R.string.profileGroup_Beliefs);
        GROUPS.add(R.string.profileGroup_ChatContactHomepage);
        GROUPS.add(R.string.profileGroup_GetToKnow);
        GROUPS.add(R.string.profileGroup_GetToKnowAsPartner);
        GROUPS.add(R.string.profileGroup_AboutProfile);

        // General info
        ArrayList<String> generalList = new ArrayList<String>();
        GROUP_LIST_APIKEYS.add(generalList);
        generalList.add(ProfileDetails.BIRTHDATE);
        generalList.add(ProfileDetails.FIRST_NAME);
        generalList.add(ProfileDetails.NICKNAMES);
        generalList.add(ProfileDetails.LAST_NAME);
        generalList.add(ProfileDetails.EMAIL_ADDRESS);

        // Group: Body
        ArrayList<String> bodyList = new ArrayList<String>();
        GROUP_LIST_APIKEYS.add(bodyList);
        bodyList.add(ProfileDetails.HEIGHT);
        bodyList.add(ProfileDetails.WEIGHT);
        bodyList.add(ProfileDetails.PHYSIQUE);
        bodyList.add(ProfileDetails.EYE_COLOR);
        bodyList.add(ProfileDetails.HAIR_COLOR);
        bodyList.add(ProfileDetails.HAIR_LENGTH);
        bodyList.add(ProfileDetails.FACIAL_HAIR);

        ArrayList<String> occupationlist = new ArrayList<String>();
        GROUP_LIST_APIKEYS.add(occupationlist);
        occupationlist.add(ProfileDetails.OCCUPATION_LIST);

        ArrayList<String> beliefList = new ArrayList<String>();
        GROUP_LIST_APIKEYS.add(beliefList);
        beliefList.add(ProfileDetails.RELIGION);
        beliefList.add(ProfileDetails.POLITICS);
        beliefList.add(ProfileDetails.DRINKER);
        beliefList.add(ProfileDetails.SMOKER);
        beliefList.add(ProfileDetails.VEGETARIAN);
        beliefList.add(ProfileDetails.WANTS_KIDS);
        beliefList.add(ProfileDetails.HAS_KIDS);

        ArrayList<String> chatContactList = new ArrayList<String>();
        GROUP_LIST_APIKEYS.add(chatContactList);
        chatContactList.add(ProfileDetails.CHATS_FREQUENCY);
        chatContactList.add(ProfileDetails.CHATS_WHICH);
        chatContactList.add(ProfileDetails.CHATS_NAMES);
        chatContactList.add(ProfileDetails.HOMEPAGE);

        ArrayList<String> getToKnow = new ArrayList<String>();
        GROUP_LIST_APIKEYS.add(getToKnow);
        getToKnow.add(ProfileDetails.TARGET_FRIENDS);

        ArrayList<String> getToKnowPartner = new ArrayList<String>();
        GROUP_LIST_APIKEYS.add(getToKnowPartner);
        getToKnowPartner.add(ProfileDetails.TARGET_PARTNER);

        ArrayList<String> aboutProfile = new ArrayList<String>();
        GROUP_LIST_APIKEYS.add(aboutProfile);
        aboutProfile.add(ProfileDetails.PROFILE_LASTONLINE_DATE);
        aboutProfile.add(ProfileDetails.PROFILE_LASTUPDATE_DATE);
        aboutProfile.add(ProfileDetails.PROFILE_CREATION_DATE);

        /*
         * List of all elements with special handling
         */
        SPECIAL.add(ProfileDetails.BIRTHDATE);
        SPECIAL.add(ProfileDetails.PROFILE_LASTONLINE_DATE);
        SPECIAL.add(ProfileDetails.PROFILE_LASTUPDATE_DATE);
        SPECIAL.add(ProfileDetails.PROFILE_CREATION_DATE);
    }

    public boolean hasSpecialHandling(String key) {
        return SPECIAL.contains(key);
    }

    public String handle(String key, String value) {
        if (ProfileDetails.BIRTHDATE.equals(key)) {
            return handleDate(value);
        } else {
            // All others are dual (so far) - either a string or a date
            if (ProfileDetails.PROFILE_DATE_LAST24h.equals(value)) {
                return PurpleSkyApplication.get().getString(R.string.profile_date_last24h);
            } else {
                return handleDate(value);
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String handleDate(String value) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parsed = format.parse(value);
            if (parsed == null) {
                return null;
            }
            return SimpleDateFormat.getDateInstance().format(parsed);
        } catch (ParseException e) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Cannot parse date from JSON string: " + value);
            }
            return null;
        }
    }

}
