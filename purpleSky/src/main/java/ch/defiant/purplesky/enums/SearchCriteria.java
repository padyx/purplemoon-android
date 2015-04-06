package ch.defiant.purplesky.enums;

import ch.defiant.purplesky.R;

public enum SearchCriteria {

    AGE(SearchCriteriaType.MIN_MAX_INT, false, R.string.Age),
    GENDER_SEXUALITY(SearchCriteriaType.LIST, false, R.string.GenderAndSexuality),
    IS_ONLINE(SearchCriteriaType.LIST, false, R.string.Online),
    COUNTRY(SearchCriteriaType.LIST, false, R.string.Country),
    DISTANCE(SearchCriteriaType.INT, true, R.string.Distance),
    PARTNER_STATUS(SearchCriteriaType.LIST, true, R.string.RelationshipStatus), 
    HEIGHT(SearchCriteriaType.MIN_MAX_INT, true, R.string.bodyHeight),
    WEIGHT(SearchCriteriaType.MIN_MAX_INT, true, R.string.bodyWeight),
    BMI(SearchCriteriaType.MIN_MAX_INT, true, 0), //XXX
    PHYSIQUE(SearchCriteriaType.LIST, true, R.string.profileKeyText_physique),
    EYE_COLOR(SearchCriteriaType.LIST, true, R.string.profile_eye_color),
    HAIR_COLOR(SearchCriteriaType.LIST, true, R.string.profileKeyText_hair_color),
    HAIR_LENGTH(SearchCriteriaType.LIST, true, R.string.profileKeyText_hair_length),
    FACIAL_HAIR(SearchCriteriaType.LIST, true, R.string.profileKeyText_facialhair),
    DRINKER(SearchCriteriaType.LIST, true, R.string.profileKeyText_drinker),
    SMOKER(SearchCriteriaType.LIST, true, R.string.profileKeyText_smoker),
    VEGETARIAN(SearchCriteriaType.LIST, true, R.string.profileKeyText_vegetarian),
    KIDS_WANT(SearchCriteriaType.LIST, true, R.string.profileKeyText_kids_want),
    KIDS_HAVE(SearchCriteriaType.LIST, true, R.string.profileKeyText_kids_have),
    RELIGION(SearchCriteriaType.LIST, true, R.string.profileKeyText_religion),
    POLITICS(SearchCriteriaType.LIST, true, R.string.profileKeyText_politics),
    PICTURES_EXIST(SearchCriteriaType.BOOLEAN, true, R.string.HasPictures),
    PUBLIC_PICTURES_EXIST(SearchCriteriaType.BOOLEAN, true, R.string.HasPublicPictures),
    EXCLUDE_FRIENDS_AND_KNOWN(SearchCriteriaType.BOOLEAN, true, R.string.ExcludeFriendsAndKnown),
    INCLUDE_NONMATCHING(SearchCriteriaType.BOOLEAN, true, R.string.IncludeNonMatching);

    private final SearchCriteriaType m_type;
    private final boolean m_poweruserOnly;
    private int m_stringRes;

    SearchCriteria(SearchCriteriaType type, boolean poweruserOnly, int stringRes) {
        m_type = type;
        m_poweruserOnly = poweruserOnly;
        m_stringRes = stringRes;
    }

    public SearchCriteriaType getType() {
        return m_type;
    }

    public boolean isPoweruserOnly() {
        return m_poweruserOnly;
    }

    public boolean isSingleSelect() {
        return getType() == SearchCriteriaType.BOOLEAN || getType() == SearchCriteriaType.MIN_MAX_INT;
    }

    public int getStringResource() {
        return m_stringRes;
    }

}
