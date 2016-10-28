package com.telerik.widget.chart.visualization.behaviors.animations;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;

import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;
import com.telerik.widget.chart.visualization.common.ChartSeries;

public class ChartFadeAnimation extends ChartAnimationBase {
    private float startOpacity = 0.0f;

    public ChartFadeAnimation() {
        super();
    }

    public ChartFadeAnimation(ChartSeries series) {
        super(series);

        this.setInterpolator(null);
    }

    @Override
    protected void animateViewCore(ViewPropertyAnimatorCompat animator) {
        animator.alpha(1.0f);
    }

    public void setStartOpacity(float value) {
        startOpacity = value;
    }

    public float getStartOpacity() {
        return startOpacity;
    }

    @Override
    public void setInitialValues(SeriesAnimationView view) {
        ViewCompat.setAlpha(view, startOpacity);
    }
}
