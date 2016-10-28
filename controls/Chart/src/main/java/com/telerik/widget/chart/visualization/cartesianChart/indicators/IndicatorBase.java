package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.CategoricalSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.series.CategoricalSeriesModel;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.PointSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView;
import com.telerik.widget.chart.visualization.cartesianChart.series.CartesianSeries;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.CategoricalSeriesLabelRenderer;
import com.telerik.widget.chart.visualization.common.Axis;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;

public abstract class IndicatorBase extends CartesianSeries {

    public static final int FINANCIAL_INDICATOR_Z_INDEX = 500;
    private boolean bindingPassed;

    /**
     * Model holding the information about the current indicator instance.
     */
    protected CategoricalSeriesModel model;

    /**
     * Data point binding for the current indicator instance.
     */
    protected DataPointBinding categoryBinding;

    /**
     * Creates a new instance of the {@link IndicatorBase} class.
     */
    public IndicatorBase() {
    }

    @Override
    public ChartSeriesModel model() {
        if (this.model == null)
            this.model = createIndicatorModel();

        return this.model;
    }

    protected CategoricalSeriesModel createIndicatorModel() {
        return new PointSeriesModel();
    }

    /**
     * Gets the collection of data points associated with the indicator.
     */
    public DataPointCollection<CategoricalDataPoint> getDataPoints() {
        return this.model().dataPoints(); // TODO: Might need to get visible points only?
    }

    /**
     * Gets the binding that will be used to fill the category of the contained data points.
     */
    public DataPointBinding getCategoryBinding() {
        return this.categoryBinding;
    }

    /**
     * Sets the binding that will be used to fill the category of the contained data points.
     *
     * @param value The new category binding.
     */
    public void setCategoryBinding(DataPointBinding value) {
        if (value == null)
            throw new NullPointerException("value");

        if (this.categoryBinding == value) {
            return;
        }

        this.categoryBinding = value;
        this.onCategoryBindingChanged(value);
    }

    @Override
    protected int getDefaultZIndex() {
        return FINANCIAL_INDICATOR_Z_INDEX + this.model.index();
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
    protected ChartSeriesModel createModel() {
        this.model = new PointSeriesModel();
        return this.model;
    }

    /**
     * Occurs when one of the axes of the owning {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}
     * has been changed.
     *
     * @param oldAxis the old axis.
     * @param newAxis the new axis.
     */
    protected void onChartAxisChanged(Axis oldAxis, Axis newAxis) {
        if (oldAxis != null) {
            this.model.detachAxis(oldAxis.getModel());
        }
        if (newAxis != null) {
            this.model.attachAxis(newAxis.getModel(), newAxis.getAxisType());
        }
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        RadCartesianChartView chart = (RadCartesianChartView) this.getChart();
        Axis horizontalAxis = chart.getHorizontalAxis();
        Axis verticalAxis = chart.getVerticalAxis();

        if (horizontalAxis != null) {
            this.model.attachAxis(horizontalAxis.getModel(), AxisType.FIRST);
        }
        if (verticalAxis != null) {
            this.model.attachAxis(verticalAxis.getModel(), AxisType.SECOND);
        }

        if (!this.bindingPassed)
            initDataBinding();

        applyPaletteCore(this.getChart().getPalette());
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        AxisModel firstAxis = this.model.getFirstAxis();
        AxisModel secondAxis = this.model.getSecondAxis();

        if (firstAxis != null) {
            this.model.detachAxis(firstAxis);
        }
        if (secondAxis != null) {
            this.model.detachAxis(secondAxis);
        }
    }

    @Override
    protected void initDataBinding() {
        if (this.model().getPresenter() == null)
            return;

        super.initDataBinding();
        this.bindingPassed = true;
    }

    private void onCategoryBindingChanged(DataPointBinding newBinding) {
        ((CategoricalSeriesDataSource) this.dataSource()).setCategoryBinding(newBinding);
    }
}
