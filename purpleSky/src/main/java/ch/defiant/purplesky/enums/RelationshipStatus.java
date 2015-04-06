package ch.defiant.purplesky.enums;

import android.support.annotation.StringRes;

import ch.defiant.purplesky.R;

/**
 * Created by Chakotay on 04.04.2015.
 */
public enum RelationshipStatus {
    SINGLE(R.string.RelationshipSingle),
    LONGTERM_RELATIONSHIP(R.string.RelationshipLongTerm),
    ENGANGED(R.string.RelationshipEngaged),
    MARRIED(R.string.RelationshipMarried),
    OPEN_RELATIONSHIP(R.string.RelationshipOpen);

    private final int m_stringRes;

    RelationshipStatus(@StringRes int stringRes) {
        m_stringRes = stringRes;
    }

    @StringRes
    public int getStringResource() {
        return m_stringRes;
    }
}
