package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.MomentumIndicatorDataSource;

public class MomentumIndicator extends ValuePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link MomentumIndicator} class.
     */
    public MomentumIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Momentum (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new MomentumIndicatorDataSource(this.model());
    }
}
