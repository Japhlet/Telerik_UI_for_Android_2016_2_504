package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.CommodityChannelIndicatorDataSource;

public class CommodityChannelIndexIndicator extends HighLowClosePeriodIndicatorBase {

    /**
     * Creates a new instance of the {@link CommodityChannelIndexIndicator} class.
     */
    public CommodityChannelIndexIndicator() {
    }

    @Override
    public String toString() {
        return String.format("Commodity Channel Index (%s)", this.period);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new CommodityChannelIndicatorDataSource(this.model());
    }
}
