package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.financial.HighLowCloseIndicatorDataSourceBase;

public abstract class HighLowCloseIndicatorBase extends HighLowIndicatorBase {

    private DataPointBinding closeBinding;

    /**
     * Creates an instance of the {@link HighLowCloseIndicatorBase} class.
     */
    public HighLowCloseIndicatorBase() {
    }

    /**
     * Gets the binding that will be used to fill the High value for the indicator calculations.
     */
    public DataPointBinding getCloseBinding() {
        return this.closeBinding;
    }

    /**
     * Sets the binding that will be used to fill the High value for the indicator calculations.
     *
     * @param value The new close binding.
     */
    public void setCloseBinding(DataPointBinding value) {
        if (this.closeBinding == value) {
            return;
        }

        this.closeBinding = value;
        this.onCloseBindingChanged(value);
    }

    @Override
    protected void initDataBinding() {
        HighLowCloseIndicatorDataSourceBase source = (HighLowCloseIndicatorDataSourceBase) this.dataSource();
        source.setCloseBinding(this.closeBinding);

        super.initDataBinding();
    }

    private void onCloseBindingChanged(DataPointBinding newValue) {
        ((HighLowCloseIndicatorDataSourceBase) this.dataSource()).setCloseBinding(newValue);
    }
}
