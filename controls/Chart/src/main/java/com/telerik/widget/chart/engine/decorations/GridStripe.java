package com.telerik.widget.chart.engine.decorations;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisTickModel;

/**
 * This class represents a grid stripe.
 */
public class GridStripe {

    /**
     * The layout rectangle that should be painted by the visualization.
     */
    public RadRect fillRect;

    /**
     * The start tick of the stripe.
     */
    public AxisTickModel startTick;

    /**
     * The end tick of the stripe.
     */
    public AxisTickModel endTick;
}

