package ch.defiant.purplesky.api.photovotes.internal;

import ch.defiant.purplesky.enums.PhotoVoteVerdict;

/**
 * @author Patrick Bänziger
 * @since v.1.1.0
 */
class PhotoVoteAPIUtility {

    public static String translatePhotoVoteVerdict(PhotoVoteVerdict verdict) {
        switch (verdict) {
            case NEUTRAL_NEGATIVE:
                return String.valueOf(PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT_NEUTRAL_NEGATIVE);
            case CUTE_ATTRACTIVE:
                return String.valueOf(PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT_CUTE_ATTRACTIVE);
            case VERY_ATTRACTIVE:
                return String.valueOf(PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT_VERY_ATTRACTIVE);
            case STUNNING:
                return String.valueOf(PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT_STUNNING);
            default:
                throw new IllegalArgumentException("No api value for " + verdict);
        }
    }

    public static PhotoVoteVerdict toPhotoVoteVerdict(int apiValue) {
        switch (apiValue) {
            case PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT_NEUTRAL_NEGATIVE:
                return PhotoVoteVerdict.NEUTRAL_NEGATIVE;
            case PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT_CUTE_ATTRACTIVE:
                return PhotoVoteVerdict.CUTE_ATTRACTIVE;
            case PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT_VERY_ATTRACTIVE:
                return PhotoVoteVerdict.VERY_ATTRACTIVE;
            case PhotoVoteAPIConstants.JSON_PHOTOVOTE_VERDICT_STUNNING:
                return PhotoVoteVerdict.STUNNING;
            default:
                throw new IllegalArgumentException("No api value for " + apiValue);
        }
    }

}
