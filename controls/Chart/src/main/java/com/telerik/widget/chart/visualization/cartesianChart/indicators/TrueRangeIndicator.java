package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.TrueRangeIndicatorDataSource;

/**
 * Visualizes a collection of DataPoints, using a line shape. This class represents the True Range oscillator.
 */
public class TrueRangeIndicator extends HighLowCloseIndicatorBase {

    /**
     * Creates a new instance of the {@link TrueRangeIndicator} class.
     */
    public TrueRangeIndicator() {
    }

    @Override
    public String toString() {
        return "True Range";
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new TrueRangeIndicatorDataSource(this.model());
    }
}
