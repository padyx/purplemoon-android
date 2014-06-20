package ch.defiant.purplesky.enums;

import android.content.Context;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;

public enum OnlineStatus {

	UNKNOWN("", R.string.Unknown,  R.color.onlinestatus_offline),
	INVISIBLE(PurplemoonAPIConstantsV1.ONLINESTATUS_INVISIBLE, R.string.Invisible, R.color.onlinestatus_invisible), 
	AWAY(PurplemoonAPIConstantsV1.ONLINESTATUS_AWAY,R.string.Away, R.color.onlinestatus_away), 
	BUSY(PurplemoonAPIConstantsV1.ONLINESTATUS_BUSY,R.string.Busy, R.color.onlinestatus_busy), 
	ONLINE(PurplemoonAPIConstantsV1.ONLINESTATUS_ONLINE,R.string.Online, R.color.onlinestatus_online),

	// This is a non-choosable state
	OFFLINE(null, R.string.Offline, R.color.onlinestatus_offline);

	OnlineStatus(String APIValue, int l10n, int color) {
		m_APIValue = APIValue;
		m_localizationString = l10n;
		m_color = color;
	}

	private final int m_localizationString;
	private final String m_APIValue;

	private final int m_color;

	public String getAPIValue() {
		return m_APIValue;
	}

	public String getLocalizedString(Context c) {
	    return c.getString(m_localizationString);
	}
	
	public int getColor(){
	    return m_color;
	}

	public static OnlineStatus getStatusByAPIValue(String value) {
		if (ONLINE.getAPIValue().equals(value)) {
			return ONLINE;
		} else if (INVISIBLE.getAPIValue().equals(value)) {
			return INVISIBLE;
		} else if (AWAY.getAPIValue().equals(value)) {
			return AWAY;
		} else if (BUSY.getAPIValue().equals(value)) {
			return BUSY;
		} else {
			return UNKNOWN;
		}
	}

	public static OnlineStatus[] getValidChoosableStates() {
		return new OnlineStatus[] { ONLINE, BUSY, AWAY, INVISIBLE };
	}

}
