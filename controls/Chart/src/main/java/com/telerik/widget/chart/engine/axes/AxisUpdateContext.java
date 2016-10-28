package com.telerik.widget.chart.engine.axes;

import com.telerik.widget.chart.engine.axes.categorical.AxisSupportsCombinedSeriesPlot;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineMode;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineStrategy;
import com.telerik.widget.chart.engine.series.combination.CombinedSeries;

import java.util.ArrayList;

/**
 * Instances of this class represent a context needed by the Chart engine to perform the plotting logic
 * of an {@link com.telerik.widget.chart.engine.axes.AxisModel}.
 */
public class AxisUpdateContext {

    private boolean isStacked = false;
    private boolean isStacked100 = false;
    private double maximumStackSum;
    private double minimumStackSum;
    private ArrayList<ChartSeriesModel> series;
    private ArrayList<CombinedSeries> combinedSeries;
    private ArrayList<ChartSeriesModel> notCombinedSeries;

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.engine.axes.AxisUpdateContext} class with
     * the specified parameters.
     *
     * @param axis                    an instance of the {@link AxisModel} which will be part of the invalidation and plotting procedure.
     * @param series                  an array of {@link com.telerik.widget.chart.engine.series.ChartSeriesModel} instances representing the series currently plotted in the Chart.
     * @param seriesCombineStrategies a collection of {@link com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineStrategy} determining how the series will be combined.
     */
    public AxisUpdateContext(AxisModel axis, ArrayList<ChartSeriesModel> series, Iterable<ChartSeriesCombineStrategy> seriesCombineStrategies) {
        this.series = series;
        if (axis instanceof AxisSupportsCombinedSeriesPlot) {
            return;
        }

        this.combinedSeries = new ArrayList<CombinedSeries>();
        this.notCombinedSeries = new ArrayList<ChartSeriesModel>();
        this.minimumStackSum = Double.POSITIVE_INFINITY;
        this.maximumStackSum = Double.NEGATIVE_INFINITY;

        for (ChartSeriesCombineStrategy combineStrategy : seriesCombineStrategies) {
            if (!combineStrategy.stackValueAxes.contains(axis)) {
                continue;
            }

            // extract only relevant combined and non combined series.
            for (ChartSeriesModel seriesModel : series) {
                for (CombinedSeries combinedSeries : combineStrategy.combinedSeries) {
                    if (combinedSeries.series().contains(seriesModel)) {
                        this.combinedSeries.add(combinedSeries);
                        this.isStacked |= combinedSeries.combineMode() == ChartSeriesCombineMode.STACK;
                        this.isStacked100 |= combinedSeries.combineMode() == ChartSeriesCombineMode.STACK_100;
                    }
                }
                if (combineStrategy.nonCombinedSeries.contains(seriesModel)) {
                    this.notCombinedSeries.add(seriesModel);
                }
            }

            if (combineStrategy.minimumStackSums.containsKey(axis)) {
                this.minimumStackSum = Math.min(combineStrategy.minimumStackSums.get(axis), this.minimumStackSum);
                this.maximumStackSum = Math.max(combineStrategy.maximumStackSums.get(axis), this.maximumStackSum);
            }
        }
    }

    /**
     * Gets a boolean value determining whether there are stacked series in the Chart.
     *
     * @return <code>true</code> if there are stacked series, otherwise <code>false</code>.
     */
    public boolean isStacked() {
        return this.isStacked;
    }

    /**
     * Gets a boolean value determining whether there are stacked-100 series in the Chart.
     *
     * @return <code>true</code> if there are stacked-100 series, otherwise <code>false</code>.
     */
    public boolean isStacked100() {
        return this.isStacked100;
    }

    /**
     * Gets the value of the maximum stack-sum.
     *
     * @return the maximum stack-sum.
     */
    public double maximumStackSum() {
        return this.maximumStackSum;
    }

    /**
     * Gets the value of the minimum stack-sum.
     *
     * @return the minimum stack-sum.
     */
    public double getMinimumStackSum() {
        return this.minimumStackSum;
    }

    /**
     * Gets a collection of {@link ChartSeriesModel} objects describing the series taking part in the update procedure.
     *
     * @return the {@link com.telerik.widget.chart.engine.series.ChartSeriesModel} collection.
     */
    public Iterable<ChartSeriesModel> series() {
        return this.series;
    }

    /**
     * Gets a collection of {@link CombinedSeries} objects describing the combined series taking part in the update procedure.
     *
     * @return the {@link CombinedSeries} collection.
     */
    public Iterable<CombinedSeries> combinedSeries() {
        return this.combinedSeries;
    }

    /**
     * Gets a collection of {@link ChartSeriesModel} objects describing the non-series taking part in the update procedure.
     *
     * @return the {@link ChartSeriesModel} collection.
     */
    public Iterable<ChartSeriesModel> nonCombinedSeries() {
        return this.notCombinedSeries;
    }
}

