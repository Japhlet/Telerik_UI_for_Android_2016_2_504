package com.telerik.widget.chart.visualization.annotations.cartesian;

import com.telerik.widget.chart.engine.decorations.annotations.SingleAxisAnnotationModel;
import com.telerik.widget.chart.visualization.annotations.ChartAnnotation;
import com.telerik.widget.chart.visualization.annotations.ChartLabelAnnotation;
import com.telerik.widget.chart.visualization.common.CartesianAxis;

/**
 * This class is a base class for all {@link ChartAnnotation} instances that are part of a
 * {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}.
 */
public abstract class CartesianChartAnnotation extends ChartLabelAnnotation {
    private CartesianAxis axis;

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.annotations.cartesian.CartesianChartAnnotation}
     * class with specified application context, styleable attributes and default style.
     */
    public CartesianChartAnnotation(CartesianAxis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("axis cannot be null");
        }
    }

    /**
     * Gets the current axis.
     *
     * @return the current axis.
     * @see CartesianAxis
     */
    public CartesianAxis getAxis() {
        return this.axis;
    }

    /**
     * Sets the current axis.
     *
     * @param value the new axis.
     * @see CartesianAxis
     */
    public void setAxis(CartesianAxis value) {
        if (value == null)
            throw new IllegalArgumentException("the value cannot be null");

        this.axis = value;

        SingleAxisAnnotationModel model = (SingleAxisAnnotationModel) this.getModel();
        model.setAxis(value.getModel());

        this.requestLayout();
    }
}
