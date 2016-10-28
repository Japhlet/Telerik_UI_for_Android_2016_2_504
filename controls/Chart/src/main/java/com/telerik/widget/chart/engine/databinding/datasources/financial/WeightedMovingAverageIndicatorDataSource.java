package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.ValuePeriodIndicatorBase;

public class WeightedMovingAverageIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    public WeightedMovingAverageIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected void bindCore() {
        this.bindCore(0);
    }

    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore(this.bindings.indexOf(binding));
    }

    private static double calculateCurrentValue(SizedQueue currentItems) {
        int itemsCount = currentItems.currentItemsCount;
        Double[] currentItemsArray = currentItems.toArray(new Double[currentItems.size]);

        double weightedSum = 0;

        for (int i = 0; i < itemsCount; i++)
            weightedSum += currentItemsArray[i] * (i + 1);

        double divider = itemsCount * (itemsCount + 1) / 2;

        return weightedSum / divider;
    }

    private void bindCore(int startIndex) {
        ValuePeriodIndicatorBase indicator = (ValuePeriodIndicatorBase) this.owner.getPresenter();

        int period = indicator.getPeriod();
        SizedQueue currentItems = new SizedQueue();
        currentItems.size = period;

        int currentIndex = 0;

        for (Object item : this.itemsSource) {
            Object val = this.getValueBinding().getValue(item);
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
