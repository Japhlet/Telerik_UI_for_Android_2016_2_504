package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.financial.ShortLongPeriodIndicatorDataSourceBase;

public abstract class ShortLongPeriodIndicatorBase extends ValueIndicatorBase {

    private int longPeriod;
    private int shortPeriod;

    /**
     * Creates a new instance of the {@link ShortLongPeriodIndicatorBase} class.
     */
    public ShortLongPeriodIndicatorBase() {
    }

    /**
     * Gets the indicator long period.
     *
     * @return the current long period.
     */
    public int getLongPeriod() {
        return this.longPeriod;
    }

    /**
     * Sets the indicator long period.
     *
     * @param value the new long period.
     */
    public void setLongPeriod(int value) {
        if (this.longPeriod == value)
            return;

        this.longPeriod = value;
        onLongPeriodChanged(value);
    }

    /**
     * Gets the indicator short period.
     *
     * @return the current short period.
     */
    public int getShortPeriod() {
        return this.shortPeriod;
    }

    /**
     * Sets the indicator short period.
     *
     * @param value the new short period.
     */
    public void setShortPeriod(int value) {
        if (this.shortPeriod == value)
            return;

        this.shortPeriod = value;
        onShortPeriodChanged(value);
    }

    private void onLongPeriodChanged(int newValue) {
        ((ShortLongPeriodIndicatorDataSourceBase) this.dataSource()).setLongPeriod(newValue);
    }

    private void onShortPeriodChanged(int newValue) {
        ((ShortLongPeriodIndicatorDataSourceBase) this.dataSource()).setShortPeriod(newValue);
    }
}
