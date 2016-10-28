package com.telerik.widget.chart.engine.series.combination;

import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;

/**
 * Base class for {@link CombinedSeries} layout rounding algorithms.
 */
public abstract class CombinedSeriesRoundLayoutStrategy {

    /**
     * Creates a new instance of the {@link CombinedSeriesRoundLayoutStrategy} class.
     */
    public CombinedSeriesRoundLayoutStrategy() {
    }

    /**
     * Applies layout rounding to the provided {@link CombinedSeries}
     *
     * @param chart  The {@link ChartAreaModel} of the series.
     * @param series The combined series on which layout rounding will be applied..
     */
    public abstract void applyLayoutRounding(ChartAreaModel chart, CombinedSeries series);
}

