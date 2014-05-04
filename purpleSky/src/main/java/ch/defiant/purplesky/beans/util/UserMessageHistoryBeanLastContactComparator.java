package ch.defiant.purplesky.beans.util;

import java.util.Comparator;

import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.util.CompareUtility;

/**
 * Comparator to order by last contact in descending order
 * 
 * @author Chakotay
 * 
 */
public class UserMessageHistoryBeanLastContactComparator implements Comparator<UserMessageHistoryBean> {

    @Override
    public int compare(UserMessageHistoryBean lhs, UserMessageHistoryBean rhs) {
        if (lhs == null && rhs == null) {
            return 0;
        } else if (lhs == null) {
            return 1;
        } else if (rhs == null) {
            return -1;
        }

        return -1 * CompareUtility.compare(lhs.getLastContact(), rhs.getLastContact());
    }

}
