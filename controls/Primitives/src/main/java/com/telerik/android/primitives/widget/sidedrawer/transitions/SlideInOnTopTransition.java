package com.telerik.android.primitives.widget.sidedrawer.transitions;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class SlideInOnTopTransition extends DrawerTransitionBase {
    public SlideInOnTopTransition() {
        super();
    }

    @Override
    protected void animateOpenLeft(View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.setTranslationX(drawerContent, -drawerWidth * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseLeft(View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.animate(drawerContent).translationX(-drawerWidth).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenRight(View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.setTranslationX(drawerContent, drawerWidth * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseRight(View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.animate(drawerContent).translationX(drawerWidth).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenTop(View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.setTranslationY(drawerContent, -drawerHeight * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseTop(View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.animate(drawerContent).translationY(-drawerHeight).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenBottom(View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.setTranslationY(drawerContent, drawerHeight * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseBottom(View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.animate(drawerContent).translationY(drawerHeight).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void setProgressLeft(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.setTranslationX(drawerContent, drawerWidth * (value - 1));
    }

    @Override
    protected void setProgressRight(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.setTranslationX(drawerContent, drawerWidth * (1 - value));
    }

    @Override
    protected void setProgressTop(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.setTranslationY(drawerContent, drawerHeight * (value - 1));
    }

    @Override
    protected void setProgressBottom(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.setTranslationY(drawerContent, drawerHeight * (1 - value));
    }

    @Override
    protected void clearCore(View drawerContent, View mainContent) {
        if(drawerContent != null) {
            ViewCompat.setTranslationY(drawerContent, 0);
            ViewCompat.setTranslationX(drawerContent, 0);
        }
    }

    @Override
    public String toString() {
        return "SlideInOnTop";
    }
}
