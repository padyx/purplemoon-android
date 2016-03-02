package ch.defiant.purplesky.enums;

import android.content.res.Resources;
import android.util.TypedValue;

import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;

public enum UserPictureSize {
	MINUSCULE(PurplemoonAPIConstantsV1.USERPICTURE_URLPOSTFIX_MINUSCULE, 50), 
	TINY(PurplemoonAPIConstantsV1.USERPICTURE_URLPOSTFIX_TINY, 75),
	SMALL(PurplemoonAPIConstantsV1.USERPICTURE_URLPOSTFIX_SMALL, 100),
	MEDIUM(PurplemoonAPIConstantsV1.USERPICTURE_URLPOSTFIX_MEDIUM, 500),
	LARGE(PurplemoonAPIConstantsV1.USERPICTURE_URLPOSTFIX_LARGE, 700),
	LARGER(PurplemoonAPIConstantsV1.USERPICTURE_URLPOSTFIX_LARGER, 1000),
	VERYLARGE(PurplemoonAPIConstantsV1.USERPICTURE_URLPOSTFIX_VERYLARGE,1500),
	XLARGE(PurplemoonAPIConstantsV1.USERPICTURE_URLPOSTFIX_XLARGE, 2000);

	private String m_APIValue;
	private int m_size;

	UserPictureSize(String apiValue, int size) {
		m_APIValue = apiValue;
		m_size = size;
	}

	public String getAPIValue() {
		return m_APIValue;
	}

	public int getSize() {
		return m_size;
	}

	public static UserPictureSize getLargest() {
		return XLARGE;
	}

	public static UserPictureSize getPictureForDpi(int minDp, Resources r) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minDp, r.getDisplayMetrics());

		return getPictureSizeForPx(px);
	}

	public static UserPictureSize getPictureSizeForPx(float px) {
		for (UserPictureSize u : UserPictureSize.values()) {
			if (u.getSize() >= px)
				return u;
		}

		// Nothing is large enough... Return largest
		return getLargest();
	}

}
