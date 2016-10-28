package com.telerik.widget.chart.visualization.behaviors.animations;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;

import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;
import com.telerik.widget.chart.visualization.common.ChartSeries;

public class ChartTranslateAnimation extends ChartAnimationBase {
    private float startX;
    private float startY;

    public ChartTranslateAnimation() {
        super();
    }

    public ChartTranslateAnimation(ChartSeries series) {
        super(series);
    }

    public void setStartX(int value) {
        startX = value;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartY(int value) {
        startY = value;
    }

    public float getStartY() {
        return startY;
    }

    @Override
    protected void animateViewCore(ViewPropertyAnimatorCompat animator) {
        animator.translationY(0.0f).translationX(0.0f);
    }

    @Override
    public void setInitialValues(SeriesAnimationView view) {
        ViewCompat.setTranslationX(view, startX);
        ViewCompat.setTranslationY(view, startY);
    }
}
