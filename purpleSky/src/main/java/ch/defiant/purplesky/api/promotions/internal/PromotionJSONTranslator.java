package ch.defiant.purplesky.api.promotions.internal;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ch.defiant.purplesky.beans.promotion.Promotion;
import ch.defiant.purplesky.beans.promotion.PromotionBuilder;

/**
 * @author Patrick Bänziger
 */
class PromotionJSONTranslator {

    private static final String TAG = PromotionJSONTranslator.class.getSimpleName();

    public static @NonNull List<Promotion> translatePromotions(JSONArray array){
        if(array == null){
            return Collections.emptyList();
        }

        List<Promotion> list = new ArrayList<Promotion>();

        final int size = array.length();
        for (int i = 0; i<size; i++){
            try {
                Promotion p = translatePromotion(array.getJSONObject(i));
                list.add(p);
            } catch (JSONException e) {
                Log.w(TAG, "Could not translate promotion response at index " + i);
            }
        }
        return list;
    }

    public static @Nullable Promotion translatePromotion(JSONObject obj){
        if(obj == null){
            return null;
        }

        Long validFrom = obj.optLong(PromotionAPIConstants.JSON_VALIDFROM);
        Long validTo = obj.optLong(PromotionAPIConstants.JSON_VALIDTO);
        String pictureUrl = obj.optString(PromotionAPIConstants.JSON_PICTURE);
        String eventUrl = obj.optString(PromotionAPIConstants.JSON_PROMOURL);

        PromotionBuilder builder = new PromotionBuilder().
                setId(obj.optInt(PromotionAPIConstants.JSON_ID)).
                setTitle(obj.optString(PromotionAPIConstants.JSON_TITLE)).
                setText(obj.optString(PromotionAPIConstants.JSON_TEXT)).
                setEventId(obj.optInt(PromotionAPIConstants.JSON_EVENTID)).
                setImportance(obj.optInt(PromotionAPIConstants.JSON_IMPORTANCE));

        if (pictureUrl != null) {
            builder.setPictureUri(Uri.parse(pictureUrl));
        }
        if (validFrom != 0L){
            builder.setValidFrom(new Date(validFrom));
        }
        if (validTo != 0L){
            builder.setValidTo(new Date(validTo));
        }
        if (eventUrl != null){
            builder.setEventUri(Uri.parse(eventUrl));
        }
        return builder.build();
    }

}
