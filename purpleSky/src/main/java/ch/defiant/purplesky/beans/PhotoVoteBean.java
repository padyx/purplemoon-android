package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Date;

import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1.PhotoVoteVerdict;

public class PhotoVoteBean implements Serializable {

    private static final long serialVersionUID = -3027811854202330779L;

    private long m_voteId;
    private String m_profileId;
    private PhotoVoteVerdict m_verdict;
    private Date m_timestamp;
    private String m_pictureUrlPrefix;
    private int m_maxHeight;
    private int m_maxWidth;
    private double m_posX;
    private double m_posY;
    private MinimalUser m_user;
    private PhotoVoteBean m_previousVote;
    private Integer m_votesRemaining;

    public long getVoteId() {
        return m_voteId;
    }

    public void setVoteId(long l) {
        m_voteId = l;
    }

    public String getProfileId() {
        return m_profileId;
    }

    public void setProfileId(String profileId) {
        m_profileId = profileId;
    }

    public PhotoVoteVerdict getVerdict() {
        return m_verdict;
    }

    public void setVerdict(PhotoVoteVerdict verdict) {
        m_verdict = verdict;
    }

    public Date getTimestamp() {
        return m_timestamp;
    }

    public void setTimestamp(Date timestamp) {
        m_timestamp = timestamp;
    }

    public String getPictureUrlPrefix() {
        return m_pictureUrlPrefix;
    }

    public void setPictureUrlPrefix(String pictureUrlPrefix) {
        m_pictureUrlPrefix = pictureUrlPrefix;
    }

    public int getMaxHeight() {
        return m_maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.m_maxHeight = maxHeight;
    }

    public int getMaxWidth() {
        return m_maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.m_maxWidth = maxWidth;
    }

    public double getPosX() {
        return m_posX;
    }

    public void setPosX(double posX) {
        this.m_posX = posX;
    }

    public double getPosY() {
        return m_posY;
    }

    public void setPosY(double posY) {
        this.m_posY = posY;
    }

    /**
     * @return User bean, if one was set.
     */
    public MinimalUser getUser() {
        return m_user;
    }

    public void setUser(MinimalUser user) {
        m_user = user;
    }

    /**
     * @return Vote that preceeded this one
     */
    public PhotoVoteBean getPreviousVote() {
        return m_previousVote;
    }

    public void setPreviousVote(PhotoVoteBean previousVote) {
        m_previousVote = previousVote;
    }

    @Override
    public String toString() {
        return "PhotoVoteBean [m_voteId=" + m_voteId + ", m_profileId=" + m_profileId + ", m_verdict=" + m_verdict + ", m_timestamp=" + m_timestamp
                + ", m_pictureUrlPrefix=" + m_pictureUrlPrefix + ", m_maxHeight=" + m_maxHeight + ", m_maxWidth=" + m_maxWidth + ", m_posX=" + m_posX
                + ", m_posY=" + m_posY + ", m_user=" + m_user + ", m_previousVote=" + m_previousVote + "]";
    }

    public Integer getVotesRemaining() {
        return m_votesRemaining;
    }

    public void setVotesRemaining(Integer votesRemaining) {
        m_votesRemaining = votesRemaining;
    }

}
