package com.telerik.widget.chart.visualization.behaviors.animations;

import com.telerik.widget.chart.visualization.behaviors.views.SeriesAnimationView;

public interface ChartAnimationFinishedListener {
    void onAnimationFinished(ChartAnimation animation, SeriesAnimationView animatedView);
}
