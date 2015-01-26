package ch.defiant.purplesky.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public final class LayoutUtility {

    public static int dpToPx(Resources r, int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static int getMaximumDisplaySidePixels(Resources r){
        DisplayMetrics metrics = r.getDisplayMetrics();
        return Math.max(metrics.heightPixels, metrics.widthPixels);
    }

    public static void setEnabledRecursive(ViewGroup group, boolean enabled){
        if(group == null){
            return;
        }
        final int size = group.getChildCount();
        for (int i = 0; i < size; i++){
            View child = group.getChildAt(i);
            if(child != null) {
                child.setEnabled(enabled);
                if (child instanceof ViewGroup) {
                    setEnabledRecursive((ViewGroup) child, enabled);
                }
            }
        }
    }

}
