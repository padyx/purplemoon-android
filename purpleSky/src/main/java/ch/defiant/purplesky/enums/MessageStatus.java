package ch.defiant.purplesky.enums;

/**
 * @author Patrick Bänziger
 * @since 1.4
 */
public enum MessageStatus {

    NEW(0),
    RETRY_NEEDED(1),
    FAILED(2),
    SENT(3);

    private final int m_id;

    MessageStatus(int id){
        m_id  = id;
    }

    public int getId(){
        return m_id;
    }

    public static MessageStatus getById(int id){
        for(MessageStatus s : values()){
            if(s.getId() == id){
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status with id "+id);
    }

}
