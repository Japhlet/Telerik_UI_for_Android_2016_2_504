package com.telerik.widget.chart.engine.series.combination.barSeries;

import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineMode;
import com.telerik.widget.chart.engine.series.combination.CombineGroup;
import com.telerik.widget.chart.engine.series.combination.CombineStack;
import com.telerik.widget.chart.engine.series.combination.CombinedSeries;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesPlotStrategy;

/**
 * Layout algorithm for combined bar series.
 */
public class CombinedBarSeriesPlotStrategy extends CombinedSeriesPlotStrategy {

    /**
     * Creates a new instance of the {@link CombinedBarSeriesPlotStrategy} class.
     */
    public CombinedBarSeriesPlotStrategy() {
    }

    @Override
    public void plot(CombinedSeries series, int combinedSeriesCount) {
        double groupPosition;
        double groupLength;
        double stackPosition;
        double stackLength;

        for (CombineGroup group : series.groups()) {
            CategoricalDataPointBase firstPoint = (CategoricalDataPointBase) group.stacks().get(0).points().get(0);
            CategoricalAxisPlotInfo plotInfo = firstPoint.categoricalPlot;
            if (plotInfo == null) {
                continue;
            }

            int maxStackSize = series.combineMode() == ChartSeriesCombineMode.CLUSTER ? series.series().size() : group.stacks().size();

            groupLength = plotInfo.length / combinedSeriesCount;
            groupPosition = plotInfo.position + (series.combineIndex() * groupLength);
            stackLength = groupLength / maxStackSize;
            stackPosition = groupPosition;

            for (CombineStack stack : group.stacks()) {
                for (DataPoint point : stack.points()) {
                    CategoricalDataPointBase categoricalDataPoint = (CategoricalDataPointBase) point;
                    int seriesIndex = series.combineMode() == ChartSeriesCombineMode.CLUSTER ? categoricalDataPoint.getParent().index() : 0;
                    plotInfo = categoricalDataPoint.categoricalPlot;
                    if (plotInfo != null) {
                        plotInfo.position = stackPosition + stackLength * seriesIndex;
                        plotInfo.length = stackLength;
                    }
                }
            }
        }
    }

}

