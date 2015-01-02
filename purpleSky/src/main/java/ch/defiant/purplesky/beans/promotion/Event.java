package ch.defiant.purplesky.beans.promotion;

import android.support.annotation.StringRes;

import java.util.Date;

import ch.defiant.purplesky.R;

/**
 * @author Patrick Bänziger
 */
public class Event {

    private int m_eventId;
    private boolean m_private;
    private boolean m_preliminary;
    private boolean m_isRegistered;
    private RegistrationVisibility m_registrationVisibility;
    private String m_eventName;
    private String m_admissionPriceHtml;
    private String m_descriptionHtml;
    private Date m_start;
    private Date m_end;



    private EventLocation m_location;
    // TODO Implement more fields
    // - Timezone
    // - Genders
    // - Flyer
    // - Preview flyer id
    // - Journey
    private Integer m_minAge;
    private Integer m_maxAge;
    private int m_registrations;
    private Genders m_genders;


    public int getEventId() {
        return m_eventId;
    }

    public void setEventId(int eventId) {
        m_eventId = eventId;
    }

    public boolean isPrivate() {
        return m_private;
    }

    public void setPrivate(boolean aPrivate) {
        m_private = aPrivate;
    }

    public boolean isPreliminary() {
        return m_preliminary;
    }

    public void setPreliminary(boolean preliminary) {
        m_preliminary = preliminary;
    }

    public String getEventName() {
        return m_eventName;
    }

    public void setEventName(String eventName) {
        m_eventName = eventName;
    }

    public String getAdmissionPriceHtml() {
        return m_admissionPriceHtml;
    }

    public void setAdmissionPriceHtml(String admissionPriceHtml) {
        m_admissionPriceHtml = admissionPriceHtml;
    }

    public String getDescriptionHtml() {
        return m_descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        m_descriptionHtml = descriptionHtml;
    }

    public Date getStart() {
        return m_start;
    }

    public void setStart(Date start) {
        m_start = start;
    }

    public Date getEnd() {
        return m_end;
    }

    public void setEnd(Date end) {
        m_end = end;
    }

    public Integer getMinAge() {
        return m_minAge;
    }

    public void setMinAge(Integer minAge) {
        m_minAge = minAge;
    }

    public Integer getMaxAge() {
        return m_maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        m_maxAge = maxAge;
    }

    public int getRegistrations() {
        return m_registrations;
    }

    public void setRegistrations(int registrations) {
        m_registrations = registrations;
    }

    public RegistrationVisibility getRegistrationVisibility() {
        return m_registrationVisibility;
    }

    public void setRegistrationVisibility(RegistrationVisibility registrationVisibility) {
        m_registrationVisibility = registrationVisibility;
    }

    public boolean isRegistered() {
        return m_isRegistered;
    }

    public void setRegistered(boolean isRegistered) {
        m_isRegistered = isRegistered;
    }

    public EventLocation getLocation() {
        return m_location;
    }

    public void setLocation(EventLocation location) {
        m_location = location;
    }

    public Genders getGenders() {
        return m_genders;
    }

    public void setGenders(Genders genders) {
        m_genders = genders;
    }

    public enum RegistrationVisibility {
        ALL, FRIENDS_AND_KNOWN, FRIENDS, KNOWN, NONE
    }

    public enum Genders {
        ALL(R.string.All),
        MEN_ONLY(R.string.MenOnly),
        WOMEN_ONLY(R.string.WomenOnly),
        MOSTLY_MEN(R.string.MostlyMen),
        MOSTLY_WOMEN(R.string.MostlyWomen);

        @StringRes
        public final int resourceId;

        Genders(@StringRes int resourceId) {
            this.resourceId = resourceId;
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "m_eventId=" + m_eventId +
                ", m_private=" + m_private +
                ", m_preliminary=" + m_preliminary +
                ", m_isRegistered=" + m_isRegistered +
                ", m_registrationVisibility=" + m_registrationVisibility +
                ", m_eventName='" + m_eventName + '\'' +
                ", m_start=" + m_start +
                ", m_end=" + m_end +
                ", m_minAge=" + m_minAge +
                ", m_maxAge=" + m_maxAge +
                ", m_registrations=" + m_registrations +
                '}';
    }
}
