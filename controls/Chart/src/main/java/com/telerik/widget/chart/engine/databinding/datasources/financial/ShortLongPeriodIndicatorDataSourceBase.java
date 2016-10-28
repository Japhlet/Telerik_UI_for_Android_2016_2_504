package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public abstract class ShortLongPeriodIndicatorDataSourceBase extends ValueIndicatorDataSourceBase {
    private int shortPeriod;
    private int longPeriod;

    public ShortLongPeriodIndicatorDataSourceBase(ChartSeriesModel owner) {
        super(owner);
    }

    public int getShortPeriod() {
        return this.shortPeriod;
    }

    public void setShortPeriod(int value) {
        if (this.shortPeriod == value) {
            return;
        }

        this.shortPeriod = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }

    public int getLongPeriod() {
        return this.longPeriod;
    }

    public void setLongPeriod(int value) {
        if (this.longPeriod == value) {
            return;
        }

        this.longPeriod = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }
}
