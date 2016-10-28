package com.telerik.widget.chart.engine.series.combination;

import com.telerik.android.common.Function;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.common.SeriesModelWithAxes;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModel;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Handles combination of widget series that are {@link SupportCombineMode} instances and have their {@link SupportCombineMode#getCombineMode()} member specified.
 */
public class ChartSeriesCombineStrategy {

    public ArrayList<CombinedSeries> combinedSeries;
    public ArrayList<ChartSeriesModel> nonCombinedSeries;
    public boolean hasCombination = false;
    public Hashtable<AxisModel, Double> maximumStackSums;
    public Hashtable<AxisModel, Double> minimumStackSums;
    public AxisModel stackAxis;
    public ArrayList<AxisModel> stackValueAxes;
    public boolean isUpdated = false;

    Function<SeriesModelWithAxes, AxisModel> valueAxesExtractor;

    /**
     * Creates a new instance of the {@link ChartSeriesCombineStrategy} class.
     */
    public ChartSeriesCombineStrategy() {
        this.combinedSeries = new ArrayList<CombinedSeries>();
        this.nonCombinedSeries = new ArrayList<ChartSeriesModel>();
        this.stackValueAxes = new ArrayList<AxisModel>();
        this.maximumStackSums = new Hashtable<AxisModel, Double>();
        this.minimumStackSums = new Hashtable<AxisModel, Double>();
    }

    /**
     * Updates the combine strategy with the supplied series and axis.
     *
     * @param series    The series of the combine strategy.
     * @param stackAxis The axis of the combine strategy.
     */
    public void update(Iterable<ChartSeriesModel> series, AxisModel stackAxis) {
        if (this.isUpdated) {
            return;
        }

        this.stackAxis = stackAxis;

        if (stackAxis.getType() == AxisType.FIRST) {
            this.valueAxesExtractor = new Function<SeriesModelWithAxes, AxisModel>() {
                @Override
                public AxisModel apply(SeriesModelWithAxes argument) {
                    return argument.getSecondAxis();
                }
            };
        } else {
            this.valueAxesExtractor = new Function<SeriesModelWithAxes, AxisModel>() {
                @Override
                public AxisModel apply(SeriesModelWithAxes argument) {
                    return argument.getFirstAxis();
                }
            };
        }

        for (ChartSeriesModel model : series) {
            AxisModel stackValueAxis = this.valueAxesExtractor.apply((SeriesModelWithAxes) model);
            if (!this.stackValueAxes.contains(stackValueAxis)) {
                this.stackValueAxes.add(stackValueAxis);
            }

            SupportCombineMode combinableSeries = null;
            if (model instanceof SupportCombineMode) {
                combinableSeries = (SupportCombineMode) model;
            }

            if (combinableSeries == null || combinableSeries.getCombineMode() == ChartSeriesCombineMode.NONE) {
                this.nonCombinedSeries.add(model);
                continue;
            }

            CombinedSeries combinedSeries = this.getCombinedSeries(combinableSeries);
            combinedSeries.series().add(model);

            this.hasCombination = true;
        }

        if (this.hasCombination) {
            this.buildGroups();
        }

        this.isUpdated = true;
    }

    /**
     * Applies layout rounding to the series in this strategy taking into account the provided chart area.
     *
     * @param chartArea The chart area of the series.
     */
    public void applyLayoutRounding(ChartAreaModel chartArea) {
        // combined series
        for (CombinedSeries series : this.combinedSeries) {
            CombinedSeriesRoundLayoutStrategy strategy = this.getRoundLayoutStrategy(series);
            if (strategy != null) {
                strategy.applyLayoutRounding(chartArea, series);
            }
        }

        // non-combined series
        for (ChartSeriesModel nonCombinedSeries : this.nonCombinedSeries) {
            nonCombinedSeries.applyLayoutRounding();
        }
    }

    /**
     * Plots the combined series on the chart area.
     */
    public void plot() {
        int count = this.combinedSeries.size();
        for (CombinedSeries series : this.combinedSeries) {
            CombinedSeriesPlotStrategy strategy = this.getPlotStrategy(series);
            if (strategy != null) {
                strategy.plot(series, count);
            }
        }
    }

    /**
     * Resets this combine strategy.
     */
    public void reset() {
        this.combinedSeries.clear();
        this.nonCombinedSeries.clear();
        this.hasCombination = false;
        this.maximumStackSums.clear();
        this.minimumStackSums.clear();
        this.stackValueAxes.clear();
        this.isUpdated = false;
    }

    private CombinedSeries getCombinedSeries(SupportCombineMode combinableSeries) {
        Type type = combinableSeries.getClass();
        ChartSeriesCombineMode combineMode = combinableSeries.getCombineMode();
        AxisModel stackValueAxis = this.valueAxesExtractor.apply((SeriesModelWithAxes) combinableSeries);

        for (CombinedSeries series : this.combinedSeries) {
            if (series.seriesType().equals(type) &&
                    series.combineMode() == combineMode && (combineMode == ChartSeriesCombineMode.CLUSTER ||
                    ((combineMode == ChartSeriesCombineMode.STACK ||
                            combineMode == ChartSeriesCombineMode.STACK_100) &&
                            series.stackValueAxis() == stackValueAxis))) {
                return series;
            }
        }

        CombinedSeries newSeries = new CombinedSeries(type, combineMode, this.combinedSeries.size(), this.stackAxis, stackValueAxis);
        this.combinedSeries.add(newSeries);

        return newSeries;
    }

    private void buildGroups() {
        // default group logic
        for (CombinedSeries combinedSeries : this.combinedSeries) {
            this.processSeries(combinedSeries);
        }
    }

    private void processSeries(CombinedSeries combinedSeries) {
        Map<Object, CombineGroup> groupsByKey = new HashMap<Object, CombineGroup>(8);
        CombineStack stack;
        double min;
        double max;

        for (ChartSeriesModel series : combinedSeries.series()) {
            AxisModel stackValueAxis = this.valueAxesExtractor.apply((SeriesModelWithAxes) series);
            if (!this.minimumStackSums.containsKey(stackValueAxis)) {
                this.minimumStackSums.put(stackValueAxis, Double.POSITIVE_INFINITY);
                this.maximumStackSums.put(stackValueAxis, Double.NEGATIVE_INFINITY);
            }

            min = this.minimumStackSums.get(stackValueAxis);
            max = this.maximumStackSums.get(stackValueAxis);

            for (Object point : series.visibleDataPoints()) {
                DataPoint dataPoint = (DataPoint) point;
                Object key = this.stackAxis.getCombineGroupKey(dataPoint);
                if (key == null) {
                    continue;
                }

                CombineGroup group;
                if (!groupsByKey.containsKey(key)) {
                    group = new CombineGroup();
                    groupsByKey.put(key, group);
                    combinedSeries.groups().add(group);
                } else {
                    group = groupsByKey.get(key);
                }

                stack = group.getStack((SupportCombineMode) series);
                stack.points().add(dataPoint);

                AxisModel.StackValue value = stackValueAxis.getStackValue(dataPoint);
                if (value.positive) {
                    stack.positiveSum += value.value;
                } else {
                    stack.negativeSum += value.value;
                }

                min = Math.min(min, stack.negativeSum);
                max = Math.max(max, stack.positiveSum);
            }

            this.minimumStackSums.put(stackValueAxis, min);
            this.maximumStackSums.put(stackValueAxis, max);
        }
    }

    private CombinedSeriesPlotStrategy getPlotStrategy(CombinedSeries series) {
        if (series.series().size() > 0) {
            return series.series().get(0).getCombinedPlotStrategy();
        }

        return null;
    }

    private CombinedSeriesRoundLayoutStrategy getRoundLayoutStrategy(CombinedSeries series) {
        if (series.series().size() > 0) {
            return series.series().get(0).getCombinedRoundLayoutStrategy();
        }

        return null;
    }
}

