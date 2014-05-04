package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.net.URL;

import ch.defiant.purplesky.enums.Gender;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.enums.ProfileStatus;
import ch.defiant.purplesky.enums.Sexuality;

public class MinimalUser implements Serializable {

	private static final long serialVersionUID = 3534554261705188886L;
	private static final long EXPIRATION = 12 * 60 * 60 * 1000;

	private long m_retrievalTime;

	private String m_userId; // Primary identifier
	private String m_username;
	private Integer m_age;
	private Gender m_gender;
	private Sexuality m_sexuality;
	private boolean m_ageVerified;
	private URL m_profilePictureURLDirectory;
	private ProfileStatus m_profileStatus;
    private String m_onlineStatusText;
    private OnlineStatus m_onlineStatus;

	public String getUserId() {
		return m_userId;
	}

	public void setUserId(String userId) {
		m_userId = userId;
	}

	public String getUsername() {
		return m_username;
	}

	public void setUsername(String username) {
		m_username = username;
	}

	public Integer getAge() {
		return m_age;
	}

	public void setAge(Integer age) {
		m_age = age;
	}

	public Gender getGender() {
		return m_gender;
	}

	public void setGender(Gender gender) {
		m_gender = gender;
	}

	public Sexuality getSexuality() {
		return m_sexuality;
	}

	public void setSexuality(Sexuality sexuality) {
		m_sexuality = sexuality;
	}

	public boolean isAgeVerified() {
		return m_ageVerified;
	}

	public void setAgeVerified(boolean isAgeVerified) {
		m_ageVerified = isAgeVerified;
	}

	public URL getProfilePictureURLDirectory() {
		return m_profilePictureURLDirectory;
	}

	public void setProfilePictureURLDirectory(URL profilePictureURLDirectory) {
		m_profilePictureURLDirectory = profilePictureURLDirectory;
	}

	public ProfileStatus getProfileStatus() {
		return m_profileStatus;
	}

	public void setProfileStatus(ProfileStatus profileStatus) {
		m_profileStatus = profileStatus;
	}

	public long getRetrievalTime() {
		return m_retrievalTime;
	}

	public void setRetrievalTime(long retrievalTime) {
		m_retrievalTime = retrievalTime;
	}

	public long getExpiryDuration() {
		return EXPIRATION;
	}

    public String getOnlineStatusText() {
    	return m_onlineStatusText;
    }

    public void setOnlineStatusText(String onlineStatusText) {
    	m_onlineStatusText = onlineStatusText;
    }

    public OnlineStatus getOnlineStatus() {
    	return m_onlineStatus;
    }

    public void setOnlineStatus(OnlineStatus onlineStatus) {
    	m_onlineStatus = onlineStatus;
    }

}
