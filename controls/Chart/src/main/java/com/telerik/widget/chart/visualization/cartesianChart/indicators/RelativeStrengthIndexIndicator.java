package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.RelativeStrengthIndexIndicatorDataSource;

public class RelativeStrengthIndexIndicator extends ValuePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link RelativeStrengthIndexIndicator} class.
     */
    public RelativeStrengthIndexIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Relative Strength Index (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new RelativeStrengthIndexIndicatorDataSource(this.model());
    }
}
