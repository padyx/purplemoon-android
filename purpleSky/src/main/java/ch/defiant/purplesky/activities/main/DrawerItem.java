package ch.defiant.purplesky.activities.main;

import ch.defiant.purplesky.enums.NavigationDrawerEventType;

public class DrawerItem {

    public DrawerItem(int titleR, int iconR, NavigationDrawerEventType type) {
        titleRes = titleR;
        iconRes = iconR;
        eventType = type;
    }

    public final int iconRes;
    public final int titleRes;
    public final NavigationDrawerEventType eventType;
}