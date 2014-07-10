package ch.defiant.purplesky.util;

import android.content.Context;

import java.util.Map;
import java.util.Map.Entry;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.BasicUser;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PreviewUser;
import ch.defiant.purplesky.beans.ProfileTriplet;

public final class UserUtility {

    private static final String DESCRIPTION_DIVIDER = ", ";

    private UserUtility() {

    }

    /**
     * Creates an empty user bean of the requested type.
     * 
     * @param clazz
     *            Which user it shall be.
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends MinimalUser> T instantiateUser(Class<T> clazz) {
        if (clazz == MinimalUser.class) {
            return (T) new MinimalUser();
        } else if (clazz == BasicUser.class) {
            return (T) new BasicUser();
        } else if (clazz == PreviewUser.class) {
            return (T) new PreviewUser();
        } else if (clazz == DetailedUser.class) {
            return (T) new DetailedUser();
        } else
            throw new IllegalArgumentException("Unknown user type to instantiate!");
    }

    /**
     * Searches (recursively, by depth-first-search) the first profile triplet with a matching api key.
     * 
     * @param key
     *            Key to identify the right profiletriplet
     * @param d
     *            user bean
     * @return The profiletriplet if found, or null.
     */
    public static ProfileTriplet getDetail(String key, DetailedUser d) {
        if (d == null || d.getProfileDetails() == null)
            return null;

        Map<String, ProfileTriplet> details = d.getProfileDetails();
        ProfileTriplet trip = details.get(key);
        if (trip != null) { // Already found?
            return trip;
        } else {
            // Search recursive
            for (ProfileTriplet it : details.values()) {
                ProfileTriplet detailRecursive = getDetailRecursive(key, it);
                if (detailRecursive != null) {
                    return detailRecursive;
                }
            }
        }

        return null;
    }

    /**
     * Searches, recursively (by depth-first-search) for a profile triplet, starting at a fix point.
     * 
     * @param key
     *            Key to identify the right profiletriplet
     * @param trip
     * @return The profiletriplet if found, or null.
     */
    public static ProfileTriplet getDetailRecursive(String key, ProfileTriplet trip) {
        if (trip == null)
            return null;

        // Is it this one we searched for?
        if (CompareUtility.equals(key, trip.getAPIKey())) {
            return trip;
        }

        if (trip.getChildren() != null) {
            // Search children
            for (ProfileTriplet child : trip.getChildren().values()) {
                if (CompareUtility.equals(child.getAPIKey(), key)) {
                    return child;
                } else {
                    ProfileTriplet t = getDetailRecursive(key, child);
                    if (t != null) {
                        return t;
                    }
                }
            }
        }
        if (trip.getList() != null) {
            // Search list
            for (Map<String, ProfileTriplet> map : trip.getList()) {
                if (map != null) {
                    for (Entry<String, ProfileTriplet> subtrip : map.entrySet()) {
                        if (CompareUtility.equals(key, subtrip)) {
                            return subtrip.getValue();
                        } else {
                            ProfileTriplet recursiveRes = getDetailRecursive(key, subtrip.getValue());
                            if (recursiveRes != null) {
                                return recursiveRes;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static CharSequence createDescription(Context c, MinimalUser item) {
        StringBuilder sb = new StringBuilder();
        if (item.getAge() != null) {
            sb.append(String.valueOf(item.getAge()));
        }
        if (item.getGender() != null) {
            if (sb.length() > 0) {
                sb.append(DESCRIPTION_DIVIDER);
            }
            sb.append(item.getGender().getLocalizedString());
        }
        if (item.getSexuality() != null) {
            if (sb.length() > 0) {
                sb.append(DESCRIPTION_DIVIDER);
            }
            sb.append(item.getSexuality().getLocalizedString(c.getResources(), item.getGender()));
        }
        if (item instanceof PreviewUser) {
            PreviewUser p = (PreviewUser) item;
            if (NVLUtility.nvl(p.isFriend(), false)) {
                if (sb.length() > 0) {
                    sb.append(DESCRIPTION_DIVIDER);
                }
                sb.append(c.getString(R.string.FriendOfYou));
                ;
            } else if (NVLUtility.nvl(p.isKnown(), false)) {
                if (sb.length() > 0) {
                    sb.append(DESCRIPTION_DIVIDER);
                }
                sb.append(c.getString(R.string.YouKnowThisUser));
            }
        }
        return sb;
    }
}
