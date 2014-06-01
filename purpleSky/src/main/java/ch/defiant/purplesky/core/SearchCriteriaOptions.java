package ch.defiant.purplesky.core;

import android.content.res.Resources;
import android.util.Pair;
import ch.defiant.purplesky.R;
import ch.defiant.purplesky.enums.SearchCriteria;
import ch.defiant.purplesky.enums.SearchCriteriaType;

public class SearchCriteriaOptions {

    public static Pair<Integer, Integer> createMinMaxOptions(SearchCriteria crit) {
        if (crit == null) {
            throw new NullPointerException("Null criteria");
        }
        if (crit.getType() != SearchCriteriaType.MIN_MAX_INT) {
            throw new IllegalArgumentException("Not a min/max integer option!");
        }

        switch (crit) {
            case AGE:
                return new Pair<Integer, Integer>(12, 80);
            default:
                throw new IllegalArgumentException("Unknown min/max option");
        }
    }

    public static Pair<String[], Boolean[]> createBooleanOptions() {
        // TODO Implement
        return null;
    }

    /**
     * Parses the options for a search criterium.
     * 
     * @param crit
     * @return Pair of String arrays: First pair - Label, Second pair - Value
     */
    public static Pair<String[], String[]> createListOptions(SearchCriteria crit) {
        int resourceStrings = 0;
        int resourceValues = 0;

        switch (crit) {
            case GENDER_SEXUALITY: {
                resourceStrings = R.array.usersearch_opts_str_gendersexuality;
                resourceValues = R.array.usersearch_opts_val_gendersexuality;
                break;
            }
            case COUNTRY: {
                resourceStrings = R.array.countryNames;
                resourceValues = R.array.countryIds;
                break;
            }
            case PARTNER_STATUS: {
                resourceStrings = R.array.usersearch_opts_str_partnerstatus;
                resourceValues = R.array.usersearch_opts_val_partnerstatus;
                break;
            }
            case PHYSIQUE: {
                resourceStrings = R.array.usersearch_opts_str_physique;
                resourceValues = R.array.usersearch_opts_val_physique;
                break;
            }
            case EYE_COLOR: {
                resourceStrings = R.array.usersearch_opts_str_eye_color;
                resourceValues = R.array.usersearch_opts_val_eye_color;
                break;
            }
            case HAIR_COLOR: {
                resourceStrings = R.array.usersearch_opts_str_hair_color;
                resourceValues = R.array.usersearch_opts_val_hair_color;
                break;
            }
            case HAIR_LENGTH: {
                resourceStrings = R.array.usersearch_opts_str_hair_length;
                resourceValues = R.array.usersearch_opts_val_hair_length;
                break;
            }
            case FACIAL_HAIR: {
                resourceStrings = R.array.usersearch_opts_str_facial_hair;
                resourceValues = R.array.usersearch_opts_val_facial_hair;
                break;
            }
            case RELIGION: {
                resourceStrings = R.array.usersearch_opts_str_religion;
                resourceValues = R.array.usersearch_opts_val_religion;
                break;
            }
            case POLITICS: {
                resourceStrings = R.array.usersearch_opts_str_politics;
                resourceValues = R.array.usersearch_opts_val_politics;
                break;
            }
            case IS_ONLINE: {
                resourceStrings = R.array.usersearch_opts_str_online;
                resourceValues = R.array.usersearch_opts_val_online;
                break;
            }
            case DRINKER: {
                resourceStrings = R.array.usersearch_opts_str_drinker;
                resourceValues = R.array.usersearch_opts_val_drinker;
                break;
            }
            case SMOKER: {
                resourceStrings = R.array.usersearch_opts_str_smoker;
                resourceValues = R.array.usersearch_opts_val_smoker;
                break;
            }
            case VEGETARIAN: {
                resourceStrings = R.array.usersearch_opts_str_vegetarian;
                resourceValues = R.array.usersearch_opts_val_vegetarian;
                break;
            }
            case KIDS_WANT: {
                resourceStrings = R.array.usersearch_opts_str_kids_want;
                resourceValues = R.array.usersearch_opts_val_kids_want;
                break;
            }
            case KIDS_HAVE: {
                resourceStrings = R.array.usersearch_opts_str_kids_have;
                resourceValues = R.array.usersearch_opts_val_kids_have;
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown list");
        }

        if (resourceStrings == 0 || resourceValues == 0) {
            throw new IllegalStateException("List resource(s) invalid for requested criterium " + crit);
        }

        final Resources resrc = PurpleSkyApplication.get().getResources();
        String[] strings = resrc.getStringArray(resourceStrings);
        String[] values = resrc.getStringArray(resourceValues);

        if (strings == null || values == null) {
            throw new IllegalStateException("List resource(s) not found for requested criterium " + crit);
        }
        if (strings.length != values.length) {
            throw new IllegalStateException("Lengths for strings / values do not match for criterium " + crit);
        }

        return new Pair<String[], String[]>(strings, values);
    }

}
