package ch.defiant.purplesky.beans;

import java.io.Serializable;

import ch.defiant.purplesky.enums.OnlineStatus;

public class UpdateBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer favoritesCount;
    private Integer messagesCount;
    private Integer postItCount;
    private Integer visitCount;
    private String m_customOnlineStatus;
    private OnlineStatus m_predefinedOnlineStatus;
    private Exception exception;

    public Integer getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(Integer favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public Integer getMessagesCount() {
        return messagesCount;
    }

    public void setMessagesCount(Integer messagesCount) {
        this.messagesCount = messagesCount;
    }

    public Integer getPostItCount() {
        return postItCount;
    }

    public void setPostItCount(Integer postItCount) {
        this.postItCount = postItCount;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getCustomOnlineStatus() {
        return m_customOnlineStatus;
    }

    public void setCustomOnlineStatus(String customOnlineStatus) {
        m_customOnlineStatus = customOnlineStatus;
    }

    public OnlineStatus getPredefinedOnlineStatus() {
        return m_predefinedOnlineStatus;
    }

    public void setPredefinedOnlineStatus(OnlineStatus predefinedOnlineStatus) {
        m_predefinedOnlineStatus = predefinedOnlineStatus;
    }

}