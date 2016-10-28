package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.RaviIndicatorDataSource;

/**
 * Visualizes a collection of DataPoints, using a line shape. This class represents the Range Action Verification Index financial indicator.
 */
public class RaviIndicator extends ShortLongPeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link RaviIndicator} class.
     */
    public RaviIndicator() {
    }

    @Override
    public String toString() {
        return String.format("RAVI (%s, %s)", this.getLongPeriod(), this.getShortPeriod());
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new RaviIndicatorDataSource(this.model());
    }
}