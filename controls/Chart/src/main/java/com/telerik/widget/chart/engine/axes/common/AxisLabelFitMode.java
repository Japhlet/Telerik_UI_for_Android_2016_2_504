package com.telerik.widget.chart.engine.axes.common;

/**
 * Determines the label fit mode of the widget axis labels.
 */
public enum AxisLabelFitMode {

    /**
     * Does not attempt to fit the axis labels.
     */
    NONE,

    /**
     * Arranges axis labels on multiple lines with each label on a different line than
     * its neighbor labels.
     */
    MULTI_LINE,

    /**
     * Arranges the axis labels so that they are rotated some degrees around their top left corner.
     */
    ROTATE,
}

