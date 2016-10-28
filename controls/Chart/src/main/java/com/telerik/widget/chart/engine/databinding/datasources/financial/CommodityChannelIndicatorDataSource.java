package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.CommodityChannelIndexIndicator;

public class CommodityChannelIndicatorDataSource extends HighLowClosePeriodIndicatorDataSourceBase {

    public CommodityChannelIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    @Override
    protected void bindCore() {
        int period = ((CommodityChannelIndexIndicator) this.owner.getPresenter()).getPeriod();
        SizedQueue typicalPrices = new SizedQueue();
        typicalPrices.size = period;

        int currentIndex = 0;
        for (Object item : this.itemsSource) {
            double high = ((Number) this.highBinding.getValue(item)).doubleValue();
            double low = ((Number) this.lowBinding.getValue(item)).doubleValue();
            double close = ((Number) this.closeBinding.getValue(item)).doubleValue();

            double typicalPrice = (high + low + close) / 3F;
            typicalPrices.enqueueItem(typicalPrice);

            double currentValue = 0;

            if (currentIndex > 0) {
                double typicalPriceMA = MovingAverageIndicatorDataSource.calculateCurrentValue(typicalPrices);

                double meanDeviation = 0;
                for (double price : typicalPrices) {
                    meanDeviation += Math.abs(typicalPriceMA - price);
                }
                meanDeviation /= typicalPrices.size;

                currentValue = (typicalPrice - typicalPriceMA) / 0.015D / meanDeviation;
            }

            CategoricalDataPoint point;
            DataPointCollection dataPoints = this.owner.dataPoints();
            if (dataPoints.size() > currentIndex) {
                point = (CategoricalDataPoint) dataPoints.get(currentIndex);
                point.setValue(currentValue);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(currentValue);
                dataPoints.add(point);
            }

            currentIndex++;
        }
    }
}
