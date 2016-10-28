package com.telerik.widget.chart.engine.databinding.datasources;

import android.graphics.Point;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.ScatterDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

public class ScatterSeriesDataSource extends ChartSeriesDataSource {
    private DataPointBinding yValueBinding;
    private DataPointBinding xValueBinding;

    public ScatterSeriesDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public DataPointBinding getXValueBinding() {
        return this.xValueBinding;
    }

    public DataPointBinding getYValueBinding() {
        return this.yValueBinding;
    }

    public void setXValueBinding(DataPointBinding value) {
        if(this.xValueBinding == value) {
            return;
        }

        this.xValueBinding = value;

        if(this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    public void setYValueBinding(DataPointBinding value) {
        if(this.yValueBinding == value) {
            return;
        }

        this.yValueBinding = value;

        if(this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    @Override
    protected DataPoint createDataPoint() {
        return new ScatterDataPoint();
    }

    @Override
    protected void processDouble(DataPoint dataPoint, double value) {
        throw new IllegalArgumentException("The scatter series don't support binding to primitive types.");
    }

    @Override
    protected void processDoubleArray(DataPoint dataPoint, double[] values) {
        ScatterDataPoint point = (ScatterDataPoint)dataPoint;

        if(values.length > 0) {
            point.setXValue(values[0]);
        }

        if(values.length > 1) {
            point.setYValue(values[1]);
        }
    }

    @Override
    protected void processSize(DataPoint dataPoint, RadSize size) {
        ScatterDataPoint point = (ScatterDataPoint)dataPoint;
        point.setXValue(size.getWidth());
        point.setYValue(size.getHeight());
    }

    @Override
    protected void processPoint(DataPoint dataPoint, Point point) {
        ScatterDataPoint scatterPoint = (ScatterDataPoint)dataPoint;
        scatterPoint.setXValue(point.x);
        scatterPoint.setYValue(point.y);
    }

    @Override
    protected void initializeBinding(DataPointBindingEntry binding) {
        ScatterDataPoint point = (ScatterDataPoint)binding.getDataPoint();

        if(this.xValueBinding != null) {
            Object xValue = this.xValueBinding.getValue(binding.getDataItem());
            if(xValue instanceof Number) {
                point.setXValue(((Number)xValue).doubleValue());
            }
        }

        if(this.yValueBinding != null) {
            Object yValue = this.yValueBinding.getValue(binding.getDataItem());
            if(yValue instanceof Number) {
                point.setYValue(((Number)yValue).doubleValue());
            }
        }
    }
}
