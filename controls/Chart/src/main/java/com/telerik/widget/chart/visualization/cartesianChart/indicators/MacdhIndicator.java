/*
package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import android.content.Context;

import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.MacdhIndicatorDataSource;
import com.telerik.widget.chart.engine.series.BarSeriesModel;
import com.telerik.widget.chart.engine.series.CategoricalSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.CategoricalSeriesLabelRenderer;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;

public class MacdhIndicator extends BarIndicatorBase {

    */
/**
     * The signal period for the current indicator instance.
     *//*

    protected int signalPeriod;

    */
/**
     * The short period for the current indicator instance.
     *//*

    protected int shortPeriod;

    */
/**
     * The long period for the current indicator instance.
     *//*

    protected int longPeriod;

    */
/**
     * Creates a new instance of the {@link MacdhIndicator} class.
     *
     * @param context the current context.
     *//*

    public MacdhIndicator(Context context) {
        super(context);
    }

    */
/**
     * Gets the long period for the indicator.
     *
     * @return the current long period for the indicator.
     *//*

    public int getLongPeriod() {
        return this.longPeriod;
    }

    */
/**
     * Sets the long period for the indicator.
     *
     * @param longPeriod the new long period.
     *//*

    public void setLongPeriod(int longPeriod) {
        if (this.longPeriod == longPeriod)
            return;

        this.longPeriod = longPeriod;
        this.onLongPeriodChanged(longPeriod);
    }

    */
/**
     * Gets the short period for the indicator.
     *
     * @return the current short period for the indicator.
     *//*

    public int getShortPeriod() {
        return this.shortPeriod;
    }

    */
/**
     * Sets the short period for the indicator.
     *
     * @param shortPeriod the new short period.
     *//*

    public void setShortPeriod(int shortPeriod) {
        if (this.shortPeriod == shortPeriod)
            return;

        this.shortPeriod = shortPeriod;
        onShortPeriodChanged(shortPeriod);
    }

    */
/**
     * Gets the signal period for the indicator.
     *
     * @return the current signal period for the indicator.
     *//*

    public int getSignalPeriod() {
        return this.signalPeriod;
    }

    */
/**
     * Sets the signal period for the indicator.
     *
     * @param signalPeriod the new signal period.
     *//*

    public void setSignalPeriod(int signalPeriod) {
        if (this.signalPeriod == signalPeriod)
            return;

        this.signalPeriod = signalPeriod;
        onSignalPeriodChanged(signalPeriod);
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new CategoricalSeriesLabelRenderer(this);
    }

    @Override
    protected CategoricalSeriesModel createIndicatorModel() {
        return new BarSeriesModel();
    }

    @Override
    public String toString() {
        return String.format("Moving Average Convergence Divergence Histogram (%s, %s, %s)", this.longPeriod, this.shortPeriod, this.signalPeriod);
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new MacdhIndicatorDataSource(this.model());
    }

    protected void onPanOffsetChanged(ChartLayoutContext context) {
        this.updateUICore(context);
    }

    @Override
    protected void onAttached() {
        super.onAttached();
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        oldChart.getChartArea().getSeries().remove(this.model);
    }

    private void onLongPeriodChanged(int newValue) {
        ((MacdhIndicatorDataSource) this.dataSource()).setLongPeriod(newValue);
    }

    private void onShortPeriodChanged(int newValue) {
        ((MacdhIndicatorDataSource) this.dataSource()).setShortPeriod(newValue);
    }

    private void onSignalPeriodChanged(int newValue) {
        ((MacdhIndicatorDataSource) this.dataSource()).setSignalPeriod(newValue);
    }
}
*/
