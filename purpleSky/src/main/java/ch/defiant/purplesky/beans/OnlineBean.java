package ch.defiant.purplesky.beans;

import java.util.Date;

import ch.defiant.purplesky.enums.OnlineStatus;

/**
 * A bean class to represent an online favorite
 */
public class OnlineBean {

	private String m_profileId;
	private MinimalUser m_userBean;
	private OnlineStatus m_onlineStatus;
	private String m_onlineStatusText;
	private Date m_onlineSince;

	public String getProfileId() {
		return m_profileId;
	}

	public void setProfileId(String profileId) {
		m_profileId = profileId;
	}

	public OnlineStatus getOnlineStatus() {
		return m_onlineStatus;
	}

	public void setOnlineStatus(OnlineStatus onlineStatus) {
		m_onlineStatus = onlineStatus;
	}

	public String getOnlineStatusText() {
		return m_onlineStatusText;
	}

	public void setOnlineStatusText(String onlineStatusText) {
		m_onlineStatusText = onlineStatusText;
	}

	public Date getOnlineSince() {
		return m_onlineSince;
	}

	public void setOnlineSince(Date onlineSince) {
		m_onlineSince = onlineSince;
	}

	public MinimalUser getUserBean() {
		return m_userBean;
	}

	public void setUserBean(MinimalUser userBean) {
		m_userBean = userBean;
	}

}
