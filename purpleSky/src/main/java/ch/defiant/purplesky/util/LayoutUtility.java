package ch.defiant.purplesky.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public final class LayoutUtility {

	public static int dpToPx(Resources r, int dp) {
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
	}
	
	public static int getMaximumDisplaySidePixels(Resources r){
	    DisplayMetrics metrics = r.getDisplayMetrics();
	    return Math.max(metrics.heightPixels, metrics.widthPixels);
	}

}
