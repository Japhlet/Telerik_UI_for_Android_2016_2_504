package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.UltimateOscillatorIndicatorDataSource;

/**
 * Visualizes a collection of DataPoints, using a line shape. This class represents the Momentum oscillator.
 */
public class UltimateOscillatorIndicator extends HighLowClosePeriodIndicatorBase {

    private int period3;
    private int period2;

    /**
     * Creates a new instance of the {@link UltimateOscillatorIndicator} class.
     */
    public UltimateOscillatorIndicator() {
    }

    /**
     * Gets the third period.
     */
    public int getPeriod3() {
        return this.period3;
    }

    /**
     * Sets the third period.
     *
     * @param value The new third period.
     */
    public void setPeriod3(int value) {
        if (this.period3 == value)
            return;

        this.period3 = value;
        this.onPeriod3Changed(value);
    }

    /**
     * Gets the second period.
     */
    public int getPeriod2()

    {
        return this.period2;
    }

    /**
     * Sets the second period.
     *
     * @param value The new second period.
     */
    public void setPeriod2(int value) {
        if (this.period2 == value)
            return;

        this.period2 = value;
        this.onPeriod2Changed(value);
    }

    @Override
    public String toString() {
        return String.format("Ultimate Oscillator (%s, %s, %s)", this.getPeriod(), this.getPeriod2(), this.getPeriod3());
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new UltimateOscillatorIndicatorDataSource(this.model());
    }

    private void onPeriod2Changed(int newValue) {
        ((UltimateOscillatorIndicatorDataSource) this.dataSource()).setPeriod2(newValue);
    }

    private void onPeriod3Changed(int newValue) {
        ((UltimateOscillatorIndicatorDataSource) this.dataSource()).setPeriod3(newValue);
    }
}
