package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.OscillatorIndicator;

public class OscillatorIndicatorDataSource extends ShortLongPeriodIndicatorDataSourceBase {
    public OscillatorIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected void bindCore() {
        this.BindCore(0);
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.BindCore(this.bindings.indexOf(binding));
    }

    private void BindCore(int startIndex) {
        OscillatorIndicator indicator = (OscillatorIndicator) this.owner.getPresenter();

        int shortPeriod = indicator.getShortPeriod();
        int longPeriod = indicator.getLongPeriod();

        SizedQueue currentItemsShort = new SizedQueue();
        currentItemsShort.size = shortPeriod;
        SizedQueue currentItemsLong = new SizedQueue();
        currentItemsLong.size = longPeriod;

        int currentIndex = 0;

        for (Object item : this.itemsSource) {
            double value = ((Number) this.getValueBinding().getValue(item)).doubleValue();
            currentItemsShort.enqueueItem(value);
            currentItemsLong.enqueueItem(value);
            if (currentIndex >= startIndex) {
                double shortAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItemsShort);
                double longAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItemsLong);
                double currentValue = (shortAverage - longAverage) / shortAverage * 100;
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
