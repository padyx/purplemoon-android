package ch.defiant.purplesky.api.internal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.enums.Gender;
import ch.defiant.purplesky.enums.MessageRetrievalRestrictionType;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.enums.PurplemoonLocationType;
import ch.defiant.purplesky.enums.Sexuality;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.StringUtility;

/**
 * @author Patrick Bänziger
 * @since 1.0.1
 */
public final class APIUtility {

    public static String getErrorTypeString(JSONObject obj){
        if(obj != null) {
            return obj.optString(PurplemoonAPIConstantsV1.Errors.JSON_ERROR_TYPE);
        } else {
            return StringUtility.EMPTY_STRING;
        }
    }

    public static JSONObject getJSONUserSearchObject(UserSearchOptions options) throws PurpleSkyException {
        JSONObject object = new JSONObject();
        try {
            if (options.getAttractions() != null) {
                object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_GENDER_SEXUALITY, createGenderSexualityCombinations(options.getAttractions()));
            }

            if (options.getMinAge() != null) {
                object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_AGEMIN, options.getMinAge());
            }
            if (options.getMaxAge() != null) {
                object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_AGEMAX, options.getMaxAge());
            }
            if (options.getCountryId() != null) {
                object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_COUNTRY, options.getCountryId());
            }
            if (options.getLastOnline() != null) {
                object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_ONLINE_ONLY, translateLastOnline(options.getLastOnline()));
            }
            if (options.getMaxDistance() != null) {
                object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_DISTANCE_KM, options.getMaxDistance());
            }

            return object;
        } catch (JSONException e) {
            throw new PurpleSkyException("Internal error", e); // FIXME pbn error handling
        }
    }

    private static JSONArray createGenderSexualityCombinations(List<Pair<Gender, Sexuality>> attractions) {
        JSONArray array = new JSONArray();
        for (Pair<Gender, Sexuality> p : attractions) {
            String genderString = translateGender(p.getFirst());
            array.put(genderString + PurplemoonAPIConstantsV1.JSON_USERSEARCH_GENDER_SEXUALITY_SEPARATOR + translateSexuality(p.getSecond()));
        }
        return array;
    }

    public static Gender toGender(String apiValue){
        if(PurplemoonAPIConstantsV1.JSON_USER_GENDER_MALE.equals(apiValue)){
            return Gender.MALE;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_GENDER_FEMALE.equals(apiValue)){
            return Gender.FEMALE;
        } else {
            throw new IllegalArgumentException("No gender defined for "+apiValue);
        }
    }

    public static String translateSexuality(Sexuality s) {
        switch (s) {
            case HETEROSEXUAL:
                return PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE;
            case HOMOSEXUAL:
                return PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE;
            case BISEXUAL:
                return PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_BISEXUAL_VALUE;
            default:
                throw new IllegalArgumentException("No api value for " + s);
        }
    }

    public static Sexuality toSexuality(String sexuality){
        if(PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE.equals(sexuality)){
            return Sexuality.HETEROSEXUAL;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_BISEXUAL_VALUE.equals(sexuality)){
            return Sexuality.BISEXUAL;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE.equals(sexuality)){
            return Sexuality.HOMOSEXUAL;
        } else {
            throw new IllegalArgumentException("Unknown value for sexuality "+sexuality);
        }
    }

    public static String translateGender(Gender g) {
        switch (g) {
            case MALE:
                return PurplemoonAPIConstantsV1.JSON_USER_GENDER_MALE;
            case FEMALE:
                return PurplemoonAPIConstantsV1.JSON_USER_GENDER_FEMALE;
            default:
                throw new IllegalArgumentException("No api value for " + g);
        }
    }

    public static String translateLastOnline(UserSearchOptions.LastOnline lastOnline) {
        switch (lastOnline) {
            case NOW:
                return PurplemoonAPIConstantsV1.JSON_USERSEARCH_ONLINE_PARAM_NOW;
            case RECENTLY:
                return PurplemoonAPIConstantsV1.JSON_USERSEARCH_ONLINE_PARAM_PAST_HOUR;
            case PAST_DAY:
                return PurplemoonAPIConstantsV1.JSON_USERSEARCH_ONLINE_PARAM_PAST_DAY;
            case PAST_WEEK:
                return PurplemoonAPIConstantsV1.JSON_USERSEARCH_ONLINE_PARAM_PAST_WEEK;
            case PAST_MONTH:
                return PurplemoonAPIConstantsV1.JSON_USERSEARCH_ONLINE_PARAM_PAST_MONTH;
            default:
                throw new IllegalArgumentException("No api value for " + lastOnline);
        }

    }

    public static PurplemoonLocationType toLocationType(String type) {
        if (PurplemoonAPIConstantsV1.LOCATIONS_TYPE_CURRENT.equals(type)) {
            return PurplemoonLocationType.CURRENT;
        } else if (PurplemoonAPIConstantsV1.LOCATIONS_TYPE_HOME.equals(type)) {
            return PurplemoonLocationType.HOME;
        } else if (PurplemoonAPIConstantsV1.LOCATIONS_TYPE_HOME2.equals(type)) {
            return PurplemoonLocationType.HOME2;
        } else if (PurplemoonAPIConstantsV1.LOCATIONS_TYPE_WORK.equals(type)) {
            return PurplemoonLocationType.WORK;
        } else if (PurplemoonAPIConstantsV1.LOCATIONS_TYPE_WORK2.equals(type)) {
            return PurplemoonLocationType.WORK2;
        } else if (PurplemoonAPIConstantsV1.LOCATIONS_TYPE_WORK3.equals(type)) {
            return PurplemoonLocationType.WORK3;
        } else {
            throw new IllegalArgumentException("No type defined for " + type);
        }
    }

    public static String translateLocationType(PurplemoonLocationType type){
        switch(type){
            case CURRENT:
                return PurplemoonAPIConstantsV1.LOCATIONS_TYPE_CURRENT;
            case HOME:
                return PurplemoonAPIConstantsV1.LOCATIONS_TYPE_HOME;
            case HOME2:
                return PurplemoonAPIConstantsV1.LOCATIONS_TYPE_HOME2;
            case WORK:
                return PurplemoonAPIConstantsV1.LOCATIONS_TYPE_WORK;
            case WORK2:
                return PurplemoonAPIConstantsV1.LOCATIONS_TYPE_WORK2;
            case WORK3:
                return PurplemoonAPIConstantsV1.LOCATIONS_TYPE_WORK3;
            default:
                throw new IllegalArgumentException("No api value for "+type);
        }
    }

    public static String translateUnreadHandling(SendOptions.UnreadHandling handling){
        switch (handling){
            case SEND:
                return PurplemoonAPIConstantsV1.MESSAGE_SEND_UNREAD_HANDLING_SEND;
            case ABORT:
                return PurplemoonAPIConstantsV1.MESSAGE_SEND_UNREAD_HANDLING_ABORT;
            default:
                throw new IllegalArgumentException("No api value for "+handling);
        }
    }

    public static String translateMessageRetrievalRestrictionType(MessageRetrievalRestrictionType type){
        switch (type){
            case UNREAD_FIRST:
                return PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_UNREADFIRST;
            case UNOPENED_ONLY:
                return PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_UNREADONLY;
            case LAST_CONTACT:
                return PurplemoonAPIConstantsV1.MESSAGE_CHATLIST_ORDER_LASTCONTACT;
            default:
                throw new IllegalArgumentException("No api value for "+type);
        }
    }

    public static OnlineStatus toOnlineStatus(String value) {
        if (StringUtility.isNullOrEmpty(value)){
            return OnlineStatus.OFFLINE;
        } else if (PurplemoonAPIConstantsV1.ONLINESTATUS_ONLINE.equals(value)) {
            return OnlineStatus.ONLINE;
        } else if (PurplemoonAPIConstantsV1.ONLINESTATUS_INVISIBLE.equals(value)) {
            return OnlineStatus.INVISIBLE;
        } else if (PurplemoonAPIConstantsV1.ONLINESTATUS_AWAY.equals(value)) {
            return OnlineStatus.AWAY;
        } else if (PurplemoonAPIConstantsV1.ONLINESTATUS_BUSY.equals(value)) {
            return OnlineStatus.BUSY;
        } else {
            throw new IllegalArgumentException("No enum for online status: "+value);
        }
    }

    public static String translateOnlineStatus(OnlineStatus status) {
        switch (status) {
            case INVISIBLE:
                return PurplemoonAPIConstantsV1.ONLINESTATUS_INVISIBLE;
            case AWAY:
                return PurplemoonAPIConstantsV1.ONLINESTATUS_AWAY;
            case BUSY:
                return PurplemoonAPIConstantsV1.ONLINESTATUS_BUSY;
            case ONLINE:
                return PurplemoonAPIConstantsV1.ONLINESTATUS_ONLINE;
            case OFFLINE:
                throw new IllegalArgumentException("No value for offline");
            default:
                throw new IllegalArgumentException("No api value for "+status);
        }
    }
}
