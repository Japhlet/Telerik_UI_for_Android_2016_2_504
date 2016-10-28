package com.telerik.widget.chart.visualization.behaviors.animations;

import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.animation.Interpolator;

import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;
import com.telerik.widget.chart.visualization.common.ChartSeries;

public interface ChartAnimation {
    ViewPropertyAnimatorCompat start(SeriesAnimationView viewToAnimate);
    void setInitialValues(SeriesAnimationView view);
    void setDuration(long value);
    long getDuration();
    void setInterpolator(Interpolator value);
    Interpolator getInterpolator();
    void setInitialDelay(long value);
    long getInitialDelay();
    ChartSeries getSeries();
    void addAnimationFinishedListener(ChartAnimationFinishedListener listener);
    void removeAnimationFinishedListener(ChartAnimationFinishedListener listener);
}
