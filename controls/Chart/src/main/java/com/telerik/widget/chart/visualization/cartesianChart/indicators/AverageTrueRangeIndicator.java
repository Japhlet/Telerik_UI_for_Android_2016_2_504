package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.AverageTrueRangeIndicatorDataSource;

public class AverageTrueRangeIndicator extends HighLowClosePeriodIndicatorBase {
    /**
     * Creates a new instance of the {@link com.telerik.widget.chart.visualization.cartesianChart.indicators.HighLowClosePeriodIndicatorBase} class.
     */
    public AverageTrueRangeIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Average True Range (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new AverageTrueRangeIndicatorDataSource(this.model());
    }
}
