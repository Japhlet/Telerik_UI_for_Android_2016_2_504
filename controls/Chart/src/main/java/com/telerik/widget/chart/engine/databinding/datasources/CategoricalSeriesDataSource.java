package com.telerik.widget.chart.engine.databinding.datasources;

import android.graphics.Point;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

/**
 * Represents a {@link CategoricalSeriesDataSourceBase} and adds the value binding functionality.
 */
public class CategoricalSeriesDataSource extends CategoricalSeriesDataSourceBase {

    protected DataPointBinding valueBinding;

    /**
     * Initializes a new instance of the {@link CategoricalSeriesDataSource} with a given chart
     * series owner.
     *
     * @param owner the chart series this data source belongs to.
     */
    public CategoricalSeriesDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    /**
     * Gets the current value binding.
     *
     * @return the current value binding.
     */
    public DataPointBinding getValueBinding() {
        return this.valueBinding;
    }

    /**
     * Sets the current value binding.
     *
     * @param value the new value binding.
     */
    public void setValueBinding(DataPointBinding value) {
        if (this.valueBinding == value) {
            return;
        }

        this.valueBinding = value;
        if (this.itemsSource != null) {
            this.rebind(false, null);
        }
    }

    @Override
    protected void initializeBinding(DataPointBindingEntry binding) {
        if (this.valueBinding != null) {

            Object value = this.valueBinding.getValue(binding.getDataItem());

            if (value instanceof Number) {
                ((CategoricalDataPoint) binding.getDataPoint()).setValue(((Number) value).doubleValue());
            }
        }

        super.initializeBinding(binding);
    }

    @Override
    protected DataPoint createDataPoint() {
        return new CategoricalDataPoint();
    }

    @Override
    protected void processDouble(DataPoint point, double value) {
        ((CategoricalDataPoint) point).setValue(value);
    }

    @Override
    protected void processDoubleArray(DataPoint point, double[] values) {
        if (values.length > 0) {
            ((CategoricalDataPoint) point).setValue(values[0]);
        }
    }

    @Override
    protected void processSize(DataPoint point, RadSize size) {
        ((CategoricalDataPoint) point).setValue(size.getWidth());
    }

    @Override
    protected void processPoint(DataPoint dataPoint, Point point) {
        ((CategoricalDataPoint) dataPoint).setValue(point.x);
    }
}
