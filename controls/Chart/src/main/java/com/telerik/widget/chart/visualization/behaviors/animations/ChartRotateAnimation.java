package com.telerik.widget.chart.visualization.behaviors.animations;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewCompatPivotYFix;
import android.support.v4.view.ViewPropertyAnimatorCompat;

import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;
import com.telerik.widget.chart.visualization.common.ChartSeries;

public class ChartRotateAnimation extends ChartAnimationBase {
    private float startAngle = 180.0f;
    private float pivotX = 0.5f;
    private float pivotY = 0.5f;

    public ChartRotateAnimation() {
        super();
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

    public ChartRotateAnimation(ChartSeries series) {
        super(series);
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float value) {
        startAngle = value;
    }

    @Override
    protected void animateViewCore(ViewPropertyAnimatorCompat animator) {
        animator.rotation(0);
    }

    @Override
    public void setInitialValues(SeriesAnimationView view) {
        ViewCompat.setPivotX(view, view.getMeasuredWidth() * pivotX);
        ViewCompatPivotYFix.setPivotY(view, view.getMeasuredHeight() * pivotY);
        ViewCompat.setRotation(view, startAngle);
    }
}
