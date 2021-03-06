package ch.defiant.purplesky.api.internal;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.core.SendOptions;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.enums.MessageRetrievalRestrictionType;
import ch.defiant.purplesky.enums.OnlineStatus;
import ch.defiant.purplesky.enums.PurplemoonLocationType;
import ch.defiant.purplesky.enums.profile.ChatFrequency;
import ch.defiant.purplesky.enums.profile.DrinkerFrequency;
import ch.defiant.purplesky.enums.profile.EyeColor;
import ch.defiant.purplesky.enums.profile.FacialHair;
import ch.defiant.purplesky.enums.profile.Gender;
import ch.defiant.purplesky.enums.profile.HairColor;
import ch.defiant.purplesky.enums.profile.HairLength;
import ch.defiant.purplesky.enums.profile.HasKids;
import ch.defiant.purplesky.enums.profile.MessengerType;
import ch.defiant.purplesky.enums.profile.OccupationType;
import ch.defiant.purplesky.enums.profile.Physique;
import ch.defiant.purplesky.enums.profile.Politics;
import ch.defiant.purplesky.enums.profile.RelationshipStatus;
import ch.defiant.purplesky.enums.profile.Religion;
import ch.defiant.purplesky.enums.profile.Sexuality;
import ch.defiant.purplesky.enums.profile.SmokerFrequency;
import ch.defiant.purplesky.enums.profile.TargetGender;
import ch.defiant.purplesky.enums.profile.Vegetarian;
import ch.defiant.purplesky.enums.profile.WantsKids;
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

    @Nullable
    public static RelationshipStatus translateToRelationshipStatus(@Nullable String jsonString){
        if(jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_SINGLE.equals(jsonString)){
            return RelationshipStatus.SINGLE;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_LONGTERM.equals(jsonString)){
            return RelationshipStatus.LONGTERM_RELATIONSHIP;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_ENGANGED.equals(jsonString)){
            return RelationshipStatus.ENGANGED;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_MARRIED.equals(jsonString)){
            return RelationshipStatus.MARRIED;
        } else if (PurplemoonAPIConstantsV1.JSON_USER_RELATIONSHIP_STATUS_OPEN.equals(jsonString)){
            return RelationshipStatus.OPEN_RELATIONSHIP;
        } else {
            throw new IllegalArgumentException("Unknown value for relationship status "+jsonString);
        }
    }

    @Nullable
    public static TargetGender translateToTargetGender(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.TARGET_FRIENDS_GENDER_MENONLY .equals(jsonString)){
            return TargetGender.MEN_ONLY;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.TARGET_FRIENDS_GENDER_MENPREFERRED.equals(jsonString)){
            return TargetGender.MEN_PREFERRED;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.TARGET_FRIENDS_GENDER_WOMENPREFERRED.equals(jsonString)){
            return TargetGender.WOMEN_PREFERRED;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.TARGET_FRIENDS_GENDER_WOMENONLY.equals(jsonString)){
            return TargetGender.WOMEN_ONLY;
        } else {
            throw new IllegalArgumentException("Unknown value for target gender "+jsonString);
        }
    }

    @Nullable
    public static EyeColor translateToEyeColor(@Nullable String jsonString){
        if(jsonString == null || jsonString.isEmpty()){
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_LIGHTBROWN.equals(jsonString)){
            return EyeColor.LIGHTBROWN;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_DARKBROWN.equals(jsonString)){
            return EyeColor.DARKBROWN;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_BROWN.equals(jsonString)){
            return EyeColor.BROWN;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_LIGHTBLUE.equals(jsonString)){
            return EyeColor.LIGHTBLUE;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_DARKBLUE.equals(jsonString)){
            return EyeColor.DARKBLUE;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_BLUE.equals(jsonString)){
            return EyeColor.BLUE;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_BLACK.equals(jsonString)){
            return EyeColor.BLACK;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_GREEN.equals(jsonString)){
            return EyeColor.GREEN;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_BLUEGREY.equals(jsonString)){
            return EyeColor.BLUEGREY;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_BLUEGREEN.equals(jsonString)){
            return EyeColor.BLUEGREEN;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_GREENBROWN.equals(jsonString)){
            return EyeColor.GREENBROWN;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_GREENGREY.equals(jsonString)){
            return EyeColor.GREENGREY;
        }else if (PurplemoonAPIConstantsV1.ProfileDetails.EYE_COLOR_GREY.equals(jsonString)){
            return EyeColor.GREY;
        } else {
            throw new IllegalArgumentException("Unknown value for eye color "+jsonString);
        }
    }

    @Nullable
    public static Physique translateToPhysique(@Nullable String jsonString){
        if(jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.PHYSIQUE_ATHLETIC.equals(jsonString)){
            return Physique.ATHLETIC;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.PHYSIQUE_BODYBUILDER.equals(jsonString)){
            return Physique.BODYBUILDER;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.PHYSIQUE_CHUBBY.equals(jsonString)){
            return Physique.CHUBBY;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.PHYSIQUE_LITTLE_TUMMY.equals(jsonString)){
            return Physique.LITTLE_TUMMY;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.PHYSIQUE_NORMAL.equals(jsonString)){
            return Physique.NORMAL;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.PHYSIQUE_SLIM.equals(jsonString)){
            return Physique.SLIM;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.PHYSIQUE_STURDY.equals(jsonString)){
            return Physique.STURDY;
        } else {
            throw new IllegalArgumentException("Unknown value for physique " + jsonString);
        }
    }

    @Nullable
    public static FacialHair translateToFacialHair(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.FACIAL_HAIR_NONE.equals(jsonString)){
            return FacialHair.NONE;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.FACIAL_HAIR_SHAVED.equals(jsonString)) {
            return FacialHair.SHAVED;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.FACIAL_HAIR_THREEDAY.equals(jsonString)) {
            return FacialHair.THREE_DAY;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.FACIAL_HAIR_MUSTACHE.equals(jsonString)) {
            return FacialHair.MUSTACHE;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.FACIAL_HAIR_PETITGOATEE.equals(jsonString)) {
            return FacialHair.PETIT_GOATEE;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.FACIAL_HAIR_GOATEE.equals(jsonString)) {
            return FacialHair.GOATEE;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.FACIAL_HAIR_MUTTONCHOPS.equals(jsonString)) {
            return FacialHair.MUTTON_CHOPS;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.FACIAL_HAIR_FULLBEARD.equals(jsonString)) {
            return FacialHair.FULL_BEARD;
        } else {
            throw new IllegalArgumentException("Unknown value for facial hair " + jsonString);
        }
    }

    @Nullable
    public static HairLength translateToHairLength(@Nullable String jsonString){
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_LENGTH_BALD.equals(jsonString)){
            return HairLength.BALD;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.HAIR_LENGTH_SHORT.equals(jsonString)) {
            return HairLength.SHORT;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.HAIR_LENGTH_MEDIUM.equals(jsonString)) {
            return HairLength.MEDIUM;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.HAIR_LENGTH_LONG.equals(jsonString)) {
            return HairLength.LONG;
        } else {
            throw new IllegalArgumentException("Unknown value for hair length " + jsonString);
        }
    }

    @Nullable
    public static HairColor translateToHairColor(@Nullable String jsonString){
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_LIGHTBROWN.equals(jsonString)){
            return HairColor.LIGHT_BROWN;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DARKBROWN.equals(jsonString)){
            return HairColor.DARK_BROWN;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_BROWN.equals(jsonString)){
            return HairColor.BROWN;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_LIGHTBLONDE.equals(jsonString)){
            return HairColor.LIGHT_BLONDE;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DARKBLONDE.equals(jsonString)){
            return HairColor.DARK_BLONDE;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_BLONDE.equals(jsonString)){
            return HairColor.BLONDE;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_BLACK.equals(jsonString)){
            return HairColor.BLACK;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_RED.equals(jsonString)){
            return HairColor.RED;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_LIGHTGREY.equals(jsonString)){
            return HairColor.LIGHT_GREY;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DARKGREY.equals(jsonString)){
            return HairColor.DARK_GREY;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DYEDRED.equals(jsonString)){
            return HairColor.DYED_RED;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DYEDBLACK.equals(jsonString)){
            return HairColor.DYED_BLACK;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DYEDBLUE.equals(jsonString)){
            return HairColor.DYED_BLUE;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DYEDGREEN.equals(jsonString)) {
            return HairColor.DYED_GREEN;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DYEDBLONDE.equals(jsonString)){
            return HairColor.DYED_BLONDE;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.HAIR_COLOR_DYEDPURPLE.equals(jsonString)){
            return HairColor.DYED_PURPLE;
        } else {
            throw new IllegalArgumentException("Unknown value for hair length " + jsonString);
        }
    }

    @Nullable
    public static DrinkerFrequency translateToDrinkerFrequency(@Nullable String jsonString){
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.DRINKER_NEVER.equals(jsonString)){
            return DrinkerFrequency.NEVER;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.DRINKER_SELDOM.equals(jsonString)){
            return DrinkerFrequency.SELDOM;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.DRINKER_SOMEIMES.equals(jsonString)){
            return DrinkerFrequency.SOMETIMES;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.DRINKER_REGULARLY.equals(jsonString)){
            return DrinkerFrequency.REGULARLY;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.DRINKER_A_LOT.equals(jsonString)){
            return DrinkerFrequency.A_LOT;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.DRINKER_WINE_ONLY.equals(jsonString)){
            return DrinkerFrequency.WINE_ONLY;
        }  else {
            throw new IllegalArgumentException("Unknown value for drinker frequency " + jsonString);
        }
    }

    @Nullable
    public static SmokerFrequency translateToSmoker(@Nullable String jsonString){
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.SMOKER_NEVER.equals(jsonString)){
            return SmokerFrequency.NEVER;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.SMOKER_ALMOST_NEVER.equals(jsonString)){
            return SmokerFrequency.ALMOST_NEVER;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.SMOKER_ONWEEKENDS.equals(jsonString)){
            return SmokerFrequency.ON_WEEKENDS;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.SMOKER_MODERATELY.equals(jsonString)){
            return SmokerFrequency.MODERATELY;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.SMOKER_A_LOT.equals(jsonString)){
            return SmokerFrequency.A_LOT;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.SMOKER_REALLY_A_LOT.equals(jsonString)){
            return SmokerFrequency.REALlY_A_LOT;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.SMOKER_JOINTS_ONLY.equals(jsonString)){
            return SmokerFrequency.JOINTS_ONLY;
        }  else {
            throw new IllegalArgumentException("Unknown value for smoker frequency " + jsonString);
        }
    }

    @Nullable
    public static Vegetarian translateToVegetarian(@Nullable String jsonString){
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.VEGETARIAN_YES.equals(jsonString)){
            return Vegetarian.YES;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.VEGETARIAN_NO.equals(jsonString)){
            return Vegetarian.NO;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.VEGETARIAN_VEGAN.equals(jsonString)){
            return Vegetarian.VEGAN;
        } else {
            throw new IllegalArgumentException("Unknown value for vegetarian " + jsonString);
        }
    }

    @Nullable
    public static HasKids translateToHasKids(@Nullable String jsonString){
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.HAS_KIDS_ONE.equals(jsonString)){
            return HasKids.ONE;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.HAS_KIDS_MULTIPLE.equals(jsonString)){
            return HasKids.MULTIPLE;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.HAS_KIDS_NONE.equals(jsonString)){
            return HasKids.NONE;
        } else {
            throw new IllegalArgumentException("Unknown value for hasKids " + jsonString);
        }
    }
    @Nullable
    public static WantsKids translateToWantsKids(@Nullable String jsonString){
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.WANTS_KIDS_YES.equals(jsonString)){
            return WantsKids.YES;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.WANTS_KIDS_NO.equals(jsonString)){
            return WantsKids.NO;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.WANTS_KIDS_UNSURE.equals(jsonString)){
            return WantsKids.UNSURE;
        } else {
            throw new IllegalArgumentException("Unknown value for wantsKids " + jsonString);
        }
    }

    @Nullable
    public static Religion translateToReligion(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_AGNOSTIC.equals(jsonString)) {
            return Religion.AGNOSTIC;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_ATHEIST.equals(jsonString)) {
            return Religion.ATHEIST;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_ROMAN_CATHOLIC.equals(jsonString)) {
            return Religion.ROMAN_CATHOLIC;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_OLD_CATHOLIC.equals(jsonString)) {
            return Religion.OLD_CATHOLIC;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_EVANGELICAL_REFORMED.equals(jsonString)) {
            return Religion.EVANGELICAL_REFORMED;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_PROTESTANT.equals(jsonString)) {
            return Religion.PROTESTANT;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_RUSSIAN_ORTHODOX.equals(jsonString)) {
            return Religion.ORTHODOX;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_GREEK_ORTHODOX.equals(jsonString)) {
            return Religion.GREEK_ORTHODOX;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_JEWISH_ORTHODOX.equals(jsonString)) {
            return Religion.JEWISH_ORTHODOX;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_ORTHODOX.equals(jsonString)) {
            return Religion.ORTHODOX;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_CHRISTIAN.equals(jsonString)) {
            return Religion.CHRISTIAN;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_FREECHURCH.equals(jsonString)) {
            return Religion.FREE_CHURCH;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_FOLK_RELIGION.equals(jsonString)) {
            return Religion.FOLK_RELIGION;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_HINDU.equals(jsonString)) {
            return Religion.HINDU;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_SUNNITE.equals(jsonString)) {
            return Religion.SUNNITE;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_SHIITE.equals(jsonString)) {
            return Religion.SHIITE;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_JEWISH.equals(jsonString)) {
            return Religion.JEWISH;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_BUDDHIST.equals(jsonString)) {
            return Religion.BUDDHIST;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_SHINTO.equals(jsonString)) {
            return Religion.SHINTO;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_SPIRITUAL.equals(jsonString)) {
            return Religion.SPIRITUAL;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.RELIGION_OTHER.equals(jsonString)) {
            return Religion.OTHER;
        } else {
            throw new IllegalArgumentException("Unknown value for wantsKids " + jsonString);
        }
    }

    @Nullable
    public static Politics translateToPolitics(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if (PurplemoonAPIConstantsV1.ProfileDetails.POLITICS_LEFT.equals(jsonString)) {
            return Politics.LEFT;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.POLITICS_MIDDLELEFT.equals(jsonString)) {
            return Politics.MIDDLE_LEFT;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.POLITICS_MIDDLE.equals(jsonString)) {
            return Politics.MIDDLE;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.POLITICS_MIDDLERIGHT.equals(jsonString)) {
            return Politics.MIDDLE_RIGHT;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.POLITIC_RIGHT.equals(jsonString)) {
            return Politics.RIGHT;
        } else if (PurplemoonAPIConstantsV1.ProfileDetails.POLITIC_NOINTEREST.equals(jsonString)) {
            return Politics.NO_INTEREST;
        } else {
            throw new IllegalArgumentException("Unknown value for politics " + jsonString);
        }
    }

    public static ChatFrequency translateToChatFrequency(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_FREQUENCY_ALLTHETIME.equals(jsonString)) {
            return ChatFrequency.ALL_THE_TIME;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_FREQUENCY_REGULARLY.equals(jsonString)) {
            return ChatFrequency.REGULARLY;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_FREQUENCY_NOWANDTHEN.equals(jsonString)) {
            return ChatFrequency.NOW_AND_THEN;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_FREQUENCY_OFTEN.equals(jsonString)) {
            return ChatFrequency.OFTEN;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_FREQUENCY_SELDOM.equals(jsonString)) {
            return ChatFrequency.SELDOM;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_FREQUENCY_NEVER.equals(jsonString)) {
            return ChatFrequency.NEVER;
        } else {
            throw new IllegalArgumentException("Unknown value for chat frequency " + jsonString);
        }
    }

    public static MessengerType translateToMessengerType(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_ICQ.equals(jsonString)) {
            return MessengerType.ICQ;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_AIM.equals(jsonString)) {
            return MessengerType.AIM;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_MSN.equals(jsonString)) {
            return MessengerType.MSN;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_YAHOO.equals(jsonString)) {
            return MessengerType.YAHOO;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_SKYPE.equals(jsonString)) {
            return MessengerType.SKYPE;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_JABBER.equals(jsonString)) {
            return MessengerType.JABBER;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_GADU_GADU.equals(jsonString)) {
            return MessengerType.GADU_GADU;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_ICHAT.equals(jsonString)) {
            return MessengerType.ICHAT;
        }  else if(PurplemoonAPIConstantsV1.ProfileDetails.CHATS_MESSENGERS_TYPE_GOOGLETALK.equals(jsonString)) {
            return MessengerType.GOOGLETALK;
        } else {
            throw new IllegalArgumentException("Unknown value for messenger type " + jsonString);
        }
    }

    public static OccupationType translateToOccupationType(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        if(PurplemoonAPIConstantsV1.ProfileDetails.OCCUPATION_TYPE_SCHOOL.equals(jsonString)) {
            return OccupationType.SCHOOL;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.OCCUPATION_TYPE_STUDIES.equals(jsonString)) {
            return OccupationType.STUDIES;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.OCCUPATION_TYPE_TRAINING.equals(jsonString)) {
            return OccupationType.TRAINING;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.OCCUPATION_TYPE_LOOKING.equals(jsonString)) {
            return OccupationType.LOOKING;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.OCCUPATION_TYPE_EMPLOYED.equals(jsonString)) {
            return OccupationType.EMPLOYED;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.OCCUPATION_TYPE_SELF_EMPLOYED.equals(jsonString)) {
            return OccupationType.SELF_EMPLOYED;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.OCCUPATION_TYPE_RAISING_KIDS.equals(jsonString)) {
            return OccupationType.RAISING_KIDS;
        } else if(PurplemoonAPIConstantsV1.ProfileDetails.OCCUPATION_TYPE_RETIRED.equals(jsonString)) {
            return OccupationType.RETIRED;
        } else {
            throw new IllegalArgumentException("Unknown value for occupation type " + jsonString);
        }
    }

}
