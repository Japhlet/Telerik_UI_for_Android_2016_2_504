package com.telerik.widget.chart.visualization.behaviors;

/**
 * States in which directions is the zoom being handled.
 */
public class ChartPanZoomMode {
    /**
     * A zoom gesture is not handled.
     */
    public static final int NONE = 0,

    /**
     * The chart is zoomed horizontally (along the x-axis).
     */
    HORIZONTAL = 1,

    /**
     * The chart is zoomed vertically (along the y-axis).
     */
    VERTICAL = HORIZONTAL << 1,

    /**
     * BOTH HORIZONTAL and VERTICAL flags are valid.
     */
    BOTH = HORIZONTAL | VERTICAL;
}
