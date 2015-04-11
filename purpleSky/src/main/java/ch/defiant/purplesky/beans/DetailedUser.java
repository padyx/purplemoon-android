package ch.defiant.purplesky.beans;


import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import ch.defiant.purplesky.enums.profile.RelationshipStatus;

public class DetailedUser extends PreviewUser {

    private static final long serialVersionUID = -3375886805715954342L;

    private Map<Integer, String> m_eventTmp = Collections.emptyMap();
    private RelationshipInformation m_relationshipInformation;
    private FriendshipInformation m_friendshipInformation;

    /**
     * @return Map containing event id -> Eventvisiting preview text
     */
    public Map<Integer, String> getEventsTmp(){
        return m_eventTmp;
    }

    public void setEventTmp(Map<Integer, String> map){
        m_eventTmp = getEventsTmp();
    }


    public RelationshipInformation getRelationshipInformation(){
        return m_relationshipInformation;
    }

    public void setRelationShipInformation(RelationshipInformation info){
        m_relationshipInformation = info;
    }


    public FriendshipInformation getFriendshipInformation(){
        return m_friendshipInformation;
    }

    public void setFriendshipInformation(FriendshipInformation info){
        m_friendshipInformation = info;
    }

    public static class FriendshipInformation implements Serializable {
        private static final long serialVersionUID = 1L;

        private String m_text;
        private Integer m_desiredAgeFrom;
        private Integer m_desiredAgeTill;
        private Integer m_maximumDistance;

        public String getText() {
            return m_text;
        }

        public FriendshipInformation setText(String text) {
            this.m_text = text;
            return this;
        }

        public Integer getDesiredAgeFrom() {
            return m_desiredAgeFrom;
        }

        public FriendshipInformation setDesiredAgeFrom(Integer desiredAgeFrom) {
            this.m_desiredAgeFrom = desiredAgeFrom;
            return this;
        }

        public Integer getDesiredAgeTill() {
            return m_desiredAgeTill;
        }

        public FriendshipInformation setDesiredAgeTill(Integer desiredAgeTill) {
            this.m_desiredAgeTill = desiredAgeTill;
            return this;
        }

        public Integer getMaximumDistance() {
            return m_maximumDistance;
        }

        public FriendshipInformation setMaximumDistance(Integer maximumDistance) {
            this.m_maximumDistance = maximumDistance;
            return this;
        }
    }

    public static class RelationshipInformation extends FriendshipInformation {
        private static final long serialVersionUID = 2L;

        private RelationshipStatus m_relationshipStatus;


        public RelationshipStatus getRelationshipStatus() {
            return m_relationshipStatus;
        }

        public RelationshipInformation setRelationshipStatus(RelationshipStatus relationshipStatus) {
            this.m_relationshipStatus = relationshipStatus;
            return this;
        }

    }

}
