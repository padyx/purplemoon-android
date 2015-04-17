package ch.defiant.purplesky.util;

import android.content.Context;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.BasicUser;
import ch.defiant.purplesky.beans.DetailedUser;
import ch.defiant.purplesky.beans.MinimalUser;
import ch.defiant.purplesky.beans.PreviewUser;

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

    public static CharSequence createDescription(Context c, MinimalUser item) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(item.getAge()));

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
