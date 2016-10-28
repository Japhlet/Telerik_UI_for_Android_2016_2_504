package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.ValuePeriodIndicatorBase;

public class ModifiedMovingAverageIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    public ModifiedMovingAverageIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected void bindCore() {
        this.bindCore(0);
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore(this.bindings.indexOf(binding));
    }

    private static double CalculateCurrentValue(SizedQueue currentItems) {
        int itemsCount = currentItems.size();
        double sum = 0;
        double currentSimpleAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItems);

        Double[] currentItemsArray = new Double[itemsCount];
        currentItems.toArray(currentItemsArray);
        for (int i = 0; i < itemsCount; i++)
            sum += (itemsCount - ((2 * i) + 1)) / 2F * currentItemsArray[i];

        return currentSimpleAverage + ((6 * sum) / itemsCount / (itemsCount + 1));
    }

    private void bindCore(int startIndex) {
        int period = ((ValuePeriodIndicatorBase) this.owner.getPresenter()).getPeriod();
        int currentIndex = 0;
        double currentAverage;

        SizedQueue currentItems = new SizedQueue();
        currentItems.size = period;

        for (Object item : this.itemsSource) {
            double value = ((Number) this.valueBinding.getValue(item)).doubleValue();
            currentItems.enqueueItem(value);

            if (currentIndex >= startIndex) {
                if (currentIndex < period - 1)
                    currentAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItems);
                else
                    currentAverage = CalculateCurrentValue(currentItems);

                CategoricalDataPoint point;
                DataPointCollection dataPoints = this.owner.dataPoints();
                if (dataPoints.size() > currentIndex) {
                    point = (CategoricalDataPoint) dataPoints.get(currentIndex);
                    point.setValue(currentAverage);
                } else {
                    point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                    point.setValue(currentAverage);
                    dataPoints.add(point);
                }
            }

            currentIndex++;
        }
    }
}
