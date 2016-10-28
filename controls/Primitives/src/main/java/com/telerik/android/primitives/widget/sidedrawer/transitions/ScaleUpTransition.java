package com.telerik.android.primitives.widget.sidedrawer.transitions;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class ScaleUpTransition extends DrawerTransitionBase {
    public ScaleUpTransition() {
        super();
    }

    @Override
    protected void animateOpenLeft(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        int drawerWidth = drawerContent.getMeasuredWidth();

        ViewCompat.animate(mainContent).translationX(drawerWidth).setInterpolator(this.getInterpolator());

        setScale(drawerContent);
        ViewCompat.animate(drawerContent).scaleX(1).scaleY(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseLeft(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.animate(drawerContent).scaleX(0.7f).scaleY(0.7f).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenRight(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerWidth = drawerContent.getMeasuredWidth();

        ViewCompat.animate(mainContent).translationX(-drawerWidth).setInterpolator(this.getInterpolator());

        setScale(drawerContent);
        ViewCompat.animate(drawerContent).scaleX(1).scaleY(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseRight(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.animate(drawerContent).scaleX(0.7f).scaleY(0.7f).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenTop(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerHeight = drawerContent.getMeasuredHeight();

        ViewCompat.animate(mainContent).translationY(drawerHeight).setInterpolator(this.getInterpolator());

        setScale(drawerContent);
        ViewCompat.animate(drawerContent).scaleX(1).scaleY(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseTop(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.animate(drawerContent).scaleX(0.7f).scaleY(0.7f).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenBottom(View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerHeight = drawerContent.getMeasuredHeight();

        ViewCompat.animate(mainContent).translationY(-drawerHeight).setInterpolator(this.getInterpolator());

        setScale(drawerContent);
        ViewCompat.animate(drawerContent).scaleX(1).scaleY(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseBottom(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());
        ViewCompat.animate(drawerContent).scaleX(0.7f).scaleY(0.7f).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    protected void setScale(View drawerContent) {
        ViewCompat.setScaleX(drawerContent, 0.7f + (0.3f * this.getProgress()));
        ViewCompat.setScaleY(drawerContent, 0.7f + (0.3f * this.getProgress()));
    }

    @Override
    protected void setProgressLeft(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        int drawerWidth = drawerContent.getMeasuredWidth();

        ViewCompat.setTranslationX(mainContent, drawerWidth * value);

        ViewCompat.setScaleX(drawerContent, 0.7f + 0.3f * value);
        ViewCompat.setScaleY(drawerContent, 0.7f + 0.3f * value);
    }

    @Override
    protected void setProgressRight(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        fadeLayer.bringToFront();
        int drawerWidth = drawerContent.getMeasuredWidth();

        ViewCompat.setTranslationX(mainContent, -drawerWidth * value);

        ViewCompat.setScaleX(drawerContent, 0.7f + 0.3f * value);
        ViewCompat.setScaleY(drawerContent, 0.7f + 0.3f * value);
    }

    @Override
    protected void setProgressTop(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerHeight = drawerContent.getMeasuredHeight();

        ViewCompat.setTranslationY(mainContent, drawerHeight * value);

        ViewCompat.setScaleX(drawerContent, 0.7f + 0.3f * value);
        ViewCompat.setScaleY(drawerContent, 0.7f + 0.3f * value);
    }

    @Override
    protected void setProgressBottom(float value, View mainContent, View drawerContent, View fadeLayer) {
        mainContent.bringToFront();
        int drawerHeight = drawerContent.getMeasuredHeight();

        ViewCompat.setTranslationY(mainContent, -drawerHeight * value);

        ViewCompat.setScaleX(drawerContent, 0.7f + 0.3f * value);
        ViewCompat.setScaleY(drawerContent, 0.7f + 0.3f * value);
    }

    @Override
    protected void clearCore(View drawerContent, View mainContent) {
        if(mainContent != null) {
            ViewCompat.setTranslationY(mainContent, 0);
            ViewCompat.setTranslationX(mainContent, 0);
        }

        if(drawerContent != null) {
            ViewCompat.setScaleX(drawerContent, 1);
            ViewCompat.setScaleY(drawerContent, 1);
        }
    }

    @Override
    public String toString() {
        return "ScaleUp";
    }

    @Override
    protected View getFrontView() {
        return this.getMainContent();
    }
}
