package com.telerik.android.primitives.widget.sidedrawer;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class NSSideDrawerLayout extends FrameLayout {
    private NSSideDrawer drawer;

    public NSSideDrawerLayout(Context context, NSSideDrawer drawer) {
        super(context);

        this.drawer = drawer;
        this.setClipToPadding(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.drawer.onGesture(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.drawer.onGesture(event) || super.onInterceptTouchEvent(event);
    }
}
