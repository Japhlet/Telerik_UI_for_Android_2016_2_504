package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.RaviIndicator;

public class RaviIndicatorDataSource extends ShortLongPeriodIndicatorDataSourceBase {
    public RaviIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    protected void bindCore() {
        this.bindCore(0);
    }

    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore(this.bindings.indexOf(binding));
    }

    private void bindCore(int startIndex) {
        RaviIndicator indicator = (RaviIndicator) this.getOwner().getPresenter();
        int shortPeriod = indicator.getShortPeriod();
        int longPeriod = indicator.getLongPeriod();

        SizedQueue currentItemsShort = new SizedQueue();
        currentItemsShort.size = shortPeriod;
        SizedQueue currentItemsLong = new SizedQueue();
        currentItemsLong.size = longPeriod;

        int currentIndex = 0;

        for (Object item : this.getItemsSource()) {
            double value = ((Number) this.getValueBinding().getValue(item)).doubleValue();
            currentItemsShort.enqueueItem(value);
            currentItemsLong.enqueueItem(value);
            if (currentIndex >= startIndex) {
                double shortAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItemsShort);
                double longAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItemsLong);
                double currentValue = (shortAverage - longAverage) / longAverage * 100;
                CategoricalDataPoint point;
                if (this.owner.dataPoints().size() > currentIndex) {
                    point = (CategoricalDataPoint) this.owner.dataPoints().get(currentIndex);
                    point.setValue(currentValue);
                } else {
                    point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                    point.setValue(currentValue);
                    this.owner.dataPoints().add(point);
                }
            }

            currentIndex++;
        }
    }
}
