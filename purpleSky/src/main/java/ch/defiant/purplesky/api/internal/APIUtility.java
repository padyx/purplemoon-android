package ch.defiant.purplesky.api.internal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.constants.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.enums.Gender;
import ch.defiant.purplesky.enums.PhotoVoteVerdict;
import ch.defiant.purplesky.enums.Sexuality;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

/**
 * @author Patrick Bänziger
 * @since 1.0.1
 */
public final class APIUtility {

    public static JSONObject getJSONUserSearchObject(UserSearchOptions options) throws PurpleSkyException {
        JSONObject object = new JSONObject();
        try {
            if (options.getGenderSexualities() != null && !options.getGenderSexualities().isEmpty()) { // TODO Remove
                JSONArray arr = new JSONArray();
                for (String pair : options.getGenderSexualities()) {
                    if (pair == null) {
                        continue;
                    }
                    arr.put(pair);
                }

                object.put(PurplemoonAPIConstantsV1.JSON_USERSEARCH_GENDER_SEXUALITY, arr);
            }

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
            throw new PurpleSkyException("Internal error",e); // FIXME pbn error handling
        }
    }

    private static JSONArray createGenderSexualityCombinations(List<Pair<Gender, Sexuality>> attractions) {
        JSONArray array = new JSONArray();
        for(Pair<Gender, Sexuality> p: attractions){
            String genderString = translateGender(p.getFirst());
            array.put(genderString +PurplemoonAPIConstantsV1.JSON_USERSEARCH_GENDER_SEXUALITY_SEPARATOR + translateSexuality(p.getSecond()));
        }
        return array;
    }

    public static String translateSexuality(Sexuality s) {
        switch (s) {
            case HETEROSEXUAL_MALE:
                return PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE;
            case HETEROSEXUAL_FEMALE:
                return PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HETEROSEXUAL_VALUE;
            case GAY:
                return PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE;
            case LESBIAN:
                return PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_HOMOSEXUAL_VALUE;
            case BISEXUAL:
                return PurplemoonAPIConstantsV1.JSON_USER_SEXUALITY_BISEXUAL_VALUE;
            default:
                throw new IllegalArgumentException("No api value for "+s);
        }
    }

    public static String translateGender(Gender g){
        switch(g){
            case MALE:
                return "male";
            case FEMALE:
                return "female";
            default:
                throw new IllegalArgumentException("No api value for "+g);
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
                throw new IllegalArgumentException("No api value for "+lastOnline);
        }

    }

    public static String translatePhotoVoteVerdict(PhotoVoteVerdict verdict){
        switch (verdict){
            case NEUTRAL_NEGATIVE:
                return String.valueOf(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT_NEUTRAL_NEGATIVE);
            case CUTE_ATTRACTIVE:
                return String.valueOf(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT_CUTE_ATTRACTIVE);
            case VERY_ATTRACTIVE:
                return String.valueOf(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT_VERY_ATTRACTIVE);
            case STUNNING:
                return String.valueOf(PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT_STUNNING);
            default:
                throw new IllegalArgumentException("No api value for "+verdict);
        }
    }

    public static PhotoVoteVerdict toPhotoVoteVerdict(int apiValue) {
        switch(apiValue){
            case PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT_NEUTRAL_NEGATIVE:
                return PhotoVoteVerdict.NEUTRAL_NEGATIVE;
            case PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT_CUTE_ATTRACTIVE:
                return PhotoVoteVerdict.CUTE_ATTRACTIVE;
            case PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT_VERY_ATTRACTIVE:
                return PhotoVoteVerdict.VERY_ATTRACTIVE;
            case PurplemoonAPIConstantsV1.JSON_PHOTOVOTE_VERDICT_STUNNING:
                return PhotoVoteVerdict.STUNNING;
            default:
                throw new IllegalArgumentException("No api value for "+apiValue);
        }
    }
}
