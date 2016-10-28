package com.telerik.android.primitives.widget.sidedrawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.telerik.android.primitives.R;

public class SideDrawerToggle implements DrawerChangeListener {
    Toolbar toolbar;
    ActionBar actionBar;
    RadSideDrawer drawer;
    Drawable closeIcon;
    Drawable openIcon;
    View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleDrawer();
        }
    };

    public SideDrawerToggle(RadSideDrawer drawer, Toolbar toolbar) {
        if(drawer == null) {
            throw new IllegalArgumentException("drawer cannot be null.");
        }

        this.drawer = drawer;

        this.openIcon = this.closeIcon = drawer.getResources().getDrawable(R.drawable.hamburger);

        if(toolbar == null) {
            this.actionBar = getActionBar(drawer);
            this.actionBar.setDisplayHomeAsUpEnabled(true);
            this.actionBar.setHomeAsUpIndicator(this.openIcon);
        } else {
            this.toolbar = toolbar;
            this.toolbar.setNavigationOnClickListener(navigationClickListener);
            this.toolbar.setNavigationIcon(this.openIcon);
        }
    }

    public SideDrawerToggle(RadSideDrawer drawer) {
        this(drawer, null);
    }

    public void setDrawerOpenIcon(int resId) {
        this.setDrawerOpenIcon(drawer.getResources().getDrawable(resId));
    }

    public void setDrawerOpenIcon(Drawable drawable) {
        if(!drawer.getIsOpen()) {
            this.updateActionBarIcon(drawable);
        }

        this.openIcon = drawable;
    }

    public void setDrawerCloseIcon(Drawable drawable) {
        if(drawer.getIsOpen()) {
            this.updateActionBarIcon(drawable);
        }

        this.closeIcon = drawable;
    }

    private void updateActionBarIcon(Drawable drawable) {
        if(this.toolbar != null) {
            this.toolbar.setNavigationIcon(drawable);
        } else  if(this.actionBar != null) {
            this.actionBar.setHomeAsUpIndicator(drawable);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            toggleDrawer();
            return true;
        }
        return false;
    }

    protected void toggleDrawer() {
        if(this.drawer.getIsOpen()) {
            this.drawer.setIsOpen(false);
        } else {
            this.drawer.setIsOpen(true);
        }
    }

    private ActionBar getActionBar(View view) {
        Context viewContext = view.getContext();

        if(!(viewContext instanceof ActionBarActivity)) {
            throw new IllegalArgumentException("SideDrawerToggle can only be used with ActionBarActivity or Toolbar.");
        }

        ActionBarActivity actionBarActivity = (ActionBarActivity)viewContext;

        return actionBarActivity.getSupportActionBar();
    }

    @Override
    public boolean onDrawerOpening(RadSideDrawer drawer) {
        return false;
    }

    @Override
    public void onDrawerOpened(RadSideDrawer drawer) {
        this.updateActionBarIcon(this.closeIcon);
    }

    @Override
    public boolean onDrawerClosing(RadSideDrawer drawer) {
        return false;
    }

    @Override
    public void onDrawerClosed(RadSideDrawer drawer) {
        this.updateActionBarIcon(this.openIcon);
    }
}
