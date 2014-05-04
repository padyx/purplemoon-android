package ch.defiant.purplesky.translators;

import java.util.List;
import java.util.Map;

import android.util.Log;
import ch.defiant.purplesky.beans.util.Pair;
import ch.defiant.purplesky.core.UserSearchOptions;
import ch.defiant.purplesky.enums.SearchCriteria;

public class SearchCriteriaTranslator {

    private static final String TAG = SearchCriteriaTranslator.class.getSimpleName();

    public static void setSearchCriteria(UserSearchOptions opts, Map<SearchCriteria, Object> values) {
        if (opts == null) {
            Log.w(TAG, "Search options to translate empty!");
            return;
        }

        for (SearchCriteria crit : values.keySet()) {
            switch (crit) {
                case AGE: {
                    @SuppressWarnings("unchecked")
                    Pair<Integer, Integer> age = (Pair<Integer, Integer>) values.get(crit);
                    opts.setMinAge(age.getFirst());
                    opts.setMaxAge(age.getSecond());
                    break;
                }
                case BMI:
                    break;
                case COUNTRY:
                    break;
                case DISTANCE:
                    break;
                case DRINKER:
                    break;
                case EXCLUDE_FRIENDS_AND_KNOWN:
                    break;
                case EYE_COLOR:
                    break;
                case FACIAL_HAIR:
                    break;
                case GENDER_SEXUALITY:
                    // Is a list
                    @SuppressWarnings("unchecked")
                    List<String> genderSex = (List<String>) values.get(crit);
                    opts.setGenderSexualities(genderSex);
                    break;
                case HAIR_COLOR:
                    break;
                case HAIR_LENGTH:
                    break;
                case HEIGHT:
                    break;
                case INCLUDE_NONMATCHING:
                    break;
                case IS_ONLINE:
                    break;
                case KIDS_HAVE:
                    break;
                case KIDS_WANT:
                    break;
                case PARTNER_STATUS:
                    break;
                case PHYSIQUE:
                    break;
                case PICTURES_EXIST:
                    break;
                case POLITICS:
                    break;
                case PUBLIC_PICTURES_EXIST:
                    break;
                case RELIGION:
                    break;
                case SMOKER:
                    break;
                case VEGETARIAN:
                    break;
                case WEIGHT:
                    break;
                default:
                    Log.e(TAG, "Unknown search criterium " + crit);
            }
        }
    }

}
