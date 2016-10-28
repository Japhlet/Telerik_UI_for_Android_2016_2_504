package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.MacdIndicator;

public class MacdIndicatorDataSource extends ShortLongPeriodIndicatorDataSourceBase {

    private int signalPeriod;
    private double currentLongEMA;
    double currentShortEMA;

    public MacdIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public int getSignalPeriod() {
        return this.signalPeriod;
    }

    public void setSignalPeriod(int signalPeriod) {
        if (this.signalPeriod == signalPeriod)
            return;

        this.signalPeriod = signalPeriod;

        if (this.itemsSource != null)
            this.rebind(false, null);
    }


    protected static double calculateSignal(int signalPeriod, SizedQueue signalPeriodItems, int currentIndex, double signalEMA, double macd) {
        if (currentIndex < signalPeriod)
            signalEMA = MovingAverageIndicatorDataSource.calculateCurrentValue(signalPeriodItems);
        else
            signalEMA = ExponentialMovingAverageIndicatorDataSource.calculateCurrentValue(false, signalPeriod, macd, signalEMA);
        return signalEMA;
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    @Override
    protected void bindCore() {
        MacdIndicator owner = (MacdIndicator) this.owner.getPresenter();
        SizedQueue longPeriodItems = new SizedQueue();
        longPeriodItems.size = owner.getLongPeriod();

        SizedQueue shortPeriodItems = new SizedQueue();
        shortPeriodItems.size = owner.getShortPeriod();

        SizedQueue signalPeriodItems = new SizedQueue();
        signalPeriodItems.size = owner.getSignalPeriod();

        this.generateDataPoints(owner.getLongPeriod(), longPeriodItems, owner.getShortPeriod(), shortPeriodItems, owner.getSignalPeriod(), signalPeriodItems);
    }

    protected void generateDataPoints(int longPeriod, SizedQueue longPeriodItems, int shortPeriod, SizedQueue shortPeriodItems, int signalPeriod, SizedQueue signalPeriodItems) {
        ChartSeriesModel signalModel = ((MacdIndicator) this.owner.getPresenter()).signalModel();

        int currentIndex = 0;
        CategoricalDataPoint point, point2;
        double signalEMA = 0d;
        double macd;

        for (Object item : this.itemsSource) {
            macd = this.calculateMacdValue(longPeriod, longPeriodItems, shortPeriod, shortPeriodItems, currentIndex, item);
            signalPeriodItems.enqueueItem(macd);

            signalEMA = calculateSignal(signalPeriod, signalPeriodItems, currentIndex, signalEMA, macd);

            if (this.owner.dataPoints().size() > currentIndex) {
                point = (CategoricalDataPoint) this.owner.dataPoints().get(currentIndex);
                point.setValue(macd);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(macd);
                this.owner.dataPoints().add(point);
            }

            if (signalModel.dataPoints().size() > currentIndex) {
                point2 = (CategoricalDataPoint) signalModel.dataPoints().get(currentIndex);
                point2.setValue(signalEMA);
            } else {
                point2 = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point2.setValue(signalEMA);
                signalModel.dataPoints().add(point2);
            }

            currentIndex++;
        }
    }

    protected double calculateMacdValue(int longPeriod, SizedQueue longPeriodItems, int shortPeriod, SizedQueue shortPeriodItems, int currentIndex, Object item) {
        double value = ((Number) this.valueBinding.getValue(item)).doubleValue();
        longPeriodItems.enqueueItem(value);
        shortPeriodItems.enqueueItem(value);

        if (currentIndex < longPeriod)
            this.currentLongEMA = MovingAverageIndicatorDataSource.calculateCurrentValue(longPeriodItems);
        else
            this.currentLongEMA = ExponentialMovingAverageIndicatorDataSource.calculateCurrentValue(false, longPeriod, value, this.currentLongEMA);

        if (currentIndex < shortPeriod)
            this.currentShortEMA = MovingAverageIndicatorDataSource.calculateCurrentValue(shortPeriodItems);
        else
            this.currentShortEMA = ExponentialMovingAverageIndicatorDataSource.calculateCurrentValue(false, shortPeriod, value, this.currentShortEMA);

        return this.currentShortEMA - this.currentLongEMA;
    }

    @Override
    protected void unbind() {
        super.unbind();

        ((MacdIndicator) this.owner.getPresenter()).signalModel().dataPoints().clear();
    }
}
