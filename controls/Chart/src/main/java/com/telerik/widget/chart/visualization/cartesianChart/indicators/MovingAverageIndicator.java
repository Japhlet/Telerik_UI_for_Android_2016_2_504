package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.MovingAverageIndicatorDataSource;

public class MovingAverageIndicator extends ValuePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link MovingAverageIndicator} class.
     */
    public MovingAverageIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Moving Average (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new MovingAverageIndicatorDataSource(this.model());
    }
}
