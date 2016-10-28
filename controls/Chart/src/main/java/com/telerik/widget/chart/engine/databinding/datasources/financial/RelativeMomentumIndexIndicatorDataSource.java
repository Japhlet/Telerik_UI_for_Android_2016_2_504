package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.RelativeMomentumIndexIndicator;

public class RelativeMomentumIndexIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    public RelativeMomentumIndexIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    private int momentumPeriod;

    public int getMomentumPeriod() {
        return this.momentumPeriod;
    }

    public void setMomentumPeriod(int value) {
        if (this.momentumPeriod == value)
            return;

        this.momentumPeriod = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        //// TODO: Optimize this to update only the effected points.
        this.bindCore();
    }

    @Override
    protected void bindCore() {
        int currentIndex = 0;

        RelativeMomentumIndexIndicator owner = (RelativeMomentumIndexIndicator) this.owner.getPresenter();
        ChartSeriesModel model = this.owner;

        int period = owner.getPeriod();
        int momentumShift = owner.getMomentumPeriod();

        SizedQueue values = new SizedQueue();
        values.size = momentumShift;

        SizedQueue upMomentumValues = new SizedQueue();
        upMomentumValues.size = period;

        SizedQueue downMomentumValues = new SizedQueue();
        downMomentumValues.size = period;

        double oldValue, value, currentIndicatorValue;
        double upMomentumAverage, downMomentumAverage;
        double up, down;

        for (Object item : this.itemsSource) {
            value = ((Number) this.valueBinding.getValue(item)).doubleValue();

            if (currentIndex == 0)
                oldValue = value;
            else if (currentIndex < momentumShift)
                oldValue = values.peek();
            else
                oldValue = values.dequeueItem();

            if (oldValue > value) {
                up = 0;
                down = oldValue - value;
            } else {
                up = value - oldValue;
                down = 0;
            }

            upMomentumValues.enqueueItem(up);
            downMomentumValues.enqueueItem(down);

            upMomentumAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(upMomentumValues);
            downMomentumAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(downMomentumValues);

            if (Math.round(upMomentumAverage + downMomentumAverage) == 0)
                currentIndicatorValue = 100;
            else
                currentIndicatorValue = 100 * upMomentumAverage / (upMomentumAverage + downMomentumAverage);

            values.enqueueItem(value);

            CategoricalDataPoint point;
            DataPointCollection dataPoints = this.owner.dataPoints();
            if (dataPoints.size() > currentIndex) {
                point = (CategoricalDataPoint) dataPoints.get(currentIndex);
                point.setValue(currentIndicatorValue);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(currentIndicatorValue);
                dataPoints.add(point);
            }

            currentIndex++;
        }
    }
}
