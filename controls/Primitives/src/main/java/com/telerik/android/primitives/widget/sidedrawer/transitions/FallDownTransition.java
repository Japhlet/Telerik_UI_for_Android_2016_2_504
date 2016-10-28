package com.telerik.android.primitives.widget.sidedrawer.transitions;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class FallDownTransition extends DrawerTransitionBase {
    public FallDownTransition() {
        super();
    }

    @Override
    protected void animateOpenLeft(View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationY(drawerContent, -drawerHeight * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationX(drawerContent.getMeasuredWidth()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseLeft(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(drawerContent).translationY(-drawerContent.getMeasuredHeight()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationX(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).start();
    }

    @Override
    protected void animateOpenRight(View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        mainContent.bringToFront();
        ViewCompat.setTranslationY(drawerContent, -drawerHeight * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationX(-drawerContent.getMeasuredWidth()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseRight(View mainContent, View drawerContent, View fadeLayer) {
        this.animateCloseLeft(mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void animateOpenTop(View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        mainContent.bringToFront();
        ViewCompat.setTranslationX(drawerContent, -drawerWidth * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationY(drawerContent.getMeasuredHeight()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseTop(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(drawerContent).translationX(-drawerContent.getMeasuredWidth()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationY(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenBottom(View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        mainContent.bringToFront();
        ViewCompat.setTranslationX(drawerContent, -drawerWidth * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
        ViewCompat.animate(mainContent).translationY(-drawerContent.getMeasuredHeight()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateCloseBottom(View mainContent, View drawerContent, View fadeLayer) {
        this.animateCloseTop(mainContent, drawerContent, fadeLayer);
    }

    @Override
    protected void setProgressLeft(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationY(drawerContent, drawerHeight * (value - 1));
        ViewCompat.setTranslationX(mainContent, drawerContent.getMeasuredWidth() * value);
    }

    @Override
    protected void setProgressRight(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationY(drawerContent, -drawerHeight * (1 - value));
        ViewCompat.setTranslationX(mainContent, -drawerContent.getMeasuredWidth() * value);
    }

    @Override
    protected void setProgressTop(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationX(drawerContent, drawerWidth * (value - 1));
        ViewCompat.setTranslationY(mainContent, drawerContent.getMeasuredHeight() * value);
    }

    @Override
    protected void setProgressBottom(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationX(drawerContent, -drawerWidth * (1 - value));
        ViewCompat.setTranslationY(mainContent, -drawerContent.getMeasuredHeight() * value);
    }

    @Override
    protected void clearCore(View drawerContent, View mainContent) {
        if(drawerContent != null) {
            ViewCompat.setTranslationX(drawerContent, 0);
            ViewCompat.setTranslationY(drawerContent, 0);
        }

        if(mainContent != null) {
            ViewCompat.setTranslationX(mainContent, 0);
            ViewCompat.setTranslationY(mainContent, 0);
        }
    }

    @Override
    public String toString() {
        return "FallDown";
    }

    @Override
    protected View getFrontView() {
        return this.getMainContent();
    }
}
