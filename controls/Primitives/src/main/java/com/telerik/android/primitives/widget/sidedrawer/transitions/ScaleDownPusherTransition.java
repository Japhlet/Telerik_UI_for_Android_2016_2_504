package com.telerik.android.primitives.widget.sidedrawer.transitions;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class ScaleDownPusherTransition extends DrawerTransitionBase {
    public ScaleDownPusherTransition() {
        super();
    }

    @Override
    protected void animateOpenLeft(View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();

        ViewCompat.animate(mainContent).scaleX(0.8f).scaleY(0.8f).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.setTranslationX(drawerContent, -drawerWidth * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseLeft(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).scaleX(1).scaleY(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.animate(drawerContent).translationX(-drawerWidth).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenRight(View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();

        ViewCompat.animate(mainContent).scaleX(0.8f).scaleY(0.8f).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.setTranslationX(drawerContent, drawerWidth * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationX(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseRight(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).scaleX(1).scaleY(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        int drawerWidth = drawerContent.getMeasuredWidth();
        ViewCompat.animate(drawerContent).translationX(drawerWidth).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenTop(View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();

        ViewCompat.animate(mainContent).scaleX(0.8f).scaleY(0.8f).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.setTranslationY(drawerContent, -drawerHeight * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseTop(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).scaleX(1).scaleY(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.animate(drawerContent).translationY(-drawerHeight).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void animateOpenBottom(View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();

        ViewCompat.animate(mainContent).scaleX(0.8f).scaleY(0.8f).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        ViewCompat.setTranslationY(drawerContent, drawerHeight * (1 - this.getProgress()));
        ViewCompat.animate(drawerContent).translationY(0).setDuration(this.getDuration()).setInterpolator(this.getInterpolator()).withEndAction(this);
    }

    @Override
    protected void animateCloseBottom(View mainContent, View drawerContent, View fadeLayer) {
        ViewCompat.animate(mainContent).scaleX(1).scaleY(1).setDuration(this.getDuration()).setInterpolator(this.getInterpolator());

        int drawerHeight = drawerContent.getMeasuredHeight();
        ViewCompat.animate(drawerContent).translationY(drawerHeight).setDuration(this.getDuration()).withEndAction(this).setInterpolator(this.getInterpolator());
    }

    @Override
    protected void setProgressLeft(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();

        setScaleProgress(value, mainContent);

        ViewCompat.setTranslationX(drawerContent, -drawerWidth * (1 - value));
    }

    @Override
    protected void setProgressRight(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerWidth = drawerContent.getMeasuredWidth();

        setScaleProgress(value, mainContent);

        ViewCompat.setTranslationX(drawerContent, drawerWidth * (1 - value));
    }

    @Override
    protected void setProgressTop(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();

        setScaleProgress(value, mainContent);

        ViewCompat.setTranslationY(drawerContent, -drawerHeight * (1 - value));
    }

    @Override
    protected void setProgressBottom(float value, View mainContent, View drawerContent, View fadeLayer) {
        int drawerHeight = drawerContent.getMeasuredHeight();

        setScaleProgress(value, mainContent);

        ViewCompat.setTranslationY(drawerContent, drawerHeight * (1 - value));
    }

    protected void setScaleProgress(float value, View mainContent) {
        ViewCompat.setScaleX(mainContent, 1.0f - (0.2f * value));
        ViewCompat.setScaleY(mainContent, 1.0f - (0.2f * value));
    }

    @Override
    protected void clearCore(View drawerContent, View mainContent) {
        if(drawerContent != null) {
            ViewCompat.setTranslationY(drawerContent, 0);
            ViewCompat.setTranslationX(drawerContent, 0);
        }

        if(mainContent != null) {
            ViewCompat.setTranslationX(mainContent, 0);
            ViewCompat.setTranslationY(mainContent, 0);
            ViewCompat.setScaleX(mainContent, 1);
            ViewCompat.setScaleY(mainContent, 1);
        }
    }

    @Override
    public String toString() {
        return "ScaleDownPusher";
    }
}
