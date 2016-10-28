package com.telerik.widget.chart.engine.decorations;

import com.telerik.android.common.math.RadPoint;
import com.telerik.widget.chart.engine.axes.AxisTickModel;

/**
 * This class represents a grid line in the chart grid.
 */
public class GridLine {

    /**
     * The tick associated with the grid line.
     */
    public AxisTickModel axisTickModel;

    /**
     * The start point of the line.
     */
    public RadPoint point1;

    /**
     * The end point of the line.
     */
    public RadPoint point2;
}

