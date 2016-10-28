package com.telerik.widget.chart.engine.axes.common;

/**
 * Defines how data points are plotted by an axis.
 */
public enum AxisPlotMode {

    /**
     * points are plotted in the middle of the range, defined between each two ticks.
     */
    BETWEEN_TICKS,

    /**
     * points are plotted over each tick.
     */
    ON_TICKS,

    /**
     * points are plotted over each tick with half a step padding applied on both ends of the axis.
     */
    ON_TICKS_PADDED
}

