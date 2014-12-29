package ch.defiant.purplesky.beans.promotion;

import android.net.Uri;

import java.util.Date;
import java.util.List;

/**
 * @author Patrick Bänziger
 */
public class Promotion {

    private final int m_id;

    private final String m_title;
    private final String m_text;

    private final List<PromotionPicture> m_promotionPictures;
    private final Uri m_eventUri;
    private final  int m_eventId;

    private final Date m_validFrom;
    private final Date m_validTo;

    private final int m_importance;

    public Promotion(int id, String title, String text, List<PromotionPicture> promotionPictures, Uri eventUri, Date validFrom, Date validTo, int eventId, int importance){
        m_id=id;
        m_title=title;
        m_text=text;
        m_promotionPictures = promotionPictures;
        m_eventUri=eventUri;
        m_validFrom=validFrom;
        m_validTo=validTo;
        m_importance=importance;
        m_eventId=eventId;
    }

    public String getTitle() {
        return m_title;
    }

    public String getText() {
        return m_text;
    }

    public List<PromotionPicture> getPromotionPictures() {
        return m_promotionPictures;
    }

    public Uri getEventUri() {
        return m_eventUri;
    }

    public int getEventId() {
        return m_eventId;
    }

    public Date getValidFrom() {
        return m_validFrom;
    }

    public Date getValidTo() {
        return m_validTo;
    }

    public int getImportance() {
        return m_importance;
    }

    public int getId() {
        return m_id;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "m_id=" + m_id +
                ", m_title='" + m_title + '\'' +
                ", m_text='" + m_text + '\'' +
                ", m_promotionPictures=" + m_promotionPictures +
                ", m_eventUri=" + m_eventUri +
                ", m_eventId=" + m_eventId +
                ", m_validFrom=" + m_validFrom +
                ", m_validTo=" + m_validTo +
                ", m_importance=" + m_importance +
                '}';
    }
}
