package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import com.telerik.android.common.Function;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.GenericDataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.CategoricalSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;

/**
 * Base class for all {@link com.telerik.widget.chart.visualization.common.ChartSeries} that may contain {@link CategoricalDataPoint}.
 */
public abstract class CategoricalSeries extends CategoricalSeriesBase {
    /**
     * Creates a new instance of the {@link CategoricalSeries} class.
     */
    protected CategoricalSeries() {
    }

    public CategoricalSeries(DataPointBinding valueBinding, DataPointBinding categoryBinding, Iterable data) {
        super(categoryBinding, data);

        setValueBinding(valueBinding);
    }

    /**
     * Gets the binding that will be used to fill the {@link com.telerik.widget.chart.engine.dataPoints.SingleValueDataPoint#value} member of the contained data points.
     *
     * @return The value binding.
     * @see com.telerik.widget.chart.engine.dataPoints.SingleValueDataPoint#getValue()
     */
    public DataPointBinding getValueBinding() {
        return ((CategoricalSeriesDataSource) this.dataSource()).getValueBinding();
    }

    /**
     * Sets the binding that will be used to fill the {@link com.telerik.widget.chart.engine.dataPoints.SingleValueDataPoint#value} member of the contained data points.
     *
     * @param value The value binding.
     * @see com.telerik.widget.chart.engine.dataPoints.SingleValueDataPoint#getValue()
     */
    public void setValueBinding(DataPointBinding value) {
        ((CategoricalSeriesDataSource) this.dataSource()).setValueBinding(value);
    }

    public <T, U> void setValueBinding(Function<T, U> valueSelector) {
        this.setValueBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new CategoricalSeriesDataSource(this.model());
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new CategoricalSeriesLabelRenderer(this);
    }

    @Override
    protected void initDataBinding() {
        CategoricalSeriesDataSource categoricalSource = (CategoricalSeriesDataSource) this.dataSource();
        categoricalSource.setValueBinding(this.getValueBinding());

        super.initDataBinding();
    }
}
