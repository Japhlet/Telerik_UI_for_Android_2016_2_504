package com.telerik.widget.chart.engine.axes;

/**
 * Enlists the possible axis types.
 */
public enum AxisType {

    /**
     * Defines the first axis. This value is coordinate-system specific.
     * It represents the x (or the HORIZONTAL) axis for a Cartesian coordinate system and the Value (or the radius) axis for a Polar coordinate system.
     */
    FIRST,

    /**
     * Defines the second axis. This value instanceof coordinate-system specific.
     * It represents the y (or VERTICAL) axis for a Cartesian coordinate system and the angle axis for a Polar coordinate system.
     */
    SECOND
}

