package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.RelativeMomentumIndexIndicatorDataSource;

public class RelativeMomentumIndexIndicator extends ValuePeriodIndicatorBase {

    /**
     * The momentum period or the shift.
     */
    protected int momentumPeriod;

    /**
     * Creates a new instance of the {@link RelativeMomentumIndexIndicator} class.
     */
    public RelativeMomentumIndexIndicator() {
    }


    /**
     * Gets the momentum period.
     *
     * @return the current momentum period.
     */
    public int getMomentumPeriod() {
        return this.momentumPeriod;
    }

    /**
     * Sets the momentum period.
     *
     * @param value the new momentum period.
     */
    public void setMomentumPeriod(int value) {
        if (this.momentumPeriod == value)
            return;

        this.momentumPeriod = value;
        onMomentumPeriodChanged(value);
    }

    @Override
    public String toString() {
        return String.format("Relative Momentum Index Indicator (%s, %s)", this.period, this.momentumPeriod);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new RelativeMomentumIndexIndicatorDataSource(this.model());
    }

    private void onMomentumPeriodChanged(int newValue) {
        ((RelativeMomentumIndexIndicatorDataSource) dataSource()).setMomentumPeriod(newValue);
    }
}
