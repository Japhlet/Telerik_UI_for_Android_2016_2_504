package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import com.telerik.android.common.Function;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.GenericDataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.CategoricalSeriesDataSourceBase;
import com.telerik.widget.chart.engine.series.CategoricalSeriesModel;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineMode;
import com.telerik.widget.chart.visualization.cartesianChart.series.CartesianSeries;

public abstract class CategoricalSeriesBase extends CartesianSeries {
    private CategoricalSeriesDataSourceBase categoricalSource;

    public CategoricalSeriesBase() {
        this(null, null);
    }

    public CategoricalSeriesBase(DataPointBinding categoryBinding, Iterable data) {
        setCategoryBinding(categoryBinding);
        setData(data);
    }

    /**
     * Gets or sets the combination mode to be used when data points are plotted.
     *
     * @return The combination mode to be used when data points are plotted.
     */
    public ChartSeriesCombineMode getCombineMode() {
        return ((CategoricalSeriesModel) this.model()).getCombineMode();
    }

    /**
     * Sets or sets the {@link ChartSeriesCombineMode} to be used when data points are plotted.
     *
     * @param value The new {@link ChartSeriesCombineMode}.
     */
    public void setCombineMode(ChartSeriesCombineMode value) {
        ((CategoricalSeriesModel) this.model()).setCombineMode(value);
    }

    /**
     * Gets the key that defines in which stack group this series will be included if its {@link #getCombineMode()} equals STACK or STACK_100.
     *
     * @return The key that defines in which stack group this series will be included if its {@link #getCombineMode()} equals STACK or STACK_100.
     */
    public Object getStackGroupKey() {
        return ((CategoricalSeriesModel) this.model()).getStackGroupKey();
    }

    /**
     * Sets the key that defines in which stack group this series will be included if its {@link #getCombineMode()} equals STACK or STACK_100.
     *
     * @param value The key that defines in which stack group this series will be included if its {@link #getCombineMode()} equals STACK or STACK_100.
     */
    public void setStackGroupKey(Object value) {
        ((CategoricalSeriesModel) this.model()).setStackGroupKey(value);
    }

    /**
     * Gets the binding that will be used to fill the {@link com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase#getCategory()} member of the contained data points.
     *
     * @return The category binding.
     */
    public DataPointBinding getCategoryBinding() {
        return ((CategoricalSeriesDataSourceBase) this.dataSource()).getCategoryBinding();
    }

    /**
     * Sets the binding that will be used to fill the {@link com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase#getCategory()} member of the contained data points.
     *
     * @param value The category binding.
     */
    public void setCategoryBinding(DataPointBinding value) {
        ((CategoricalSeriesDataSourceBase) this.dataSource()).setCategoryBinding(value);
    }

    public <T, U> void setCategoryBinding(Function<T, U> valueSelector) {
        this.setCategoryBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    @Override
    protected void initDataBinding() {
        if (this.categoricalSource == null) {
            this.categoricalSource = (CategoricalSeriesDataSourceBase) this.dataSource();
        }

        this.categoricalSource.setCategoryBinding(this.getCategoryBinding());

        super.initDataBinding();
    }
}
