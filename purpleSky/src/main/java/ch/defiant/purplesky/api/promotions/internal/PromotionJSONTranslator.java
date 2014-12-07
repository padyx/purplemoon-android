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

import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.beans.promotion.Promotion;
import ch.defiant.purplesky.beans.promotion.PromotionBuilder;
import ch.defiant.purplesky.util.CompareUtility;

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

        Long validFrom = obj.optLong(PromotionAPIConstants.Promotion.JSON_VALIDFROM);
        Long validTo = obj.optLong(PromotionAPIConstants.Promotion.JSON_VALIDTO);
        String pictureUrl = obj.optString(PromotionAPIConstants.Promotion.JSON_PICTURE);
        String eventUrl = obj.optString(PromotionAPIConstants.Promotion.JSON_PROMOURL);

        PromotionBuilder builder = new PromotionBuilder().
                setId(obj.optInt(PromotionAPIConstants.Promotion.JSON_ID)).
                setTitle(obj.optString(PromotionAPIConstants.Promotion.JSON_TITLE)).
                setText(obj.optString(PromotionAPIConstants.Promotion.JSON_TEXT)).
                setEventId(obj.optInt(PromotionAPIConstants.Promotion.JSON_EVENTID)).
                setImportance(obj.optInt(PromotionAPIConstants.Promotion.JSON_IMPORTANCE));

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

    public static @Nullable Event translateEvent(JSONObject object) {
        if(object == null){
            return null;
        }

        Event event = new Event();
        event.setEventId(object.optInt(PromotionAPIConstants.Event.JSON_ID));
        event.setEventName(object.optString(PromotionAPIConstants.Event.JSON_NAME));
        event.setDescriptionHtml(object.optString(PromotionAPIConstants.Event.JSON_DESCRIPTION));
        event.setAdmissionPriceHtml(object.optString(PromotionAPIConstants.Event.JSON_ADMISSION));

        event.setMinAge(object.optInt(PromotionAPIConstants.Event.JSON_AGEMIN));
        int maxAge = object.optInt(PromotionAPIConstants.Event.JSON_AGEMAX, 250);
        event.setMaxAge(maxAge != PromotionAPIConstants.Event.MAX_AGE_NULL_VALUE ? maxAge : null);

        event.setPreliminary(object.optBoolean(PromotionAPIConstants.Event.JSON_PRELIMINARY));
        event.setRegistrations(object.optInt(PromotionAPIConstants.Event.JSON_REGISTRATIONS));

        String status = object.optString(PromotionAPIConstants.Event.JSON_STATUS);
        if(CompareUtility.equals(status, PromotionAPIConstants.Event.JSON_STATUS_PRIVATE)){
            event.setPrivate(true);
        } else if (CompareUtility.equals(status, PromotionAPIConstants.Event.JSON_STATUS_PUBLIC)){
            event.setPrivate(false);
        } else {
            // OOPS
            event.setPrivate(true);
            Log.e(TAG, "API delivered unknown status value for event (" + event.getEventId() + "): " + status);
        }
        long startDate = object.optLong(PromotionAPIConstants.Event.JSON_DATEFROM);
        if (startDate != 0L){
            event.setStart(new Date(startDate));
        }
        long endDate = object.optLong(PromotionAPIConstants.Event.JSON_DATEUNTIL);
        if (endDate != 0L){
            event.setEnd(new Date(endDate));
        }

        // TODO IMPLEMENT MORE: Flyer, location, organizer etc.

        return event;
    }
}
