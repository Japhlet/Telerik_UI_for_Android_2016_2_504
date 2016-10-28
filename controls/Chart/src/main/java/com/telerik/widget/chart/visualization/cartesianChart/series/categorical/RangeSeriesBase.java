package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import com.telerik.android.common.Function;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.GenericDataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.RangeSeriesDataSourceBase;

/**
 * Base class for all range series. See {@link RangeBarSeries} for example.
 */
public abstract class RangeSeriesBase extends CategoricalSeriesBase {
    private DataPointBinding lowBinding;
    private DataPointBinding highBinding;
    private RangeSeriesDataSourceBase rangeSource;

    /**
     * Creates a new instance of the {@link RangeSeriesBase} class.
     */
    public RangeSeriesBase() {
    }

    /**
     * Gets the binding that will be used to get the value of the {@link com.telerik.widget.chart.engine.dataPoints.RangeDataPoint#getLow()}
     * member of the contained data points.
     *
     * @return The low value binding.
     */
    public DataPointBinding getLowBinding() {
        return this.lowBinding;
    }

    /**
     * Sets the binding that will be used to get the value of the {@link com.telerik.widget.chart.engine.dataPoints.RangeDataPoint#getLow()}
     * member of the contained data points.
     *
     * @param value The low value binding.
     */
    public void setLowBinding(DataPointBinding value) {
        if (this.lowBinding == value) {
            return;
        }

        this.lowBinding = value;
        this.onLowBindingChanged(value);
    }

    /**
     * Sets the binding that will be used to get the value of the {@link com.telerik.widget.chart.engine.dataPoints.RangeDataPoint#getLow()}
     * member of the contained data points.
     *
     * @param valueSelector The low value selector.
     */
    public <T, U> void setLowBinding(Function<T, U> valueSelector) {
        this.setLowBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    /**
     * Gets the binding that will be used to get the value of the {@link com.telerik.widget.chart.engine.dataPoints.RangeDataPoint#getHigh()}
     * member of the contained data points.
     *
     * @return The high binding.
     */
    public DataPointBinding getHighBinding() {
        return this.highBinding;
    }

    /**
     * Gets the binding that will be used to get the value of the {@link com.telerik.widget.chart.engine.dataPoints.RangeDataPoint#getHigh()}
     * member of the contained data points.
     *
     * @param value The high binding.
     */
    public void setHighBinding(DataPointBinding value) {
        if (this.highBinding == value) {
            return;
        }

        this.highBinding = value;
        this.onHighBindingChanged(value);
    }

    /**
     * Gets the binding that will be used to get the value of the {@link com.telerik.widget.chart.engine.dataPoints.RangeDataPoint#getHigh()}
     * member of the contained data points.
     *
     * @param valueSelector The high value selector.
     */
    public <T, U> void setHighBinding(Function<T, U> valueSelector) {
        this.setHighBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    @Override
    protected void initDataBinding() {
        if (this.rangeSource == null) {
            this.rangeSource = (RangeSeriesDataSourceBase) this.dataSource();
        }

        this.rangeSource.setHighBinding(this.highBinding);
        this.rangeSource.setLowBinding(this.lowBinding);

        super.initDataBinding();
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        this.rangeSource = new RangeSeriesDataSourceBase(this.model());
        return this.rangeSource;
    }

    private void onLowBindingChanged(DataPointBinding newValue) {
        this.rangeSource.setLowBinding(newValue);
    }

    private void onHighBindingChanged(DataPointBinding newValue) {
        this.rangeSource.setHighBinding(newValue);
    }
}
