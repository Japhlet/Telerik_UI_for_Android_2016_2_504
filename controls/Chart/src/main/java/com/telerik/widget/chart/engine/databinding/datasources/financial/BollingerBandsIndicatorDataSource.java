package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.BollingerBandsIndicator;

public class BollingerBandsIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    private int standardDeviations;

    public BollingerBandsIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public int getStandardDeviations() {
        return this.standardDeviations;
    }

    public void setStandardDeviations(int standardDeviations) {
        if (this.standardDeviations == standardDeviations)
            return;

        this.standardDeviations = standardDeviations;

        if (this.itemsSource != null)
            this.rebind(false, null);
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    @Override
    protected void bindCore() {
        BollingerBandsIndicator owner = (BollingerBandsIndicator) this.getOwner().getPresenter();
        double deviations = owner.getStandardDeviations();
        SizedQueue currentItems = new SizedQueue();
        currentItems.size = owner.getPeriod();

        int currentIndex = 0;
        double stdDeviation = 0;

        for (Object item : this.itemsSource) {
            Object val = this.valueBinding.getValue(item);
            currentItems.enqueueItem(((Number) val).doubleValue());
            double currentAverage = MovingAverageIndicatorDataSource.calculateCurrentValue(currentItems);
            stdDeviation = calculateStandardDeviation(currentItems, currentAverage);
            CategoricalDataPoint point, secondPoint;
            if (this.owner.dataPoints().size() > currentIndex) {
                point = (CategoricalDataPoint) this.owner.dataPoints().get(currentIndex);
                point.setValue(currentAverage + (deviations * stdDeviation));

                secondPoint = (CategoricalDataPoint) owner.lowerBandModel().dataPoints().get(currentIndex);
                secondPoint.setValue(currentAverage - (deviations * stdDeviation));
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(currentAverage + (deviations * stdDeviation));
                owner.getDataPoints().add(point);

                secondPoint = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                secondPoint.setValue(currentAverage - (deviations * stdDeviation));
                owner.lowerBandModel().dataPoints().add(secondPoint);
            }

            currentIndex++;
        }
    }

    @Override
    protected void unbind() {
        super.unbind();
        ((BollingerBandsIndicator) this.owner.getPresenter()).lowerBandModel().dataPoints().clear();
    }

    private static double calculateStandardDeviation(SizedQueue items, double average) {
        double sum = 0;
        for (double item : items) {
            double entry = Math.pow(item - average, 2);
            sum += entry;
        }

        return Math.sqrt(sum / items.size);
    }
}
