package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.ValuePeriodIndicatorBase;

public class ExponentialMovingAverageIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    public ExponentialMovingAverageIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    private boolean isModified;

    public boolean isModified() {
        return this.isModified;
    }

    public void setModified(boolean isModified) {
        this.isModified = isModified;
    }

    public static double calculateCurrentValue(boolean isModified, int period, double value, double prevEMA) {
        double multiplier = isModified ? 1D / period : 2D / (1 + period);
        return calculateCurrentValue(multiplier, value, prevEMA);
    }

    @Override
    protected void bindCore() {
        int period = ((ValuePeriodIndicatorBase) this.owner.getPresenter()).getPeriod();
        SizedQueue currentItems = new SizedQueue();
        currentItems.size = period;

        double currentAverage;
        double prevEMA = 0;
        int currentIndex = 0;
        double multiplier = this.isModified ? 1D / period : 2D / (1 + period);

        for (Object item : this.itemsSource) {
            double value = ((Number) this.valueBinding.getValue(item)).doubleValue();

            //// The first values are calculated as SMA
            if (currentIndex < period) {
                currentItems.enqueueItem(value);
                currentAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItems);
            } else {
                currentAverage = calculateCurrentValue(multiplier, value, prevEMA);
            }

            prevEMA = currentAverage;

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

            currentIndex++;
        }
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    private static double calculateCurrentValue(double multiplier, double value, double prevEMA) {
        return prevEMA + (multiplier * (value - prevEMA));
    }
}
