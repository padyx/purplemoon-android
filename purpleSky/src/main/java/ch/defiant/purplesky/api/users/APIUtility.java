package ch.defiant.purplesky.api.users;

import android.support.annotation.Nullable;

import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.beans.DetailedUser;

/**
 * @author  Patrick Baenziger
 */
class APIUtility {

    @Nullable
    static DetailedUser.RelationshipStatus translateToRelationshipStatus(String jsonString){
        if(jsonString == null || jsonString.isEmpty()){
            return null;
        }
        if(PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_SINGLE.equals(jsonString)){
            return DetailedUser.RelationshipStatus.SINGLE;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_LONGTERM.equals(jsonString)){
            return DetailedUser.RelationshipStatus.LONGTERM_RELATIONSHIP;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_ENGANGED.equals(jsonString)){
            return DetailedUser.RelationshipStatus.ENGANGED;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_MARRIED.equals(jsonString)){
            return DetailedUser.RelationshipStatus.MARRIED;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_OPEN.equals(jsonString)){
            return DetailedUser.RelationshipStatus.OPEN_RELATIONSHIP;
        } else {
            throw new IllegalArgumentException("Unknown value for relationship status "+jsonString);
        }
    }

}
