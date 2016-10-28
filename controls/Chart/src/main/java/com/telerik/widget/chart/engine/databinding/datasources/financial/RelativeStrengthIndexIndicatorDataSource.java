package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.ValuePeriodIndicatorBase;

public class RelativeStrengthIndexIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    public RelativeStrengthIndexIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    @Override
    protected void bindCore() {
        int period = ((ValuePeriodIndicatorBase) this.owner.getPresenter()).getPeriod();
        SizedQueue losses = new SizedQueue();
        losses.size = period;

        SizedQueue gains = new SizedQueue();
        gains.size = period;

        double prevValue = 0;
        double currentValue = 0;
        double lossesAverage = 0;
        double gainsAverage = 0;
        int currentIndex = 0;

        for (Object item : this.itemsSource) {
            double value = ((Number) this.valueBinding.getValue(item)).doubleValue();
            double difference = 0;
            if (currentIndex > 0)
                difference = Math.abs(value - prevValue);

            double gain;
            double loss;

            if (value > prevValue) {
                gain = difference;
                loss = 0;
            } else {
                gain = 0;
                loss = difference;
            }

            gains.enqueueItem(gain);
            losses.enqueueItem(loss);

            if (currentIndex < period) {
                lossesAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(losses);
                gainsAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(gains);
            } else {
                gainsAverage = ExponentialMovingAverageIndicatorDataSource.calculateCurrentValue(false, period, gain, gainsAverage);
                lossesAverage = ExponentialMovingAverageIndicatorDataSource.calculateCurrentValue(false, period, loss, lossesAverage);

                currentValue = 100 - (100 / (1 + (gainsAverage / lossesAverage)));
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

            prevValue = value;
            currentIndex++;
        }
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }
}
