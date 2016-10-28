package com.telerik.widget.chart.engine.databinding.datasources;

import android.graphics.Point;

import com.telerik.android.common.Function;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

import java.util.HashMap;

public class OhlcSeriesDataSource extends CategoricalSeriesDataSourceBase {

    private DataPointBinding highBinding;
    private DataPointBinding lowBinding;
    private DataPointBinding openBinding;
    private DataPointBinding closeBinding;

    public OhlcSeriesDataSource(ChartSeriesModel owner) {
        super(owner);
    }

    public DataPointBinding getHighBinding() {
        return this.highBinding;
    }

    public void setHighBinding(DataPointBinding highBinding) {
        if (highBinding == null)
            throw new NullPointerException("highBinding");

        if (this.highBinding == highBinding)
            return;

        this.highBinding = highBinding;

        if (this.itemsSource != null)
            this.rebind(false, null);
    }

    public DataPointBinding getLowBinding() {
        return this.lowBinding;
    }

    public void setLowBinding(DataPointBinding lowBinding) {
        if (lowBinding == null)
            throw new NullPointerException("lowBinding");

        if (this.lowBinding == lowBinding)
            return;

        this.lowBinding = lowBinding;

        if (this.itemsSource != null)
            this.rebind(false, null);
    }

    public DataPointBinding getOpenBinding() {
        return this.openBinding;
    }

    public void setOpenBinding(DataPointBinding openBinding) {
        if (openBinding == null)
            throw new NullPointerException("openBinding");

        if (this.openBinding == openBinding)
            return;

        this.openBinding = openBinding;

        if (this.itemsSource != null)
            this.rebind(false, null);
    }

    public DataPointBinding getCloseBinding() {
        return this.closeBinding;
    }

    public void setCloseBinding(DataPointBinding closeBinding) {
        if (closeBinding == null)
            throw new NullPointerException("closeBinding");

        if (this.closeBinding == closeBinding)
            return;

        this.closeBinding = closeBinding;

        if (this.itemsSource != null)
            this.rebind(false, null);
    }

    @Override
    protected DataPoint createDataPoint() {
        return new OhlcDataPoint();
    }

    @Override
    protected void processDouble(DataPoint dataPoint, double value) {
        throw new UnsupportedOperationException("processing double values is not supported");
    }

    @Override
    protected void processDoubleArray(DataPoint dataPoint, double[] values) {
        throw new UnsupportedOperationException("processing double array is not supported");
    }

    @Override
    protected void processSize(DataPoint dataPoint, RadSize size) {
        throw new UnsupportedOperationException("processing size is not supported");
    }

    @Override
    protected void processPoint(DataPoint dataPoint, Point point) {
        throw new UnsupportedOperationException("processing point is not supported");
    }

    @Override
    protected void initializeBinding(DataPointBindingEntry binding) {
        Number value;
        OhlcDataPoint dataPoint = (OhlcDataPoint) binding.getDataPoint();

        if (this.highBinding != null) {
            value = (Number) this.highBinding.getValue(binding.getDataItem());

            if (value != null) {
                dataPoint.setHigh(value.doubleValue());
            } else {
                dataPoint.isEmpty = true;
            }
        }

        if (this.lowBinding != null) {
            value = (Number) this.lowBinding.getValue(binding.getDataItem());
            if (value != null) {
                dataPoint.setLow(value.doubleValue());
            } else {
                dataPoint.isEmpty = true;
            }
        }

        if (this.openBinding != null) {
            value = (Number) this.openBinding.getValue(binding.getDataItem());
            if (value != null) {
                dataPoint.setOpen(value.doubleValue());
            } else {
                dataPoint.isEmpty = true;
            }
        }

        if (this.closeBinding != null) {
            value = (Number) this.closeBinding.getValue(binding.getDataItem());
            if (value != null) {
                dataPoint.setClose(value.doubleValue());
            } else {
                dataPoint.isEmpty = true;
            }
        }

        if (dataPoint.getHigh() < dataPoint.getLow()) {
            throw new IllegalStateException("OHLC data cannot contain such values: high < low.");
        }

        super.initializeBinding(binding);
    }

}
