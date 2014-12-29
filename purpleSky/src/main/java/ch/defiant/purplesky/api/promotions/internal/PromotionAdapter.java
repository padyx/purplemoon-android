package ch.defiant.purplesky.api.promotions.internal;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
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
                 PromotionAPIConstants.EVENT_TYPE_EVENT + "+" + PromotionAPIConstants.EVENT_TYPE_REGISTRATION  +
                "/" + eventId);
        JSONArray object = APINetworkUtility.performGETRequestForJSONArray(url);

        return CollectionUtil.firstElement(PromotionJSONTranslator.translateEvents(object));
    }

}
