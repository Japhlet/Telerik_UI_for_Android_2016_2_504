package com.telerik.widget.chart.engine.axes.common;

/**
 * Defines the strategy that will update the last label of the axis.
 */
public enum AxisLastLabelVisibility {

    /**
     * The desired space is reserved so that the label is fully visible.
     */
    VISIBLE,

    /**
     * The last label is not displayed.
     */
    HIDDEN,

    /**
     * The last label is displayed but no space if reserved so that it is fully visible.
     */
    CLIP
}

