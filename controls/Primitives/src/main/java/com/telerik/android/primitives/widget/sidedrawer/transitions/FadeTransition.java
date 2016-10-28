package com.telerik.android.primitives.widget.sidedrawer.transitions;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class FadeTransition extends DrawerTransitionBase {
    public FadeTransition() {
        this.setDuration(150);
        this.setInterpolator(null);
    }

    @Override
    protected void animateOpenLeft(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(drawerContent).alpha(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateOpenRight(View mainContent, View drawerContent, View fadeLayer) {
        animateOpenLeft(mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void animateOpenTop(View mainContent, View drawerContent, View fadeLayer) {
        animateOpenLeft(mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void animateOpenBottom(View mainContent, View drawerContent, View fadeLayer) {
        animateOpenLeft(mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void animateCloseLeft(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(drawerContent).alpha(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseRight(View mainContent, View drawerContent, View fadeLayer) {
        animateCloseLeft(mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void animateCloseTop(View mainContent, View drawerContent, View fadeLayer) {
        animateCloseLeft(mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void animateCloseBottom(View mainContent, View drawerContent, View fadeLayer) {
        animateCloseLeft(mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void setProgressLeft(float value, View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.setAlpha(drawerContent, value);
    }

    @Override
    protected void setProgressRight(float value, View mainContent, View drawerContent, View fadeLayer) {
        setProgressLeft(value, mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void setProgressTop(float value, View mainContent, View drawerContent, View fadeLayer) {
        setProgressLeft(value, mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void setProgressBottom(float value, View mainContent, View drawerContent, View fadeLayer) {
        setProgressLeft(value, mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void clearCore(View drawerContent, View mainContent) {
        if(drawerContent != null) {
            ViewCompat.setAlpha(drawerContent, 1);
        }

        if(mainContent != null) {
            ViewCompat.setAlpha(mainContent, 1);
        }
    }
}
