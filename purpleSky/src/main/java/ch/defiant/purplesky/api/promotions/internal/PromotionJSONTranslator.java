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
import ch.defiant.purplesky.beans.promotion.EventLocation;
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
        event.setGenders(translateEventGender(object.optString(PromotionAPIConstants.Event.JSON_GENDERS)));

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

        if (object.has(PromotionAPIConstants.Event.JSON_LOCATION)){
            event.setLocation(translateEventLocation(object.optJSONObject(PromotionAPIConstants.Event.JSON_LOCATION)));
        }


        // TODO IMPLEMENT MORE: Flyer, organizer etc.

        return event;
    }

    private static EventLocation translateEventLocation(JSONObject jsonObject) {
        if(jsonObject == null){
            return null;
        }

        EventLocation location = new EventLocation();
        location.setLocationId(jsonObject.optInt(PromotionAPIConstants.EventLocation.JSON_ID));
        location.setLocationName(jsonObject.optString(PromotionAPIConstants.EventLocation.JSON_LOCATIONNAME));
        location.setAddress(jsonObject.optString(PromotionAPIConstants.EventLocation.JSON_ADDRESS));
        location.setCountryCode(jsonObject.optString(PromotionAPIConstants.EventLocation.JSON_COUNTRYCODE));
        location.setRegionCode(jsonObject.optString(PromotionAPIConstants.EventLocation.JSON_REGIONCODE));
        location.setVillage(jsonObject.optString(PromotionAPIConstants.EventLocation.JSON_VILLAGE));
        location.setLatitude(jsonObject.optDouble(PromotionAPIConstants.EventLocation.JSON_LATITUDE));
        location.setLongitude(jsonObject.optDouble(PromotionAPIConstants.EventLocation.JSON_LONGITUDE));

        if(jsonObject.has(PromotionAPIConstants.EventLocation.JSON_WEBSITE)){
            location.setWebsite(Uri.parse(jsonObject.optString(PromotionAPIConstants.EventLocation.JSON_WEBSITE)));
        }

        return location;
    }

    public static Event.RegistrationVisibility translateVisibility(String visibility){
        if(visibility == null){
            return Event.RegistrationVisibility.NONE;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_ALL.equals(visibility)){
            return Event.RegistrationVisibility.ALL;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_FRIENDS_AND_KNOWN.equals(visibility)){
            return Event.RegistrationVisibility.FRIENDS_AND_KNOWN;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_FRIENDS.equals(visibility)){
            return Event.RegistrationVisibility.FRIENDS;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_KNOWN.equals(visibility)){
            return Event.RegistrationVisibility.KNOWN;
        } else if (PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_NONE.equals(visibility)){
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

    private static Event.Genders translateEventGender(String gender){
        if(PromotionAPIConstants.Event.JSON_GENDERS_ALL.equals(gender)){
            return Event.Genders.ALL;
        } else if (PromotionAPIConstants.Event.JSON_GENDERS_MEN_ONLY.equals(gender)){
            return Event.Genders.MEN_ONLY;
        } else if (PromotionAPIConstants.Event.JSON_GENDERS_WOMEN_ONLY.equals(gender)){
            return Event.Genders.WOMEN_ONLY;
        } else if (PromotionAPIConstants.Event.JSON_GENDERS_MOSTLY_MEN.equals(gender)){
            return Event.Genders.MOSTLY_MEN;
        } else if (PromotionAPIConstants.Event.JSON_GENDERS_MOSTLY_WOMEN.equals(gender)){
            return Event.Genders.MOSTLY_WOMEN;
        } else {
            throw new IllegalArgumentException("Unknown JSON gender: " + gender);
        }
    }

    public static String translate(Event.RegistrationVisibility visibility) {
        if(visibility == null){
            return "";
        }
        switch (visibility){
            case ALL:
                return PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_ALL;
            case FRIENDS_AND_KNOWN:
                return PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_FRIENDS_AND_KNOWN;
            case FRIENDS:
                return PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_FRIENDS;
            case KNOWN:
                return PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_KNOWN;
            case NONE:
                return PromotionAPIConstants.Event.JSON_REGISTRATION_VISIBILITY_NONE;
            default:
                throw new IllegalArgumentException("Missing translation for visibility");
        }
    }
}
