package ch.defiant.purplesky.activities.common;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import ch.defiant.purplesky.enums.NavigationDrawerEventType;

public class DrawerItem {

    public DrawerItem(
            @StringRes int titleR,
            @DrawableRes int iconR,
            NavigationDrawerEventType type,
            boolean selected,
            @DrawableRes int notificationCountBackgroundResId) {
        titleRes = titleR;
        iconRes = iconR;
        eventType = type;
        isSelected = selected;
        countBrackgroundResId = notificationCountBackgroundResId;
    }

    @DrawableRes
    public final int iconRes;
    @StringRes
    public final int titleRes;
    public final NavigationDrawerEventType eventType;
    public final boolean isSelected;

    @DrawableRes
    public final int countBrackgroundResId;
}