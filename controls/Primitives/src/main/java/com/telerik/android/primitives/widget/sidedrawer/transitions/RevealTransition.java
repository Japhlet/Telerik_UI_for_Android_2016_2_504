package com.telerik.android.primitives.widget.sidedrawer.transitions;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class RevealTransition extends DrawerTransitionBase {
    public RevealTransition() {
        super();
    }

    @Override
    protected void animateOpenLeft(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.animate(mainContent).translationX(drawerContent.getMeasuredWidth()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseLeft(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).translationX(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenRight(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        ViewCompat.animate(mainContent).translationX(-drawerContent.getMeasuredWidth()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseRight(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).translationX(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenTop(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        ViewCompat.animate(mainContent).translationY(drawerContent.getMeasuredHeight()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseTop(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).translationY(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenBottom(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        ViewCompat.animate(mainContent).translationY(-drawerContent.getMeasuredHeight()).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseBottom(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).translationY(0).withEndAction(this).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void setProgressLeft(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationX(mainContent, drawerContent.getMeasuredWidth() * value);
    }

    @Override
    protected void setProgressRight(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationX(mainContent, -drawerContent.getMeasuredWidth() * value);
    }

    @Override
    protected void setProgressTop(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationY(mainContent, drawerContent.getMeasuredHeight() * value);
    }

    @Override
    protected void setProgressBottom(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        ViewCompat.setTranslationY(mainContent, -drawerContent.getMeasuredHeight() * value);
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
        return "Reveal";
    }

    @Override
    protected View getFrontView() {
        return this.getMainContent();
    }
}
