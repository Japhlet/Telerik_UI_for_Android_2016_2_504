package com.telerik.widget.chart.engine.series.rangeSeries;

import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineMode;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesPlotStrategy;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesRoundLayoutStrategy;
import com.telerik.widget.chart.engine.series.combination.SupportCombineMode;
import com.telerik.widget.chart.engine.series.combination.barSeries.CombinedBarSeriesPlotStrategy;

/**
 * This class defines the layout algorithm for the {@link com.telerik.widget.chart.visualization.cartesianChart.series.categorical.RangeBarSeries}.
 */
public class RangeBarSeriesModel extends RangeSeriesBaseModel implements SupportCombineMode {
    @Override
    public AxisPlotMode getDefaultPlotMode() {
        return AxisPlotMode.BETWEEN_TICKS;
    }

    @Override
    public CombinedSeriesPlotStrategy getCombinedPlotStrategy() {
        return new CombinedBarSeriesPlotStrategy();
    }

    @Override
    public void setCombineMode(ChartSeriesCombineMode value) {
        if(value == ChartSeriesCombineMode.STACK || value == ChartSeriesCombineMode.STACK_100) {
            throw new IllegalArgumentException("The range bar series support only CLUSTER combine mode.");
        }

        super.setCombineMode(value);
    }

    @Override
    protected boolean getShouldRoundLayout() {
        return true;
    }

    @Override
    public CombinedSeriesRoundLayoutStrategy getCombinedRoundLayoutStrategy() {
        return new CombinedRangeBarSeriesRoundLayoutStrategy();
    }
}

