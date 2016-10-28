package com.telerik.widget.chart.engine.databinding.datasources;

import android.graphics.Point;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.RangeDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

/**
 * Base class that holds the low and high data point bindings used in all range series.
 */
public class RangeSeriesDataSourceBase extends CategoricalSeriesDataSourceBase {

    private DataPointBinding lowBinding;
    private DataPointBinding highBinding;

    /**
     * Initializes a new instance of the {@link RangeSeriesDataSourceBase} using a given chart
     * series owner.
     *
     * @param owner the chart series this data source belongs to.
     */
    public RangeSeriesDataSourceBase(ChartSeriesModel owner) {
        super(owner);
    }

    /**
     * Gets the current low data point binding.
     *
     * @return the current low data point binding.
     */
    public DataPointBinding getLowBinding() {
        return this.lowBinding;
    }

    /**
     * Sets the current low data point binding.
     *
     * @param value the new low data point binding.
     */
    public void setLowBinding(DataPointBinding value) {
        if (this.lowBinding == value) {
            return;
        }

        this.lowBinding = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }

    /**
     * Gets the current high data point binding.
     *
     * @return the current high data point binding.
     */
    public DataPointBinding getHighBinding() {
        return this.highBinding;
    }

    /**
     * Sets the current high data point binding.
     *
     * @param value the new high data point binding.
     */
    public void setHighBinding(DataPointBinding value) {
        if (this.highBinding == value) {
            return;
        }

        this.highBinding = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }

    @Override
    protected DataPoint createDataPoint() {
        return new RangeDataPoint();
    }

    @Override
    protected void processDouble(DataPoint point, double value) {
        RangeDataPoint rangeDataPoint = (RangeDataPoint) point;
        rangeDataPoint.setLow(Math.min(0, value));
        rangeDataPoint.setHigh(Math.max(0, value));
    }

    @Override
    protected void processDoubleArray(DataPoint point, double[] values) {
        if (values.length == 2) {
            RangeDataPoint rangeDataPoint = (RangeDataPoint) point;
            rangeDataPoint.setLow(values[0]);
            rangeDataPoint.setHigh(Math.max(values[1], rangeDataPoint.getLow()));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void processPoint(DataPoint dataPoint, Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void processSize(DataPoint point, RadSize size) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void initializeBinding(DataPointBindingEntry binding) {
        boolean highIsValidNumber = true;
        boolean lowIsValidNumber = true;

        RangeDataPoint rangeDataPoint = (RangeDataPoint) binding.getDataPoint();

        if (this.highBinding != null) {
            Object value = this.highBinding.getValue(binding.getDataItem());
            if (value instanceof Number) {
                rangeDataPoint.setHigh(((Number) value).doubleValue());
            } else {
                rangeDataPoint.setHigh(0F);
                highIsValidNumber = false;
            }
        }

        if (this.lowBinding != null) {
            Object value = this.lowBinding.getValue(binding.getDataItem());
            if (value instanceof Number) {
                rangeDataPoint.setLow(((Number) value).doubleValue());
            } else {
                rangeDataPoint.setLow(0F);
                lowIsValidNumber = false;
            }
        }

        rangeDataPoint.isEmpty = !lowIsValidNumber && !highIsValidNumber;
        super.initializeBinding(binding);
    }
}
