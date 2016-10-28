/*
package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.databinding.PropertyNameDataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.CategoricalSeriesDataSource;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.MacdhIndicator;

import java.beans.PropertyChangeEvent;

public class MacdhIndicatorDataSource extends CategoricalSeriesDataSource {

    public MacdhIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    private int longPeriod;
    private int shortPeriod;
    private int signalPeriod;

    private double currentLongEMA;
    private double currentShortEMA;

    public int getLongPeriod() {
        return this.longPeriod;
    }

    public void setLongPeriod(int value) {
        if (this.longPeriod == value) {
            return;
        }

        this.longPeriod = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    public int getShortPeriod() {
        return this.shortPeriod;
    }

    public void setShortPeriod(int value) {
        if (this.shortPeriod == value) {
            return;
        }

        this.shortPeriod = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    public int getSignalPeriod() {

        return this.signalPeriod;
    }

    public void setSignalPeriod(int value) {
        if (this.signalPeriod == value) {
            return;
        }

        this.signalPeriod = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
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
        if (this.owner.getPresenter() == null)
            return;

        MacdhIndicator owner = (MacdhIndicator) this.owner.getPresenter();
        SizedQueue longPeriodItems = new SizedQueue();
        longPeriodItems.size = owner.getLongPeriod();

        SizedQueue shortPeriodItems = new SizedQueue();
        shortPeriodItems.size = owner.getShortPeriod();

        int signalPeriod = owner.getSignalPeriod();
        SizedQueue signalPeriodItems = new SizedQueue();
        signalPeriodItems.size = signalPeriod;

        this.generateDataPoints(longPeriod, longPeriodItems, shortPeriod, shortPeriodItems, signalPeriod, signalPeriodItems);
    }

    protected void generateDataPoints(int longPeriod, SizedQueue longPeriodItems, int shortPeriod, SizedQueue shortPeriodItems, int signalPeriod, SizedQueue signalPeriodItems) {
        int currentIndex = 0;
        CategoricalDataPoint point;
        double signalEMA = 0;
        double macd;

        for (Object item : this.itemsSource) {
            macd = this.calculateMacdValue(longPeriod, longPeriodItems, shortPeriod, shortPeriodItems, currentIndex, item);
            signalPeriodItems.enqueueItem(macd);

            signalEMA = calculateSignal(signalPeriod, signalPeriodItems, currentIndex, signalEMA, macd);

            DataPointCollection dataPoints = this.owner.dataPoints();
            if (dataPoints.size() > currentIndex) {
                point = (CategoricalDataPoint) dataPoints.get(currentIndex);
                point.setValue(macd - signalEMA);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(macd - signalEMA);
                dataPoints.add(point);
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
    protected void onBoundItemPropertyChanged(PropertyChangeEvent e) {
        PropertyNameDataPointBinding propertyNameValueBinding = (PropertyNameDataPointBinding) ((MacdhIndicator) this.owner.getPresenter()).getValueBinding();
        PropertyNameDataPointBinding propertyNameCategoryBinding = (PropertyNameDataPointBinding) ((MacdhIndicator) this.owner.getPresenter()).getCategoryBinding();

        if (propertyNameValueBinding != null && propertyNameCategoryBinding != null &&
                !e.getPropertyName().equals(propertyNameValueBinding.getPropertyName()) && !e.getPropertyName().equals(propertyNameCategoryBinding.getPropertyName())) {
            return;
        }

        super.onBoundItemPropertyChanged(e);
    }
}
*/
