package ch.defiant.purplesky.enums;

import android.content.Context;

import ch.defiant.purplesky.R;

public enum OnlineStatus {

	UNKNOWN(R.string.Unknown,  R.color.onlinestatus_offline),
	INVISIBLE(R.string.Invisible, R.color.onlinestatus_invisible),
	AWAY(R.string.Away, R.color.onlinestatus_away),
	BUSY(R.string.Busy, R.color.onlinestatus_busy),
	ONLINE(R.string.Online, R.color.onlinestatus_online),

	// This is a non-choosable state
	OFFLINE(R.string.Offline, R.color.onlinestatus_offline);

	OnlineStatus(int l10n, int color) {
		m_localizationString = l10n;
		m_color = color;
	}

	private final int m_localizationString;

	private final int m_color;

	public String getLocalizedString(Context c) {
	    return c.getString(m_localizationString);
	}
	
	public int getColor(){
	    return m_color;
	}

	public static OnlineStatus[] getValidChoosableStates() {
		return new OnlineStatus[] { ONLINE, BUSY, AWAY, INVISIBLE };
	}

}
