package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.common.ChartSeries;

public class TrueRangeIndicatorDataSource extends HighLowCloseIndicatorDataSourceBase {

    public TrueRangeIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public static double calculateValue(DataPointBinding highBinding, DataPointBinding lowBinding, DataPointBinding closeBinding, Object previousItem, Object currentItem) {
        double previousClose, high, low, value;

        high = ((Number) highBinding.getValue(currentItem)).doubleValue();
        low = ((Number) lowBinding.getValue(currentItem)).doubleValue();
        if (previousItem != null)
            previousClose = ((Number) closeBinding.getValue(previousItem)).doubleValue();
        else
            previousClose = ((Number) closeBinding.getValue(currentItem)).doubleValue();

        value = high - low;

        if (previousItem != null)
            value = calculateValue(previousClose, low, high);

        return value;
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        int index = this.bindings.indexOf(binding);
        Object item = binding.getDataItem();

        double high = ((Number) this.highBinding.getValue(item)).doubleValue();
        double low = ((Number) this.lowBinding.getValue(item)).doubleValue();
        double value = high - low;

        if (index > 0) {
            Object previousItem = this.bindings.get(index - 1).getDataItem();
            double previousClose = ((Number) this.closeBinding.getValue(previousItem)).doubleValue();
            value = calculateValue(previousClose, low, high);
        }

        ((CategoricalDataPoint) binding.getDataPoint()).setValue(value);

        DataPointBindingEntry nextBinding;
        if (index < this.bindings.size() - 1) {
            nextBinding = this.bindings.get(index + 1);
            Object nextItem = nextBinding.getDataItem();
            double nextHigh = ((Number) this.highBinding.getValue(nextItem)).doubleValue();
            double nextLow = ((Number) this.lowBinding.getValue(nextItem)).doubleValue();
            double close = ((Number) this.closeBinding.getValue(item)).doubleValue();

            double nextValue = calculateValue(close, nextLow, nextHigh);
            ((CategoricalDataPoint) nextBinding.getDataPoint()).setValue(nextValue);
        }

        super.updateBinding(binding);
    }

    @Override
    protected void bindCore() {
        double value;
        int currentIndex = 0;
        Object previousItem = null;
        for (Object item : this.itemsSource) {
            value = calculateValue(this.highBinding, this.lowBinding, this.closeBinding, previousItem, item);

            CategoricalDataPoint point;
            DataPointCollection dataPoints = ((ChartSeries) this.owner.getPresenter()).model().dataPoints();
            if (dataPoints.size() > currentIndex) {
                point = (CategoricalDataPoint) dataPoints.get(currentIndex);
                point.setValue(value);
            } else {
                point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
                point.setValue(value);
                dataPoints.add(point);
            }
            currentIndex++;

            previousItem = item;
        }
    }

    private static double calculateValue(double previousClose, double low, double high) {
        low = Math.min(low, previousClose);
        high = Math.max(high, previousClose);

        return high - low;
    }
}
