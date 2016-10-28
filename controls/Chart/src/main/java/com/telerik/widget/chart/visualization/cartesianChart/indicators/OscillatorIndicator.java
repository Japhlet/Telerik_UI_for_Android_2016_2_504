package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.OscillatorIndicatorDataSource;

/**
 * Visualizes a collection of DataPoints, using a line. This class represents the Moving Average financial indicator. Its values are defined as the average value of the last points.
 */
public class OscillatorIndicator extends ShortLongPeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link OscillatorIndicator} class.
     */
    public OscillatorIndicator() {
    }

    @Override
    public String toString() {
        return "Oscillator (" + this.getLongPeriod() + ", " + this.getShortPeriod() + ")";
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new OscillatorIndicatorDataSource(this.model());
    }
}
