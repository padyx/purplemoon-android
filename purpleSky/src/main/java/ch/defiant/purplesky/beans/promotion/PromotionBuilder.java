package ch.defiant.purplesky.beans.promotion;

import android.net.Uri;

import java.util.Date;

public class PromotionBuilder {
    private int m_id;
    private String m_title;
    private String m_text;
    private Uri m_pictureUri = null;
    private Uri m_eventUri;
    private Date m_validFrom;
    private Date m_validTo = null;
    private int m_eventId;
    private int m_importance;

    public PromotionBuilder setId(int id) {
        m_id = id;
        return this;
    }

    public PromotionBuilder setTitle(String title) {
        m_title = title;
        return this;
    }

    public PromotionBuilder setText(String text) {
        m_text = text;
        return this;
    }

    public PromotionBuilder setPictureUri(Uri pictureUri) {
        m_pictureUri = pictureUri;
        return this;
    }

    public PromotionBuilder setEventUri(Uri eventUri) {
        m_eventUri = eventUri;
        return this;
    }

    public PromotionBuilder setValidFrom(Date validFrom) {
        m_validFrom = validFrom;
        return this;
    }

    public PromotionBuilder setValidTo(Date validTo) {
        m_validTo = validTo;
        return this;
    }

    public PromotionBuilder setEventId(int eventId) {
        m_eventId = eventId;
        return this;
    }

    public PromotionBuilder setImportance(int importance) {
        m_importance = importance;
        return this;
    }

    public Promotion build() {
        return new Promotion(m_id, m_title, m_text, m_pictureUri, m_eventUri, m_validFrom, m_validTo, m_eventId, m_importance);
    }
}