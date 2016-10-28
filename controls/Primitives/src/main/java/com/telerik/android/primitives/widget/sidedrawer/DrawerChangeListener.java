package com.telerik.android.primitives.widget.sidedrawer;

public interface DrawerChangeListener {
    boolean onDrawerOpening(RadSideDrawer drawer);
    void onDrawerOpened(RadSideDrawer drawer);
    boolean onDrawerClosing(RadSideDrawer drawer);
    void onDrawerClosed(RadSideDrawer drawer);
}
