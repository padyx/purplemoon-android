package ch.defiant.purplesky.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.defiant.purplesky.beans.UserMessageHistoryBean;

/**
 * Utility to reconcile online and cached version of message list.
 * The cached version always loses against an online version of the same bean.
 * @author Patrick Bänziger
 *
 */
public class ConversationReconciler {
    
    /**
     * Merges two lists of history beans. 
     * @param cached
     * @param online
     * @return Merged list. In no particular order.
     */
    public static List<UserMessageHistoryBean> reconcile(Collection<UserMessageHistoryBean> cached, Collection<UserMessageHistoryBean> online){
        Map<String, UserMessageHistoryBean> map = new HashMap<>();
        
        if(cached != null && !cached.isEmpty()){
            for (UserMessageHistoryBean b : cached) {
                String profileId = b.getProfileId();
                if(profileId == null && b.getUserBean() != null){
                    profileId = b.getUserBean().getUserId();
                }
                map.put(profileId, b);
            }
        }

        if(online != null && !online.isEmpty()){
            for (UserMessageHistoryBean b : online) {
                String profileId = b.getProfileId();
                if(profileId == null && b.getUserBean() != null){
                    profileId = b.getUserBean().getUserId();
                }
                map.put(profileId, b);
            }
        }

        
        return new ArrayList<>(map.values());
    }

    
    
}
