package ch.defiant.purplesky.fragments.event;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.defiant.purplesky.R;
import ch.defiant.purplesky.beans.promotion.Event;
import ch.defiant.purplesky.beans.promotion.EventFlyer;
import ch.defiant.purplesky.beans.promotion.EventLocation;
import ch.defiant.purplesky.core.PurpleSkyApplication;
import ch.defiant.purplesky.util.DateUtility;

/**
 * @author Patrick Bänziger
 */
public class EventHTMLTranslator {

    public static String promoToHtml(Context c, Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
        sb.append("<style>td {padding: 8px} tr:nth-child(odd) {background-color:#BBE4FA;} " +
                "tr:nth-child(even) {background-color:#AFD1E3;}</style>");
        sb.append("</head>\n");
        sb.append("<body><h2>");
        sb.append(event.getEventName());
        sb.append("</h2>");
        if(event.getEventFlyers() != null && !event.getEventFlyers().isEmpty()) {
            EventFlyer flyer = findFlyerById(event.getPreviewFlyerId(), event.getEventFlyers());
            if(flyer != null) {
                Uri uri = flyer.getPictureBaseUri();
                sb.append("<img style='float:left; margin-right: 8px; margin-bottom: 8px' src='");
                sb.append(uri.toString());
                sb.append("400/w' width='30%' />"); // FIXME Determine w/h from context
            }
        }
        sb.append("<p style='font-face: Verdana,Arial,sans-serif;'>");
        sb.append(event.getDescriptionHtml());
        sb.append("</p>\n");

        if(event.isPreliminary()) {
            sb.append("<p><em>");
            sb.append(c.getString(R.string.PreliminaryEventNoRegistration));
            sb.append("</em></p>");
        }

        sb.append("<p>");
        String date = DateUtility.getMediumDateTimeString(event.getStart());
        if(event.getEnd() != null){
            date += " - ";
            if (DateUtility.isSameDay(event.getStart(), event.getEnd())){
                date += android.text.format.DateFormat.getTimeFormat(PurpleSkyApplication.get()).format(event.getEnd());
            } else {
                date += DateUtility.getMediumDateTimeString(event.getEnd());
            }
        }
        sb.append(date);
        sb.append("</p><h3>");

        sb.append(c.getString(R.string.Admission));
        sb.append("</h3><table style='border:0'>");
        if(event.isPrivate()){
            addRow(sb, c.getString(R.string.Admission), c.getString(R.string.Limited_PrivateEvent));
        }
        if(event.getGenders() != null){
            Event.Genders genders = event.getGenders();
            if(Arrays.asList(Event.Genders.MEN_ONLY, Event.Genders.WOMEN_ONLY).contains(genders)){
                addRow(sb, c.getString(R.string.EntranceOnlyFor), c.getString(genders.resourceId));
            }
            else if (genders != Event.Genders.ALL) {
                // We don't care about all
                addRow(sb, c.getString(R.string.Audience), c.getString(genders.resourceId));
            }
        }
        if(event.getMinAge() != null) {
            addRow(sb, c.getString(R.string.MinimumAge), String.valueOf(event.getMinAge()));
        }
        if(event.getMaxAge() != null) {
            addRow(sb, c.getString(R.string.MaximumAge), String.valueOf(event.getMaxAge()));
        }
        addRow(sb, c.getString(R.string.Registrations), String.valueOf(event.getRegistrations()));
        sb.append("</table>");



        if(event.getLocation() != null) {
            EventLocation location = event.getLocation();
            sb.append("<h3>");
            sb.append(c.getString(R.string.EventLocation));
            sb.append("</h3><table style='border:0'>");

            addRow(sb, c.getString(R.string.Name), location.getLocationName());
            addRow(sb, c.getString(R.string.Address), location.getAddress());
            addRow(sb, c.getString(R.string.Country), new Locale("", location.getCountryCode().toUpperCase()).getDisplayCountry());

            if(location.getWebsite() != null) {
                addRow(sb,
                        c.getString(R.string.Information),
                        "<a href='"+location.getWebsite()+"'>"+c.getString(R.string.Website)+"</a>");
            }

            sb.append("</table>");
        }
        sb.append("</body></html>");

        return sb.toString();
    }

    @Nullable
    private static EventFlyer findFlyerById(int previewFlyerId, List<EventFlyer> eventFlyers) {
        if(eventFlyers == null || eventFlyers.isEmpty()){
            return null;
        }
        for(EventFlyer f : eventFlyers){
            if(f != null && f.getFlyerId() == previewFlyerId){
                return f;
            }
        }
        return null;
    }

    private static void addRow(StringBuilder sb, String firstCol, String secondCol) {
        sb.append("<tr><td>");
        sb.append(firstCol);
        sb.append("</td><td>");
        sb.append(secondCol);
        sb.append("</td></tr>\n");
    }
}
