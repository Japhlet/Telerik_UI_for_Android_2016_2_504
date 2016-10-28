package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.AverageTrueRangeIndicator;


public class AverageTrueRangeIndicatorDataSource extends HighLowClosePeriodIndicatorDataSourceBase {

    public AverageTrueRangeIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    @Override
    protected void bindCore() {
        AverageTrueRangeIndicator indicator = (AverageTrueRangeIndicator) this.owner.getPresenter();
        int period = indicator.getPeriod();

        SizedQueue currentItems = new SizedQueue();
        currentItems.size = period;

        int currentIndex = 0;
        Object previousItem = null;
        double value;
        for (Object item : this.itemsSource) {
            value = TrueRangeIndicatorDataSource.calculateValue(this.highBinding, this.lowBinding, this.closeBinding, previousItem, item);

            currentItems.enqueueItem(value);

            double currentValue = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItems);

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
            previousItem = item;
        }
    }
}
