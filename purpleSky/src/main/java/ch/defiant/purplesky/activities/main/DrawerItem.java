package ch.defiant.purplesky.activities.main;

import ch.defiant.purplesky.enums.NavigationDrawerEventType;

class DrawerItem {

    public DrawerItem(int titleR, int iconR, NavigationDrawerEventType type) {
        titleRes = titleR;
        iconRes = iconR;
        eventType = type;
    }

    int iconRes;
    int titleRes;
    NavigationDrawerEventType eventType;
}