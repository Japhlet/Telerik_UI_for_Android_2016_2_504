package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.RateOfChangeIndicatorDataSource;

public class RateOfChangeIndicator extends MomentumIndicator {

    /**
     * Creates a new instance of the {@link RateOfChangeIndicator} class.
     */
    public RateOfChangeIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Rate Of Change (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new RateOfChangeIndicatorDataSource(this.model());
    }
}
