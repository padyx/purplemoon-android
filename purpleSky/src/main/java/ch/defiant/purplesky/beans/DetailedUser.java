package ch.defiant.purplesky.beans;


import android.support.annotation.StringRes;

import java.util.Collections;
import java.util.Map;

import ch.defiant.purplesky.R;

public class DetailedUser extends PreviewUser {

    private static final long serialVersionUID = -3375886805715954342L;

    private Map<Integer, String> m_eventTmp = Collections.emptyMap();
    private RelationshipStatus m_relationshipStatus;


    /**
     * @return Map containing event id -> Eventvisiting preview text
     */
    public Map<Integer, String> getEventsTmp(){
        return m_eventTmp;
    }

    public void setEventTmp(Map<Integer, String> map){
        m_eventTmp = getEventsTmp();
    }

    public RelationshipStatus getRelationshipStatus(){
        return m_relationshipStatus;
    }

    public void setRelationshipStatus(RelationshipStatus status){
        m_relationshipStatus = status;
    }

    public enum RelationshipStatus {
        SINGLE(R.string.RelationshipSingle),
        LONGTERM_RELATIONSHIP(R.string.RelationshipLongTerm),
        ENGANGED(R.string.RelationshipEngaged),
        MARRIED(R.string.RelationshipMarried),
        OPEN_RELATIONSHIP(R.string.RelationshipOpen);

        private final int m_stringRes;

        RelationshipStatus(@StringRes int stringRes){
            m_stringRes = stringRes;
        }
        
        @StringRes
        public int getStringResource(){
            return m_stringRes;
        }
    }

}
