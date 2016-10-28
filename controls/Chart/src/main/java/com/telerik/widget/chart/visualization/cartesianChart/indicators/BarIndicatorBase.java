package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all bar indicators.
 */
public abstract class BarIndicatorBase extends ValueIndicatorBase {

    private List<View> realizedDataPoints;

    /**
     * Initializes a new instance of the {@link BarIndicatorBase} class.
     */
    public BarIndicatorBase() {
        this.realizedDataPoints = new ArrayList<View>();
    }

    /**
     * Gets the current collection of realized data points.
     *
     * @return the current collection of data points.
     */
    public List<View> realizedDataPoints() {
        return this.realizedDataPoints;
    }
}
