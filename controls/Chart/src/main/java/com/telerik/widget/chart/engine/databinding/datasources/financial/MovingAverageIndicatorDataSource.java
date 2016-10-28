package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.ValuePeriodIndicatorBase;

public class MovingAverageIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    public MovingAverageIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    protected static double calculateCurrentValue(SizedQueue items) {
        return items.runningSum / items.currentItemsCount;
    }

    @Override
    protected void bindCore() {
        this.bindCore(0);
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore(this.bindings.indexOf(binding));
    }

    private void bindCore(int startIndex) {
        SizedQueue currentItems = new SizedQueue();
        currentItems.size = ((ValuePeriodIndicatorBase) this.owner.getPresenter()).getPeriod();

        int currentIndex = 0;

        for (Object item : this.itemsSource) {
            Object val = this.valueBinding.getValue(item);
            currentItems.enqueueItem(((Number) val).doubleValue());
            if (currentIndex >= startIndex) {
                double currentAverage = calculateCurrentValue(currentItems);
                CategoricalDataPoint point;
                if (this.owner.dataPoints().size() > currentIndex) {
                    point = (CategoricalDataPoint) this.owner.dataPoints().get(currentIndex);
                    point.setValue(currentAverage);
                } else {
                    point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                    point.setValue(currentAverage);
                    this.owner.dataPoints().add(point);
                }
            }

            currentIndex++;
        }
    }
}
