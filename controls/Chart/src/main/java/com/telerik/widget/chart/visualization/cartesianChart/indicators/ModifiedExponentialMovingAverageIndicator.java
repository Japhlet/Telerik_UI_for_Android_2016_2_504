package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.ExponentialMovingAverageIndicatorDataSource;

public class ModifiedExponentialMovingAverageIndicator extends ExponentialMovingAverageIndicator {

    /**
     * Creates a new instance of the {@link ModifiedExponentialMovingAverageIndicator} class.
     */
    public ModifiedExponentialMovingAverageIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Modified Exponential Moving Average (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        ExponentialMovingAverageIndicatorDataSource dataSource = new ExponentialMovingAverageIndicatorDataSource(this.model());
        dataSource.setModified(true);

        return dataSource;
    }
}
