package ch.defiant.purplesky.api.promotions.internal;

import org.json.JSONArray;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import ch.defiant.purplesky.api.common.APINetworkUtility;
import ch.defiant.purplesky.api.internal.PurplemoonAPIConstantsV1;
import ch.defiant.purplesky.api.promotions.IPromotionAdapter;
import ch.defiant.purplesky.beans.promotion.Promotion;
import ch.defiant.purplesky.exceptions.PurpleSkyException;

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

}
