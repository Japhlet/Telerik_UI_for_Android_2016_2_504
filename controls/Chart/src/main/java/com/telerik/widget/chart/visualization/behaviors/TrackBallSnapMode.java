package com.telerik.widget.chart.visualization.behaviors;

/**
 * Defines how a {@link com.telerik.widget.chart.visualization.behaviors.ChartTrackBallBehavior} or a
 * {@link ChartPopupBehavior} instance should snap to the closest to a physical lastLocation data points.
 */
public enum TrackBallSnapMode {

    /**
     * The trackball will not be snapped to any of the closest data points.
     */
    NONE,

    /**
     * The behavior will snap to the closest data point, regardless of the chart series that own it.
     */
    CLOSEST_POINT,

    /**
     * The behavior will snap to the closest data points from all chart series.
     */
    ALL_CLOSE_POINTS
}
