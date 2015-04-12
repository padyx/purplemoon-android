package ch.defiant.purplesky.constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1.ProfileDetails;

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

    private final Set<String> SPECIAL = new HashSet<>();

    private ProfileListMap() {
        GROUPS = new ArrayList<>();
        GROUP_LIST_APIKEYS = new ArrayList<>();

        // Groups
        GROUPS.add(R.string.profileGroup_Occupation);

        ArrayList<String> occupationlist = new ArrayList<>();
        GROUP_LIST_APIKEYS.add(occupationlist);
        occupationlist.add(ProfileDetails.OCCUPATION_LIST);

    }

    public boolean hasSpecialHandling(String key) {
        return SPECIAL.contains(key);
    }

    public String handle(String key, String value) {
        return "";
    }

}
