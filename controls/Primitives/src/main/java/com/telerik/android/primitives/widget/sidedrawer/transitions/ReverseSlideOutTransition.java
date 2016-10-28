package com.telerik.android.primitives.widget.sidedrawer.transitions;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class ReverseSlideOutTransition extends DrawerTransitionBase {
    public ReverseSlideOutTransition() {
        super();
    }

    @Override
    protected void animateOpenLeft(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.setTranslationX(drawerContent, drawerWidth * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.animate(mainContent).translationX(drawerWidth).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseLeft(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(drawerContent).translationX(drawerContent.getMeasuredWidth()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationX(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenRight(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.setTranslationX(drawerContent, -drawerWidth * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.animate(mainContent).translationX(-drawerWidth).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseRight(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(drawerContent).translationX(-drawerContent.getMeasuredWidth()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationX(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenTop(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.setTranslationY(drawerContent, drawerHeight * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.animate(mainContent).translationY(drawerHeight).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseTop(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(drawerContent).translationY(drawerContent.getMeasuredHeight()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationY(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenBottom(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.setTranslationY(drawerContent, -drawerHeight * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.animate(mainContent).translationY(-drawerHeight).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseBottom(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(drawerContent).translationY(-drawerContent.getMeasuredHeight()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(mainContent).translationY(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void setProgressLeft(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();

        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.setTranslationX(drawerContent, drawerWidth * (1 - value));
        ViewCompat.setTranslationX(mainContent, drawerWidth * value);
    }

    @Override
    protected void setProgressRight(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.setTranslationX(drawerContent, drawerWidth * (value - 1));
        ViewCompat.setTranslationX(mainContent, -drawerWidth * value);
    }

    @Override
    protected void setProgressTop(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.setTranslationY(drawerContent, drawerHeight * (1 - value));
        ViewCompat.setTranslationY(mainContent, drawerHeight * value);
    }

    @Override
    protected void setProgressBottom(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.setTranslationY(drawerContent, drawerHeight * (value - 1));
        ViewCompat.setTranslationY(mainContent, -drawerHeight * value);
    }

    @Override
    protected void clearCore(View drawerContent, View mainContent) {
        if(drawerContent != null) {
            ViewCompat.setTranslationY(drawerContent, 0);
            ViewCompat.setTranslationX(drawerContent, 0);
        }

        if(mainContent != null) {
            ViewCompat.setTranslationY(mainContent, 0);
            ViewCompat.setTranslationX(mainContent, 0);
        }
    }

    @Override
    public String toString() {
        return "ReverseSlideOut";
    }

    @Override
    protected View getFrontView() {
        return this.getMainContent();
    }
}