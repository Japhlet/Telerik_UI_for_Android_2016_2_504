package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.TrixIndicatorDataSource;

/**
 * This class represents the TRIX financial indicator.
 */
public class TrixIndicator extends ValuePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link TrixIndicator} class.
     */
    public TrixIndicator() {
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new TrixIndicatorDataSource(this.model());
    }
}
