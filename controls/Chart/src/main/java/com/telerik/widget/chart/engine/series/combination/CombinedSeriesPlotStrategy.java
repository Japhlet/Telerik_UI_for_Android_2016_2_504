package com.telerik.widget.chart.engine.series.combination;

/**
 * Base class for {@link CombinedSeries} plot algorithms.
 */
public abstract class CombinedSeriesPlotStrategy {

    /**
     * Creates a new instance of the {@link CombinedSeriesPlotStrategy} class.
     */
    public CombinedSeriesPlotStrategy() {
    }

    /**
     * Plots the given {@link CombinedSeries}.
     *
     * @param series              The combined series.
     * @param combinedSeriesCount The combined series count.
     */
    public abstract void plot(CombinedSeries series, int combinedSeriesCount);
}
