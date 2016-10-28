package com.telerik.widget.chart.engine.databinding.datasources;

import android.graphics.Point;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.SingleValueDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

/**
 * Represents a {@link ChartSeriesDataSource} that holds a single value binding.
 */
public abstract class SingleValuePointDataSource extends ChartSeriesDataSource {

    private DataPointBinding valueBinding;

    /**
     * Initializes a new instance of the {@link SingleValuePointDataSource} class using a given
     * chart series owner.
     *
     * @param owner the chart series this data source belongs to.
     */
    public SingleValuePointDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    /**
     * Gets the current value binding for the data source.
     *
     * @return the current value binding.
     */
    public DataPointBinding getValueBinding() {
        return this.valueBinding;
    }

    /**
     * Sets the current value binding for the data source.
     *
     * @param value the new value binding.
     */
    public void setValueBinding(DataPointBinding value)

    {
        if (this.valueBinding == value) {
            return;
        }

        this.valueBinding = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }


    @Override
    protected void processDouble(DataPoint point, double value) {
        ((SingleValueDataPoint) point).setValue(value);
    }

    @Override
    protected void processDoubleArray(DataPoint point, double[] values) {
        if (values.length > 0) {
            ((SingleValueDataPoint) point).setValue(values[0]);
        }
    }

    @Override
    protected void processSize(DataPoint point, RadSize size) {
        ((SingleValueDataPoint) point).setValue(size.getWidth());
    }

    @Override
    protected void processPoint(DataPoint dataPoint, Point point) {
        ((SingleValueDataPoint) dataPoint).setValue(point.x);
    }

    @Override
    protected void initializeBinding(DataPointBindingEntry binding) {
        if (this.valueBinding != null) {
            Object value = this.valueBinding.getValue(binding.getDataItem());
            if (value != null && !(value instanceof Number)) {
                throw new IllegalArgumentException(value + " is not a valid value. Use only valid Numbers.");
            }

            if (value != null) {
                ((SingleValueDataPoint) binding.getDataPoint()).setValue(((Number) value).doubleValue());
            }
        }
    }
}