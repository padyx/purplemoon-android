package ch.defiant.purplesky.beans;


import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import ch.defiant.purplesky.enums.profile.ChatFrequency;
import ch.defiant.purplesky.enums.profile.MessengerType;
import ch.defiant.purplesky.enums.profile.RelationshipStatus;
import ch.defiant.purplesky.enums.profile.TargetGender;

public class DetailedUser extends PreviewUser {

    private static final long serialVersionUID = -3375886805715954342L;

    private Map<Integer, String> m_eventTmp = Collections.emptyMap();
    private RelationshipInformation m_relationshipInformation;
    private FriendshipInformation m_friendshipInformation;
    private Date m_createDate;
    private Date m_updateDate;
    private Date m_lastOnlineDate;
    private String m_nicknames;
    private Date m_birthDate;
    private String m_emailAddress;
    private String m_homepage;
    private String m_chatNames;
    private String m_whichChats;
    private ChatFrequency m_chatFrequency;
    private Collection<MessengerBean> m_messengers;

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

    @Nullable
    public Date getCreateDate() {
        return m_createDate;
    }

    public void setCreateDate(Date createDate) {
        m_createDate = createDate;
    }

    @Nullable
    public Date getUpdateDate() {
        return m_updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        m_updateDate = updateDate;
    }

    @Nullable
    public Date getLastOnlineDate() {
        return m_lastOnlineDate;
    }

    public void setLastOnlineDate(Date lastOnlineDate) {
        m_lastOnlineDate = lastOnlineDate;
    }

    public String getNicknames() {
        return m_nicknames;
    }

    public void setNicknames(String nicknames) {
        m_nicknames = nicknames;
    }

    public Date getBirthDate() {
        return m_birthDate;
    }

    public void setBirthDate(Date birthDate) {
        m_birthDate = birthDate;
    }

    public String getEmailAddress() {
        return m_emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        m_emailAddress = emailAddress;
    }

    public String getHomepage() {
        return m_homepage;
    }

    public void setHomepage(String homepage) {
        m_homepage = homepage;
    }

    public String getChatNames() {
        return m_chatNames;
    }

    public void setChatNames(String chatNames) {
        m_chatNames = chatNames;
    }

    public String getWhichChats() {
        return m_whichChats;
    }

    public void setWhichChats(String whichChats) {
        m_whichChats = whichChats;
    }

    public ChatFrequency getChatFrequency() {
        return m_chatFrequency;
    }

    public void setChatFrequency(ChatFrequency chatFrequency) {
        m_chatFrequency = chatFrequency;
    }

    public Collection<MessengerBean> getMessengers() {
        return m_messengers;
    }

    public void setMessengers(Collection<MessengerBean> messengers) {
        m_messengers = messengers;
    }

    public static abstract class AbstractRelation implements Serializable {
        private static final long serialVersionUID = 1L;

        private String m_text;
        private Integer m_desiredAgeFrom;
        private Integer m_desiredAgeTill;
        private Integer m_maximumDistance;

        public String getText() {
            return m_text;
        }

        public AbstractRelation setText(String text) {
            this.m_text = text;
            return this;
        }

        public Integer getDesiredAgeFrom() {
            return m_desiredAgeFrom;
        }

        public AbstractRelation setDesiredAgeFrom(Integer desiredAgeFrom) {
            this.m_desiredAgeFrom = desiredAgeFrom;
            return this;
        }

        public Integer getDesiredAgeTill() {
            return m_desiredAgeTill;
        }

        public AbstractRelation setDesiredAgeTill(Integer desiredAgeTill) {
            this.m_desiredAgeTill = desiredAgeTill;
            return this;
        }

        public Integer getMaximumDistance() {
            return m_maximumDistance;
        }

        public AbstractRelation setMaximumDistance(Integer maximumDistance) {
            this.m_maximumDistance = maximumDistance;
            return this;
        }
    }


    public static class FriendshipInformation extends AbstractRelation {
        private static final long serialVersionUID = 1L;

        private TargetGender m_targetGender;

        public TargetGender getTargetGender() {
            return m_targetGender;
        }

        public FriendshipInformation setTargetGender(TargetGender targetGender) {
            m_targetGender = targetGender;
            return this;
        }
    }

    public static class RelationshipInformation extends AbstractRelation {
        private static final long serialVersionUID = 1L;

        private RelationshipStatus m_relationshipStatus;


        public RelationshipStatus getRelationshipStatus() {
            return m_relationshipStatus;
        }

        public RelationshipInformation setRelationshipStatus(RelationshipStatus relationshipStatus) {
            this.m_relationshipStatus = relationshipStatus;
            return this;
        }
    }

    public static class MessengerBean implements Serializable {

        private static final long serialVersionUID = 1L;
        private final MessengerType m_type;
        private final String m_username;

        public MessengerBean(MessengerType type, String username){
            m_type = type;
            m_username = username;
        }

        public MessengerType getType() {
            return m_type;
        }

        public String getUsername() {
            return m_username;
        }
    }

}
