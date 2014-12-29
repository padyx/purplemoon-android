package ch.defiant.purplesky.beans.promotion;

import android.net.Uri;

/**
 * @author Patrick Bänziger
 */
public class PromotionPicture {

    private final int m_height;
    private final int m_width;
    private final Uri m_uri;

    public PromotionPicture(int height, int width, Uri uri) {
        m_height = height;
        m_width = width;
        m_uri = uri;
    }

    public int getHeight() {
        return m_height;
    }

    public int getWidth() {
        return m_width;
    }

    public Uri getUri() {
        return m_uri;
    }
}
