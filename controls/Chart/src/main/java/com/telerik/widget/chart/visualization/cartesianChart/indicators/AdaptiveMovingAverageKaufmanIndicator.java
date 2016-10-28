package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.AdaptiveMovingAverageKaufmanIndicatorDataSource;

public class AdaptiveMovingAverageKaufmanIndicator extends ValuePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link com.telerik.widget.chart.visualization.cartesianChart.indicators.ValuePeriodIndicatorBase} class.
     */
    public AdaptiveMovingAverageKaufmanIndicator() {
    }

    private int slowPeriod;
    private int fastPeriod;

    /**
     * Gets the "SlowPeriod" parameter of the {@link AdaptiveMovingAverageKaufmanIndicator}./>.
     */
    public int getSlowPeriod() {
        return this.slowPeriod;
    }

    /**
     * Sets the "SlowPeriod" parameter of the {@link AdaptiveMovingAverageKaufmanIndicator}./>.
     *
     * @param slowPeriod The new slow period.
     */
    public void setSlowPeriod(int slowPeriod) {
        if (this.slowPeriod == slowPeriod) {
            return;
        }

        this.slowPeriod = slowPeriod;
        this.onSlowPeriodChanged(slowPeriod);
    }

    /**
     * Gets the "FastPeriod" parameter of the {@link AdaptiveMovingAverageKaufmanIndicator}.
     */
    public int getFastPeriod() {
        return this.fastPeriod;
    }

    /**
     * Sets the "FastPeriod" parameter of the {@link AdaptiveMovingAverageKaufmanIndicator}.
     *
     * @param fastPeriod The new fast period.
     */
    public void setFastPeriod(int fastPeriod) {
        if (this.fastPeriod == fastPeriod) {
            return;
        }

        this.fastPeriod = fastPeriod;
        this.onFastPeriodChanged(fastPeriod);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", this.period, this.fastPeriod, this.slowPeriod);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new AdaptiveMovingAverageKaufmanIndicatorDataSource(this.model());
    }

    private void onSlowPeriodChanged(int newSlowPeriod) {
        ((AdaptiveMovingAverageKaufmanIndicatorDataSource) this.dataSource()).setSlowPeriod(newSlowPeriod);
    }

    private void onFastPeriodChanged(int newFastPeriod) {
        ((AdaptiveMovingAverageKaufmanIndicatorDataSource) this.dataSource()).setFastPeriod(newFastPeriod);
    }
}
