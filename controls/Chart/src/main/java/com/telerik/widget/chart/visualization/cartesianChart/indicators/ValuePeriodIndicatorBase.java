package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.financial.ValuePeriodIndicatorDataSourceBase;

public abstract class ValuePeriodIndicatorBase extends ValueIndicatorBase {

    /**
     * Period of the current indicator.
     */
    protected int period;

    /**
     * Creates a new instance of the {@link ValuePeriodIndicatorBase} class.
     */
    public ValuePeriodIndicatorBase() {
    }

    /**
     * Gets or sets the moving average period.
     */
    public int getPeriod() {
        return this.period;
    }

    /**
     * Sets or sets the moving average period.
     *
     * @param value The new period.
     */
    public void setPeriod(int value) {
        if (this.period == value)
            return;

        this.period = value;
        this.onPeriodChanged(value);
    }

    private void onPeriodChanged(int newValue) {
        ((ValuePeriodIndicatorDataSourceBase) this.dataSource()).setPeriod(newValue);
    }
}
