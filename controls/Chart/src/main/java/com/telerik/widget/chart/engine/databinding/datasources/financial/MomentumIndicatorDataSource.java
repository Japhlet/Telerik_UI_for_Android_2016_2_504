package com.telerik.widget.chart.engine.databinding.datasources.financial;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.MomentumIndicator;

import java.util.ArrayList;
import java.util.List;

public class MomentumIndicatorDataSource extends ValuePeriodIndicatorDataSourceBase {

    public MomentumIndicatorDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    protected double calculateValue(double currentValue, double olderValue) {
        return (currentValue / olderValue) * 100;
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        int index = this.bindings.indexOf(binding);
        Object item = binding.getDataItem();
        double value = ((Number) this.valueBinding.getValue(item)).doubleValue();
        double previousItemValue, nextItemValue;
        int period = ((MomentumIndicator) this.owner.getPresenter()).getPeriod();

        Object previousItem, nextItem;
        if (index < period)
            previousItem = this.bindings.get(0).getDataItem();
        else
            previousItem = this.bindings.get(index - period).getDataItem();

        previousItemValue = ((Number) this.valueBinding.getValue(previousItem)).doubleValue();

        double currentValue = this.calculateValue(value, previousItemValue);
        ((CategoricalDataPoint) binding.getDataPoint()).setValue(currentValue);

        if (index + period < this.bindings.size()) {
            DataPointBindingEntry nextItemBinding = this.bindings.get(index + period);
            nextItem = nextItemBinding.getDataItem();
            nextItemValue = ((Number) this.valueBinding.getValue(nextItem)).doubleValue();
            double nextValue = this.calculateValue(nextItemValue, value);
            ((CategoricalDataPoint) nextItemBinding.getDataPoint()).setValue(nextValue);
        }
    }

    @Override
    protected void bindCore() {
        MomentumIndicator indicator = (MomentumIndicator) this.owner.getPresenter();
        int period = indicator.getPeriod();
        List<Double> currentItems = new ArrayList<Double>();

        for (Object item : this.itemsSource) {
            if (currentItems.size() >= period + 1)
                currentItems.remove(0);

            currentItems.add(((Number) this.valueBinding.getValue(item)).doubleValue());

            CategoricalDataPoint point;
            point = (CategoricalDataPoint) this.generateDataPoint(item, -1);
            point.setValue(this.calculateValue(currentItems.get(currentItems.size() - 1), currentItems.get(0)));
            this.owner.dataPoints().add(point);
        }
    }
}
