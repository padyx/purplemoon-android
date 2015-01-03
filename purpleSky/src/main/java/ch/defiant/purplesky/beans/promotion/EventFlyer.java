package ch.defiant.purplesky.beans.promotion;

import android.net.Uri;

/**
 * @author Patrick Bänziger
 */
public class EventFlyer {

    private final int m_flyerId;
    private final Uri m_pictureBaseUri;
    private final int m_maxWidth;
    private final int m_maxHeight;

    public EventFlyer(int flyerId, Uri pictureBaseUri, int maxWidth, int maxHeight){
        m_flyerId = flyerId;
        m_pictureBaseUri = pictureBaseUri;
        m_maxWidth = maxWidth;
        m_maxHeight = maxHeight;
    }

    public int getMaxHeight() {
        return m_maxHeight;
    }

    public int getFlyerId() {
        return m_flyerId;
    }

    public Uri getPictureBaseUri() {
        return m_pictureBaseUri;
    }

    public int getMaxWidth() {
        return m_maxWidth;
    }

}
