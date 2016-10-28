package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.WeightedMovingAverageIndicatorDataSource;

/**
 * Visualizes a collection of DataPoints using a line. This class represents the Weighted Moving Average financial indicator.
 */
public class WeightedMovingAverageIndicator extends ValuePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link WeightedMovingAverageIndicator} class.
     */
    public WeightedMovingAverageIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Weighted Moving Average Indicator (%s)", this.getPeriod());
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new WeightedMovingAverageIndicatorDataSource(this.model());
    }
}
