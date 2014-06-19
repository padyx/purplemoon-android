package ch.defiant.purplesky.enums;

import ch.defiant.purplesky.R;

public enum PhotoVoteVerdict {
    NEUTRAL_NEGATIVE(R.string.PhotoVote0_NeutralNegative, R.drawable.vote0),
    CUTE_ATTRACTIVE(R.string.PhotoVote1_Cute, R.drawable.vote1),
    VERY_ATTRACTIVE(R.string.PhotoVote2_VeryCute, R.drawable.vote2),
    STUNNING(R.string.PhotoVote3_Stunning, R.drawable.vote3);

    private PhotoVoteVerdict(int resourceId, int iconId) {
        m_resourceId = resourceId;
        m_iconId = iconId;
    }

    public int getResourceId() {
        return m_resourceId;
    }

    public int getIconId() {
        return m_iconId;
    }

    private final int m_iconId;
    private final int m_resourceId;

}