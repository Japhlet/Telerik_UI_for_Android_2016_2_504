package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.StochasticSlowIndicatorDataSource;

/**
 * Base class for financial indicators that are calculated upon the High, Low and Close values and define a period.
 */
public class StochasticSlowIndicator extends StochasticFastIndicator {

    private int slowPeriod;

    /**
     * Creates a new instance of the {@link StochasticSlowIndicator} class.
     */
    public StochasticSlowIndicator() {
    }

    /**
     * Gets the indicator slowing period.
     */
    public int getSlowingPeriod() {
        return this.slowPeriod;
    }

    /**
     * Sets the indicator slowing period.
     *
     * @param value The new slow period.
     */
    public void setSlowingPeriod(int value) {
        if (this.slowPeriod == value)
            return;

        this.slowPeriod = value;
        this.onSlowingPeriodChanged(value);
    }

    @Override
    public String toString() {
        return String.format("Stochastic Slow (%s, %s, %s)", this.getMainPeriod(), this.getSignalPeriod(), this.getSlowingPeriod());
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new StochasticSlowIndicatorDataSource(this.model());
    }

    private void onSlowingPeriodChanged(int newValue) {
        ((StochasticSlowIndicatorDataSource) this.dataSource()).setSlowingPeriod(newValue);
    }
}
