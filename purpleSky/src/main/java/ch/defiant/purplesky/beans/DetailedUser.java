package ch.defiant.purplesky.beans;


import java.util.Collections;
import java.util.Map;

public class DetailedUser extends PreviewUser {

    private static final long serialVersionUID = -3375886805715954342L;

    private Map<Integer, String> m_eventTmp = Collections.emptyMap();

    /**
     * @return Map containing event id -> Eventvisiting preview text
     */
    public Map<Integer, String> getEventsTmp(){
        return m_eventTmp;
    }

    public void setEventTmp(Map<Integer, String> map){
        m_eventTmp = getEventsTmp();
    }
}
