package ch.defiant.purplesky.util;

import java.util.Collection;
import java.util.List;

public class CollectionUtil {

    /**
     * Returns the last element of a list.
     *
     * @param list
     * @return Last element or <tt>null</tt>
     */
    public static <T> T lastElement(List<T> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    /* Returns the first element of a collection.
    * @param collection
    * @return Last element or <tt>null</tt>
    */
    public static <T> T firstElement(Collection<T> collection) {
        if(collection == null){
            return null;
        } else if (collection.isEmpty()) {
            return null;
        } else {
            return collection.iterator().next();
        }
    }

    /**
     * Returns the size of the collection or zero, if null.
     * @param c
     * @return size of collection, or zero if <tt>null</tt>
     */
    public static int safeSize(Collection<?> c){
        if(c == null){
            return 0;
        } else {
            return c.size();
        }
    }
}
