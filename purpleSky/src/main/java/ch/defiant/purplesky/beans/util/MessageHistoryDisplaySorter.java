package ch.defiant.purplesky.beans.util;

import java.util.Comparator;

import ch.defiant.purplesky.beans.UserMessageHistoryBean;
import ch.defiant.purplesky.util.CompareUtility;

/**
 * Sorts the beans for display to the user. Sort order: First all unread conversations, descending by last contact. Then
 * the remaining conversations descending by last contact.
 * 
 * @author Patrick Bänziger
 * 
 */
public class MessageHistoryDisplaySorter implements Comparator<UserMessageHistoryBean> {

    @Override
    public int compare(UserMessageHistoryBean lhs, UserMessageHistoryBean rhs) {
        if(lhs == rhs){
            return 0;
        }
        else if (lhs == null){
            return 1;
        } else if (rhs == null){
            return -1;
        }
        
        String profileIdLHS = lhs.getProfileId();
        if(profileIdLHS == null && lhs.getUserBean() != null){
            profileIdLHS = lhs.getUserBean().getUserId();
        }
        String profileIdRHS= rhs.getProfileId();
        if(profileIdRHS == null && rhs.getUserBean() != null){
            profileIdRHS = rhs.getUserBean().getUserId();
        }
        if (CompareUtility.equals(profileIdLHS, profileIdRHS)){
            return 0;
        } 
        
        // Unread has precedence
        if(lhs.getUnopenedMessageCount() > 0){
            if(rhs.getUnopenedMessageCount() == 0){
                return -1;
            }
        } else if (rhs.getUnopenedMessageCount() > 0){
            if(lhs.getUnopenedMessageCount() == 0){
                return 1;
            }
        }
        
        // Both have unread, or both have no unread here. Use contact date, but in reverse order
        return -1 * CompareUtility.compare(lhs.getLastContact(), rhs.getLastContact());
    }
    
}