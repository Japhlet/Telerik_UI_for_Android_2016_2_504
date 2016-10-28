package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.ValuePeriodIndicatorBase;

public class TrixIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {
    public TrixIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected void bindCore() {
        int period = ((ValuePeriodIndicatorBase) this.owner.getPresenter()).getPeriod();
        SizedQueue currentItems = new SizedQueue();
        currentItems.size = period;
        SizedQueue emaOneItems = new SizedQueue();
        emaOneItems.size = period;
        SizedQueue emaTwoItems = new SizedQueue();
        emaTwoItems.size = period;

        int currentIndex = 0;
        double emaOne, emaTwo, emaThree;
        double lastEmaOne = 0;
        double lastEmaTwo = 0;
        double lastEmaThree = 0;
        double currentValue;

        for (Object item : this.itemsSource) {
            double value = ((Number) this.getValueBinding().getValue(item)).doubleValue();
            currentItems.enqueueItem(value);

            if (currentIndex < period) {
                emaOne = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItems);
                emaOneItems.enqueueItem(emaOne);
                emaTwo = MovingAverageIndicatorDataSource.calculateCurrentValue(emaOneItems);
                emaTwoItems.enqueueItem(emaTwo);
                emaThree = MovingAverageIndicatorDataSource.calculateCurrentValue(emaTwoItems);
            } else {
                emaOne = ExponentialMovingAverageIndicatorDataSource.calculateCurrentValue(false, period, value, lastEmaOne);
                emaOneItems.enqueueItem(emaOne);
                emaTwo = ExponentialMovingAverageIndicatorDataSource.calculateCurrentValue(false, period, emaOne, lastEmaTwo);
                emaTwoItems.enqueueItem(emaTwo);
                emaThree = ExponentialMovingAverageIndicatorDataSource.calculateCurrentValue(false, period, emaTwo, lastEmaThree);
            }

            if (currentIndex == 0)
                currentValue = 0;
            else
                currentValue = 100 * (emaThree - lastEmaThree) / emaThree;

            CategoricalDataPoint point;
            if (this.owner.dataPoints().size() > currentIndex) {
                point = (CategoricalDataPoint) this.owner.dataPoints().get(currentIndex);
                point.setValue(currentValue);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(currentValue);
                this.owner.dataPoints().add(point);
            }

            lastEmaOne = emaOne;
            lastEmaTwo = emaTwo;
            lastEmaThree = emaThree;
            currentIndex++;
        }
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }
}
