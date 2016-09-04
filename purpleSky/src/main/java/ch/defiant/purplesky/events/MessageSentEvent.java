package ch.defiant.purplesky.events;

/**
 * @author Patrick Bänziger
 */
public class MessageSentEvent {

    private final long m_pendingMessageId;
    private final long m_deliveredMessageId;

    public MessageSentEvent(long pendingMessageId, long deliveredMessageId){
        m_pendingMessageId = pendingMessageId;
        m_deliveredMessageId = deliveredMessageId;
    }

    public long getPendingMessageId() {
        return m_pendingMessageId;
    }

    public long getDeliveredMessageId() {
        return m_deliveredMessageId;
    }
}
