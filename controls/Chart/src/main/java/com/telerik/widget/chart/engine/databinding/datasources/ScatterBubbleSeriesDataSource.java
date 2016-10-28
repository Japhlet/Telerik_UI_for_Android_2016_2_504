package com.telerik.widget.chart.engine.databinding.datasources;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.ScatterBubbleDataPoint;
import com.telerik.widget.chart.engine.dataPoints.ScatterDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public class ScatterBubbleSeriesDataSource extends ScatterSeriesDataSource {
    private DataPointBinding bubbleSizeBinding;

    public ScatterBubbleSeriesDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public DataPointBinding getBubbleSizeBinding() {
        return this.bubbleSizeBinding;
    }

    public void setBubbleSizeBinding(DataPointBinding value) {
        if(this.bubbleSizeBinding == value) {
            return;
        }

        this.bubbleSizeBinding = value;

        if(this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    @Override
    protected void processDoubleArray(DataPoint dataPoint, double[] values) {
        super.processDoubleArray(dataPoint, values);
        ScatterBubbleDataPoint point = (ScatterBubbleDataPoint) dataPoint;

        if(values.length > 2) {
            point.setSize(values[2]);
        }
    }

    @Override
    protected DataPoint createDataPoint() {
        return new ScatterBubbleDataPoint();
    }

    @Override
    protected void initializeBinding(DataPointBindingEntry binding) {
        super.initializeBinding(binding);

        ScatterBubbleDataPoint point = (ScatterBubbleDataPoint) binding.getDataPoint();
        if(this.bubbleSizeBinding != null) {
            Object sizeValue = this.bubbleSizeBinding.getValue(binding.getDataItem());
            if(sizeValue instanceof Number) {
                point.setSize(((Number) sizeValue).doubleValue());
            }
        }
    }
}
