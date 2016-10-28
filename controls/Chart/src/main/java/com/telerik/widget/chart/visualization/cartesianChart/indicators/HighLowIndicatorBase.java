package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.financial.HighLowIndicatorDataSourceBase;

public abstract class HighLowIndicatorBase extends LineIndicatorBase {

    private DataPointBinding highBinding;
    private DataPointBinding lowBinding;

    /**
     * Creates a new instance of the {@link HighLowIndicatorBase} class.
     */
    public HighLowIndicatorBase() {
    }

    /**
     * Gets the binding that will be used to fill the High value for the indicator calculations.
     */
    public DataPointBinding getHighBinding() {
        return this.highBinding;
    }

    /**
     * Sets the binding that will be used to fill the High value for the indicator calculations.
     *
     * @param value The new high binding.
     */
    public void setHighBinding(DataPointBinding value) {
        if (value == null)
            throw new NullPointerException("value");

        if (this.highBinding == value)
            return;

        this.highBinding = value;
        onHighBindingChanged(value);
    }

    /**
     * Gets the binding that will be used to fill the Low value for the indicator calculations.
     */
    public DataPointBinding getLowBinding() {
        return this.lowBinding;
    }

    /**
     * Gets the binding that will be used to fill the Low value for the indicator calculations.
     *
     * @param value The new low binding.
     */
    public void setLowBinding(DataPointBinding value) {
        if (value == null)
            throw new NullPointerException("value");

        if (this.lowBinding == value)
            return;

        this.lowBinding = value;
        onLowBindingChanged(value);
    }

    @Override
    protected void initDataBinding() {
        HighLowIndicatorDataSourceBase source = (HighLowIndicatorDataSourceBase) this.dataSource();
        source.setHighBinding(this.highBinding);
        source.setLowBinding(this.lowBinding);
        source.setCategoryBinding(this.getCategoryBinding());

        super.initDataBinding();
    }

    private void onHighBindingChanged(DataPointBinding newBinding) {
        ((HighLowIndicatorDataSourceBase) this.dataSource()).setHighBinding(newBinding);
    }

    private void onLowBindingChanged(DataPointBinding newBinding) {
        ((HighLowIndicatorDataSourceBase) this.dataSource()).setLowBinding(newBinding);
    }
}
