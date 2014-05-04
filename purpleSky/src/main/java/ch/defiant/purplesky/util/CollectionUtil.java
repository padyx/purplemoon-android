package ch.defiant.purplesky.util;

import java.util.List;

public class CollectionUtil {

    /**
     * Returns the last element of a list.
     * @param collection
     * @return Last element or <tt>null</tt>
     */
    public static <T> T lastElement(List<T> collection ){
        if(collection.isEmpty()){
            return null;
        } else {
            return collection.get(collection.size()-1);
        }
    }
    
}
