package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.AdaptiveMovingAverageKaufmanIndicator;

public class AdaptiveMovingAverageKaufmanIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    private int slowPeriod;
    private int fastPeriod;

    public AdaptiveMovingAverageKaufmanIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public int getSlowPeriod() {
        return this.slowPeriod;
    }

    public void setSlowPeriod(int slowPeriod) {
        if (this.slowPeriod == slowPeriod)
            return;

        this.slowPeriod = slowPeriod;
        if (this.itemsSource != null)
            this.rebind(false, null);
    }

    public int getFastPeriod() {
        return this.fastPeriod;
    }

    public void setFastPeriod(int fastPeriod) {
        if (this.fastPeriod == fastPeriod)
            return;

        this.fastPeriod = fastPeriod;
        if (this.itemsSource != null)
            this.rebind(false, null);
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    @Override
    protected void bindCore() {
        AdaptiveMovingAverageKaufmanIndicator indicator = (AdaptiveMovingAverageKaufmanIndicator) this.owner.getPresenter();
        int period = indicator.getPeriod();

        double currentAverage;
        double prevKAMA = 0;
        int currentIndex = 0;

        SizedQueue currentItems = new SizedQueue();
        currentItems.size = period;

        for (Object item : this.itemsSource) {
            double value = ((Number) this.valueBinding.getValue(item)).doubleValue();
            currentItems.add(value);

            //// The raw value is used for the first elements
            if (currentIndex < period) {
                currentAverage = value;
                prevKAMA = value;
            } else {
                currentAverage = calculateCurrentValue(currentItems, indicator.getSlowPeriod(), indicator.getFastPeriod(), prevKAMA);
                prevKAMA = currentAverage;
            }

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

    private static double calculateCurrentValue(SizedQueue currentItems, int slow, int fast, double prevKAMA) {
        int period = currentItems.size();
        Double[] itemsArray = new Double[currentItems.size()];
        currentItems.toArray(itemsArray);

        double diff = Math.abs(itemsArray[period - 1] - itemsArray[0]);
        double rangesSum = 0;

        for (int i = 1; i < itemsArray.length; i++)
            rangesSum += Math.abs(itemsArray[i] - itemsArray[i - 1]);

        double efficiencyRatio = diff / rangesSum;

        double fastConstant = 2D / (fast + 1);
        double slowConstant = 2D / (slow + 1);
        double weight = Math.pow((efficiencyRatio * (fastConstant - slowConstant)) + slowConstant, 2);

        return prevKAMA + (weight * (currentItems.peekLast() - prevKAMA));
    }
}
