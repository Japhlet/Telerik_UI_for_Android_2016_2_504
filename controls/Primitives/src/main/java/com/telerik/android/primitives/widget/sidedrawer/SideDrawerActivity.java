package com.telerik.android.primitives.widget.sidedrawer;

import android.app.Activity;

public class SideDrawerActivity extends Activity {
    private RadSideDrawer drawer;
    private boolean closeDrawerOnBackButton = true;

    public SideDrawerActivity() {
        super();
    }

    public void setDrawer(RadSideDrawer value) {
        this.drawer = value;
    }

    public RadSideDrawer getDrawer() {
        return this.drawer;
    }

    public void setCloseDrawerOnBackButton(boolean value) {
        this.closeDrawerOnBackButton = value;
    }

    public boolean getCloseDrawerOnBackButton() {
        return this.closeDrawerOnBackButton;
    }

    @Override
    public void onBackPressed() {
        if(this.drawer == null) {
            super.onBackPressed();
            return;
        }

        if(this.getCloseDrawerOnBackButton() && this.drawer.getIsOpen()) {
            this.drawer.setIsOpen(false);
            return;
        }

        super.onBackPressed();
    }
}
