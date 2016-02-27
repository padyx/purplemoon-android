package ch.defiant.purplesky.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Patrick Bänziger
 * @since 1.4
 */
public class PendingMessage implements Serializable, IPrivateMessage{

    private static final long serialVersionUID = 1;

    public Date getNextSendAttempt() {
        return m_nextSendAttempt;
    }

    public void setNextSendAttempt(Date nextSendAttempt) {
        m_nextSendAttempt = nextSendAttempt;
    }

    public enum Status {
        NEW(0),
        RETRY_NEEDED(1),
        FAILED(2);

        private final int m_id;

        Status(int id){
            m_id  = id;
        }

        public int getId(){
            return m_id;
        }

        public static Status getById(int id){
            for(Status s : values()){
                if(s.getId() == id){
                    return s;
                }
            }
            throw new IllegalArgumentException("Unknown status with id "+id);
        }
    }

    private Long m_id;
    private String m_messageText;
    private long m_recipientId;
    private Date m_timeSent;
    private Status m_status;
    private int m_retryCount;
    private Date m_nextSendAttempt;
    private long m_senderId;

    public Long getId() {
        return m_id;
    }

    public void setId(Long id) {
        m_id = id;
    }

    public String getMessageText() {
        return m_messageText;
    }

    public void setMessageText(String messageText) {
        m_messageText = messageText;
    }

    public long getRecipientId() {
        return m_recipientId;
    }

    @Override
    public long getSenderId() {
        return m_senderId;
    }

    public void setRecipientId(Long recipientId) {
        m_recipientId = recipientId;
    }

    public Date getTimeSent() {
        return m_timeSent;
    }

    public void setTimeSent(Date timeSent) {
        m_timeSent = timeSent;
    }

    public Status getStatus() {
        return m_status;
    }

    public void setStatus(Status status) {
        m_status = status;
    }

    public int getRetryCount() {
        return m_retryCount;
    }

    public void setRetryCount(int retryCount) {
        m_retryCount = retryCount;
    }
}
