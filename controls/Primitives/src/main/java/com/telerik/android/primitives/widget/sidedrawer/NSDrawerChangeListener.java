package com.telerik.android.primitives.widget.sidedrawer;

public interface NSDrawerChangeListener {
    boolean onDrawerOpening(NSSideDrawer drawer);
    void onDrawerOpened(NSSideDrawer drawer);
    boolean onDrawerClosing(NSSideDrawer drawer);
    void onDrawerClosed(NSSideDrawer drawer);
}
