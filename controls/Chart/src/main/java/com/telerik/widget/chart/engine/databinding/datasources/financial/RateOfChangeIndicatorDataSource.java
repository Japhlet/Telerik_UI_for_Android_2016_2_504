package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public class RateOfChangeIndicatorDataSource extends MomentumIndicatorDataSource {

    public RateOfChangeIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected double calculateValue(double currentValue, double olderValue) {
        return (currentValue - olderValue) / olderValue * 100;
    }
}
