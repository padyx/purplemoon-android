package ch.defiant.purplesky.api.promotions.internal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.common.ApiResponse;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.api.promotions.EventRegistrationResult;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.beans.promotion.Promotion;
import ch.defiant.purplesky.exceptions.PurpleSkyException;
import ch.defiant.purplesky.util.CollectionUtil;

/**
 * @author Patrick Bänziger
 */
public class PromotionAdapter implements IPromotionAdapter {

    @Override
    public List<Promotion> getActivePromotions() throws IOException, PurpleSkyException {
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + PromotionAPIConstants.PROMOTION_URL);

        JSONArray array = APINetworkUtility.performGETRequestForJSONArray(url);
        if(array == null || array.length() == 0){
            return Collections.emptyList();
        }

        return PromotionJSONTranslator.translatePromotions(array);
    }


    @Override
    public Event getEvent(int eventId) throws IOException, PurpleSkyException {
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL +
                PromotionAPIConstants.EVENT_URL +
                PromotionAPIConstants.EVENT_TYPE_EVENT + "+" +
                PromotionAPIConstants.EVENT_TYPE_REGISTRATION  + "+" +
                PromotionAPIConstants.EVENT_TYPE_LOCATION +
                "/" + eventId);
        JSONArray object = APINetworkUtility.performGETRequestForJSONArray(url);

        return CollectionUtil.firstElement(PromotionJSONTranslator.translateEvents(object));
    }

    @Override
    public EventRegistrationResult register(int eventId, Event.RegistrationVisibility visibility) throws IOException, PurpleSkyException {
        URL url = new URL(PurplemoonAPIConstantsV1.BASE_URL + PromotionAPIConstants.REGISTER_URL + eventId);

        ApiResponse<JSONObject> response = APINetworkUtility.getJSONObject(url);
        if (response.isError()) {
            String error = response.getError();
            if (PromotionAPIConstants.REGISTER_ERROR_NOTFOUND.equals(response.getError())) {
                return EventRegistrationResult.ERROR_NOT_FOUND;
            } else if (PromotionAPIConstants.REGISTER_ERROR_PRELIMINARY.equals(response.getError())) {
                return EventRegistrationResult.ERROR_PRELIMINARY;
            } else if (PromotionAPIConstants.REGISTER_ERROR_TOOOLD.equals(response.getError())) {
                return EventRegistrationResult.ERROR_TOO_OLD;
            } else if (PromotionAPIConstants.REGISTER_ERROR_TOOYOUNG.equals(response.getError())) {
                return EventRegistrationResult.ERROR_TOO_YOUNG;
            } else if (PromotionAPIConstants.REGISTER_ERROR_WRONGGENDER.equals(response.getError())) {
                return EventRegistrationResult.ERROR_WRONG_GENDER;
            } else {
                return EventRegistrationResult.ERROR_GENERIC;
            }
        } else {
            return EventRegistrationResult.SUCCESS;
        }
    }

}
