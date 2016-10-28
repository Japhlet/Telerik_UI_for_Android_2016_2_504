package com.telerik.widget.chart.visualization.cartesianChart.axes;

import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.ValueRange;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.common.CartesianAxis;

import java.util.List;

/**
 * Base class for all axes that use numbers to plot associated points.
 */
public abstract class NumericalAxis extends CartesianAxis {

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.cartesianChart.axes.NumericalAxis}
     * with a specified activity context, a set of styleable attributes and a default style id.
     */
    protected NumericalAxis() {
    }

    /**
     * Gets  the user-defined minimum of the axis.
     *
     * @return the minimum.
     */
    public double getMinimum() {
        return ((NumericalAxisModel) this.getModel()).getMinimum();
    }

    /**
     * Sets the user-defined minimum of the axis.
     * By default the axis itself will calculate the minimum depending on the minimum of the plotted data points.
     *
     * @param value the preferred minimum.
     */
    public void setMinimum(double value) {
        ((NumericalAxisModel) this.getModel()).setMinimum(value);
    }

    /**
     * Gets the user-defined maximum of the axis.
     *
     * @return the minimum.
     */
    public double getMaximum() {
        return ((NumericalAxisModel) this.getModel()).getMaximum();
    }

    /**
     * Sets the user-defined maximum of the axis.
     * By default the axis itself will calculate the maximum depending on the maximum of the plotted data points.
     *
     * @param value the preferred maximum.
     */
    public void setMaximum(double value) {
        ((NumericalAxisModel) this.getModel()).setMaximum(value);
    }

    /**
     * Gets a value from the {@link com.telerik.widget.chart.engine.axes.continuous.NumericalAxisRangeExtendDirection} enum
     * that specifies how the auto-range of this axis will be extended so that each data point is
     * visualized in the best possible way.
     *
     * @return the {@link com.telerik.widget.chart.engine.axes.continuous.NumericalAxisRangeExtendDirection} value.
     */
    public int getRangeExtendDirection() {
        return ((NumericalAxisModel) this.getModel()).getRangeExtendDirection();
    }

    /**
     * Sets a value from the {@link com.telerik.widget.chart.engine.axes.continuous.NumericalAxisRangeExtendDirection} enum
     * that specifies how the auto-range of this axis will be extended so that each data point is
     * visualized in the best possible way.
     *
     * @param value the {@link com.telerik.widget.chart.engine.axes.continuous.NumericalAxisRangeExtendDirection} value.
     */
    public void setRangeExtendDirection(int value) {
        ((NumericalAxisModel) this.getModel()).setRangeExtendDirection(value);
    }

    /**
     * Gets the user-defined number of ticks presented on the axis.
     *
     * @return the user-defined number of ticks.
     */
    public int getDesiredTickCount() {
        return ((NumericalAxisModel) this.getModel()).getDesiredTickCount();
    }

    /**
     * Gets the user-defined number of ticks presented on the axis.
     *
     * @param value the user-defined number of ticks.
     */
    public void setDesiredTickCount(int value) {
        ((NumericalAxisModel) this.getModel()).setDesiredTickCount(value);
    }

    /**
     * Gets an instance of the {@link com.telerik.widget.chart.engine.axes.continuous.ValueRange} class
     * representing the value range currently displayed on this axis.
     *
     * @return the value range.
     */
    public ValueRange<Double> getActualRange() {
        return ((NumericalAxisModel) this.getModel()).getActualRange();
    }

    @Override
    public List<DataPoint> getDataPointsForValue(Object value) {
        return null;
    }
}
