package ch.defiant.purplesky.events;

/**
 * @author Patrick Bänziger
 */
public class MessageDeliveryFailedEvent {

    private final long m_pendingMessageId;

    public MessageDeliveryFailedEvent(long pendingMessageId){
        m_pendingMessageId = pendingMessageId;
    }

    public long getPendingMessageId() {
        return m_pendingMessageId;
    }
}
