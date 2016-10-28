package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public class ValuePeriodIndicatorDataSourceBase extends ValueIndicatorDataSourceBase {
    private int period;

    public ValuePeriodIndicatorDataSourceBase(ChartSeriesModel owner) {
        super(owner);
    }

    public int getPeriod() {
        return this.period;
    }

    public void setPeriod(int value) {
        if (this.period == value) {
            return;
        }

        this.period = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }
}
