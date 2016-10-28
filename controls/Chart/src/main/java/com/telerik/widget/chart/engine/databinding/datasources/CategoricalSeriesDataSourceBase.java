package com.telerik.widget.chart.engine.databinding.datasources;

import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

/**
 * Represents a {@link ChartSeriesDataSource} used as base class for storing data sources of
 * the categorical charts.
 */
public abstract class CategoricalSeriesDataSourceBase extends ChartSeriesDataSource {

    private DataPointBinding categoryBinding;

    /**
     * Initializes a new instance of the {@link CategoricalSeriesDataSourceBase} class.
     *
     * @param owner the chart series this data source belongs to.
     */
    public CategoricalSeriesDataSourceBase(ChartSeriesModel owner) {
        super(owner);
    }

    /**
     * Gets the current category binding.
     *
     * @return the current category binding.
     */
    public DataPointBinding getCategoryBinding() {
        return this.categoryBinding;
    }

    /**
     * Sets the current category binding.
     *
     * @param value the new category binding.
     */
    public void setCategoryBinding(DataPointBinding value) {
        if (this.categoryBinding == value) {
            return;
        }

        this.categoryBinding = value;

        if (this.getItemsSource() != null) {
            this.rebind(false, null);
        }
    }

    @Override
    protected void initializeBinding(DataPointBindingEntry binding) {
        if (this.categoryBinding != null)
            ((CategoricalDataPointBase) binding.getDataPoint()).setCategory(this.categoryBinding.getValue(binding.getDataItem()));
    }

    @Override
    protected void updateBinding(DataPointBindingEntry binding) {
        this.initializeBinding(binding);
    }
}
