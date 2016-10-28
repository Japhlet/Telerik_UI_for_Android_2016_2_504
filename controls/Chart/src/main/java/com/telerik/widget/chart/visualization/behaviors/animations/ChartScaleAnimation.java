package com.telerik.widget.chart.visualization.behaviors.animations;

import android.support.v4.view.*;
import android.support.v4.view.ViewPropertyAnimatorCompat;

import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;
import com.telerik.widget.chart.visualization.common.ChartSeries;

public class ChartScaleAnimation extends ChartAnimationBase {
    private float pivotX = 0.0f;
    private float pivotY = 1.0f;
    private float startScaleY = 0.0f;
    private float startScaleX = 1.0f;

    public ChartScaleAnimation() {
        super();
    }

    public ChartScaleAnimation(ChartSeries series) {
        super(series);
    }

    public void setStartScaleX(float value) {
        startScaleX = value;
    }

    public float getStartScaleX() {
        return startScaleX;
    }

    public void setStartScaleY(float value) {
        startScaleY = value;
    }

    public float getStartScaleY() {
        return startScaleY;
    }

    public void setPivotX(float value) {
        pivotX = value;
    }

    public float getPivotX() {
        return pivotX;
    }

    public void setPivotY(float value) {
        pivotY = value;
    }

    public float getPivotY() {
        return pivotY;
    }

    @Override
    protected void animateViewCore(ViewPropertyAnimatorCompat animator) {
        animator.scaleY(1.0f).scaleX(1.0f);
    }

    @Override
    public void setInitialValues(SeriesAnimationView view) {
        ViewCompat.setScaleY(view, startScaleY);
        ViewCompat.setScaleX(view, startScaleX);
        ViewCompat.setPivotX(view, view.getMeasuredWidth() * pivotX);
        ViewCompatPivotYFix.setPivotY(view, view.getMeasuredHeight() * pivotY);
    }
}
