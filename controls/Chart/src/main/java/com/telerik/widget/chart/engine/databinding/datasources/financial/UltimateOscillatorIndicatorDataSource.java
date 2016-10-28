package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.UltimateOscillatorIndicator;

public class UltimateOscillatorIndicatorDataSource extends HighLowClosePeriodIndicatorDataSourceBase {
    public UltimateOscillatorIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    private int period2;
    private int period3;

    public int getPeriod2() {
        return this.period2;
    }

    public void setPeriod2(int value) {
        if (this.period2 == value) {
            return;
        }

        this.period2 = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    public int getPeriod3() {
        return this.period3;
    }

    public void setPeriod3(int value) {
        if (this.period3 == value) {
            return;
        }

        this.period3 = value;

        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    public static double calculateCurrentValue(SizedQueue items) {
        return items.runningSum / items.currentItemsCount;
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.bindCore();
    }

    @Override
    protected void bindCore() {
        UltimateOscillatorIndicator indicator = (UltimateOscillatorIndicator) this.owner.getPresenter();

        int period = indicator.getPeriod();
        int period2 = indicator.getPeriod2();
        int period3 = indicator.getPeriod3();

        SizedQueue items = new SizedQueue();
        items.size = period;

        SizedQueue items2 = new SizedQueue();
        items2.size = period2;

        SizedQueue items3 = new SizedQueue();
        items3.size = period3;

        SizedQueue trueRangeItems = new SizedQueue();
        trueRangeItems.size = period;
        SizedQueue trueRangeItems2 = new SizedQueue();
        trueRangeItems2.size = period2;
        SizedQueue trueRangeItems3 = new SizedQueue();
        trueRangeItems3.size = period3;

        int currentIndex = 0;
        double trueHigh, trueLow, trueRange;
        double high, low, close, range;
        double average1, average2, average3;
        double trueRangeAverage1, trueRangeAverage2, trueRangeAverage3;

        double previousClose = 0;

        for (Object item : this.itemsSource) {
            high = ((Number) this.getHighBinding().getValue(item)).doubleValue();
            low = ((Number) this.getLowBinding().getValue(item)).doubleValue();
            close = ((Number) this.getCloseBinding().getValue(item)).doubleValue();

            if (currentIndex == 0)
                previousClose = close;

            trueHigh = Math.max(high, previousClose);
            trueLow = Math.min(low, previousClose);
            trueRange = trueHigh - trueLow;
            range = close - trueLow;

            items.enqueueItem(range);
            items2.enqueueItem(range);
            items3.enqueueItem(range);

            trueRangeItems.enqueueItem(trueRange);
            trueRangeItems2.enqueueItem(trueRange);
            trueRangeItems3.enqueueItem(trueRange);

            average1 = MovingAverageIndicatorDataSource.calculateCurrentValue(items);
            average2 = MovingAverageIndicatorDataSource.calculateCurrentValue(items2);
            average3 = MovingAverageIndicatorDataSource.calculateCurrentValue(items3);

            trueRangeAverage1 = MovingAverageIndicatorDataSource.calculateCurrentValue(trueRangeItems);
            trueRangeAverage2 = MovingAverageIndicatorDataSource.calculateCurrentValue(trueRangeItems2);
            trueRangeAverage3 = MovingAverageIndicatorDataSource.calculateCurrentValue(trueRangeItems3);

            double value = 100 * ((4 * average1 / trueRangeAverage1) + (2 * average2 / trueRangeAverage2) + (average3 / trueRangeAverage3)) / 7D;

            CategoricalDataPoint point;
            if (this.owner.dataPoints().size() > currentIndex) {
                point = (CategoricalDataPoint) this.owner.dataPoints().get(currentIndex);
                point.setValue(value);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(value);
                this.owner.dataPoints().add(point);
            }

            previousClose = close;
            currentIndex++;
        }
    }
}