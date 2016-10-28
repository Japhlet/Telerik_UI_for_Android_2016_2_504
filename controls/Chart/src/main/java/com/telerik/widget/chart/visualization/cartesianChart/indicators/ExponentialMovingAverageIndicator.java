package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.ExponentialMovingAverageIndicatorDataSource;

public class ExponentialMovingAverageIndicator extends ValuePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link ExponentialMovingAverageIndicator} class.
     */
    public ExponentialMovingAverageIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Exponential Moving Average (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new ExponentialMovingAverageIndicatorDataSource(this.model());
    }
}
