package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.ModifiedMovingAverageIndicatorDataSource;

public class ModifiedMovingAverageIndicator extends ValuePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link ModifiedMovingAverageIndicator} class.
     */
    public ModifiedMovingAverageIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Modified Moving Average (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new ModifiedMovingAverageIndicatorDataSource(this.model());
    }
}
