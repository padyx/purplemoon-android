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
import java.util.List;

import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.beans.promotion.Promotion;
import ch.defiant.purplesky.beans.promotion.PromotionBuilder;
import ch.defiant.purplesky.beans.promotion.PromotionPicture;
import ch.defiant.purplesky.util.DateUtility;

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

    public static @Nullable Promotion translatePromotion(JSONObject obj) throws JSONException {
        if(obj == null){
            return null;
        }

        Long validFrom = obj.optLong(PromotionAPIConstants.Promotion.JSON_VALIDFROM);
        Long validTo = obj.optLong(PromotionAPIConstants.Promotion.JSON_VALIDTO);
        JSONArray pictureUrlArray = obj.optJSONArray(PromotionAPIConstants.Promotion.JSON_PICTURE);
        String eventUrl = obj.optString(PromotionAPIConstants.Promotion.JSON_PROMOURL);

        PromotionBuilder builder = new PromotionBuilder().
                setId(obj.optInt(PromotionAPIConstants.Promotion.JSON_ID)).
                setTitle(obj.optString(PromotionAPIConstants.Promotion.JSON_TITLE)).
                setText(obj.optString(PromotionAPIConstants.Promotion.JSON_TEXT)).
                setEventId(obj.optInt(PromotionAPIConstants.Promotion.JSON_EVENTID)).
                setImportance(obj.optInt(PromotionAPIConstants.Promotion.JSON_IMPORTANCE));

        if (pictureUrlArray != null) {
            final int size = pictureUrlArray.length();
            List<PromotionPicture> list = new ArrayList<>();
            for(int i=0; i<size; i++){
                JSONObject pictureObj = pictureUrlArray.getJSONObject(i);
                if(pictureObj == null){
                    continue;
                }
                list.add(new PromotionPicture(
                        pictureObj.optInt(PromotionAPIConstants.Promotion.JSON_PICTURE_HEIGHT),
                        pictureObj.optInt(PromotionAPIConstants.Promotion.JSON_PICTURE_WIDTH),
                        Uri.parse(pictureObj.optString(PromotionAPIConstants.Promotion.JSON_PICTURE_URL))
                ));
            }
            builder.setPromotionPictures(list);
        }
        if (validFrom != 0L){
            builder.setValidFrom(DateUtility.getFromUnixTime(validFrom)); // FIXME Wrong date translated
        }
        if (validTo != 0L){
            builder.setValidTo(DateUtility.getFromUnixTime(validTo));
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

        boolean registered = object.has(PromotionAPIConstants.Event.JSON_REGISTRATION);
        event.setRegistered(registered);
        if (registered) {
            event.setRegistrationVisibility(translateVisibility(object.optString(PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY)));
        }

        event.setMinAge(object.optInt(PromotionAPIConstants.Event.JSON_AGEMIN));
        int maxAge = object.optInt(PromotionAPIConstants.Event.JSON_AGEMAX, 250);
        event.setMaxAge(maxAge != PromotionAPIConstants.Event.MAX_AGE_NULL_VALUE ? maxAge : null);

        event.setPreliminary(object.optBoolean(PromotionAPIConstants.Event.JSON_PRELIMINARY));
        event.setRegistrations(object.optInt(PromotionAPIConstants.Event.JSON_REGISTRATIONS));

        event.setPrivate(object.optBoolean(PromotionAPIConstants.Event.JSON_PRIVATE));

        long startDate = object.optLong(PromotionAPIConstants.Event.JSON_DATEFROM);
        if (startDate != 0L){
            event.setStart(DateUtility.getFromUnixTime(startDate));
        }
        long endDate = object.optLong(PromotionAPIConstants.Event.JSON_DATEUNTIL);
        if (endDate != 0L){
            event.setEnd(DateUtility.getFromUnixTime(endDate));
        }

        // TODO IMPLEMENT MORE: Flyer, location, organizer etc.

        return event;
    }

    public static Event.RegistrationVisibility translateVisibility(String visibility){
        if(visibility == null){
            return Event.RegistrationVisibility.NONE;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISiBILITY_ALL.equals(visibility)){
            return Event.RegistrationVisibility.ALL;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISiBILITY_FRIENDS_AND_KNOWN.equals(visibility)){
            return Event.RegistrationVisibility.FRIENDS_AND_KNOWN;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISiBILITY_FRIENDS.equals(visibility)){
            return Event.RegistrationVisibility.FRIENDS;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISiBILITY_KNOWN.equals(visibility)){
            return Event.RegistrationVisibility.KNOWN;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISiBILITY_NONE.equals(visibility)){
            return Event.RegistrationVisibility.NONE;
        } else {
            throw new IllegalStateException("Could not translate registration visibility: "+visibility);
        }
    }

    public static List<Event> translateEvents(JSONArray object) {
        if(object == null){
            return Collections.emptyList();
        }
        ArrayList<Event> list = new ArrayList<>();
        final int size = object.length();
        for(int i=0; i<size; i++){
            Event e = translateEvent(object.optJSONObject(i));
            if(e != null){
                list.add(e);
            }
        }

        return list;
    }
}
