package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.CategoricalSeriesDataSource;

/**
 * Represents a base class for financial indicators whose value depends on one input value (Open, High, Low, Close).
 */
public abstract class ValueIndicatorBase extends LineIndicatorBase {

    private DataPointBinding valueBinding;

    /**
     * Creates a new instance of the {@link ValueIndicatorBase} class.
     */
    public ValueIndicatorBase() {
    }

    /**
     * Gets the binding that will be used to fill the value of the contained data points.
     */
    public DataPointBinding getValueBinding() {
        return this.valueBinding;
    }

    /**
     * Sets the binding that will be used to fill the value of the contained data points.
     *
     * @param value The new value binding.
     */
    public void setValueBinding(DataPointBinding value) {
        if (value == null)
            throw new NullPointerException("value");

        if (this.valueBinding == value)
            return;

        this.valueBinding = value;
        this.onValueBindingChanged(value);
    }

    @Override
    protected void initDataBinding() {
        CategoricalSeriesDataSource source = (CategoricalSeriesDataSource) this.dataSource();
        source.setValueBinding(this.valueBinding);

        super.initDataBinding();
    }

    private void onValueBindingChanged(DataPointBinding newValueBinding) {
        ((CategoricalSeriesDataSource) this.dataSource()).setValueBinding(newValueBinding);
    }
}
