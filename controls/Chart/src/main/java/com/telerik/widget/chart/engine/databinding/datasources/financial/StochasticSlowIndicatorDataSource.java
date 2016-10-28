package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.StochasticIndicatorDataSourceBase;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.StochasticSlowIndicator;

public class StochasticSlowIndicatorDataSource extends StochasticIndicatorDataSourceBase {
    private int slowingPeriod;

    public StochasticSlowIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public int getSlowingPeriod() {
        return this.slowingPeriod;
    }

    public void setSlowingPeriod(int value) {
        if (this.slowingPeriod == value) {
            return;
        }

        this.slowingPeriod = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    @Override
    protected void bindCore() {
        int currentIndex = 0;

        StochasticSlowIndicator owner = (StochasticSlowIndicator) this.owner.getPresenter();
        ChartSeriesModel signalModel = owner.signalModel();

        int mainPeriod = owner.getMainPeriod();
        int slowingPeriod = owner.getSlowingPeriod();
        int signalPeriod = owner.getSignalPeriod();

        SizedQueue highValues = new SizedQueue();
        highValues.size = mainPeriod;
        SizedQueue lowValues = new SizedQueue();
        lowValues.size = mainPeriod;
        SizedQueue fastStochValues = new SizedQueue();
        fastStochValues.size = slowingPeriod;
        SizedQueue slowStochValues = new SizedQueue();
        slowStochValues.size = signalPeriod;

        for (Object item : this.itemsSource) {
            double high = ((Number) this.getHighBinding().getValue(item)).doubleValue();
            double low = ((Number) this.getLowBinding().getValue(item)).doubleValue();
            double close = ((Number) this.getCloseBinding().getValue(item)).doubleValue();

            double fastStochValue = calculateMainValue(highValues, lowValues, high, low, close);
            fastStochValues.enqueueItem(fastStochValue);

            double slowStochValue = MovingAverageIndicatorDataSource.calculateCurrentValue(fastStochValues);
            slowStochValues.enqueueItem(slowStochValue);

            double slowSignalValue = MovingAverageIndicatorDataSource.calculateCurrentValue(slowStochValues);

            CategoricalDataPoint point, point2;
            if (this.owner.dataPoints().size() > currentIndex) {
                point = (CategoricalDataPoint) this.owner.dataPoints().get(currentIndex);
                point.setValue(slowStochValue);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(slowStochValue);
                this.owner.dataPoints().add(point);
            }

            if (signalModel.dataPoints().size() > currentIndex) {
                point2 = (CategoricalDataPoint) signalModel.dataPoints().get(currentIndex);
                point2.setValue(slowSignalValue);
            } else {
                point2 = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point2.setValue(slowSignalValue);
                signalModel.dataPoints().add(point2);
            }

            currentIndex++;
        }
    }

    private static double calculateMainValue(SizedQueue highValues, SizedQueue lowValues, double high, double low, double close) {
        highValues.enqueueItem(high);
        lowValues.enqueueItem(low);
        double max = highValues.max();
        double min = lowValues.min();

        return (close - min) / (max - min) * 100;
    }
}
