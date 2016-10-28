package com.telerik.widget.chart.visualization.cartesianChart.axes;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.continuous.LogarithmicAxisModel;

/**
 * This class represents an axis for numerical values with logarithmic distribution.
 */
public class LogarithmicAxis extends NumericalAxis {

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.cartesianChart.axes.LogarithmicAxis}
     * with a specified activity context, a set of styleable attributes and a default style id.
     */
    public LogarithmicAxis() {
    }

    /**
     * Gets the super of the logarithm used for normalizing data points' values.
     *
     * @return the super of the logarithm.
     */
    public double getLogarithmBase() {
        return ((LogarithmicAxisModel) this.getModel()).getLogarithmBase();

    }

    /**
     * Sets the super of the logarithm used for normalizing data points' values.
     *
     * @param value the super of the logarithm.
     */
    public void setLogarithmBase(double value) {
        ((LogarithmicAxisModel) this.getModel()).setLogarithmBase(value);
    }

    /**
     * Gets the exponent step between each axis tick.
     * By default the axis itself will calculate the exponent step, depending on the plotted data points.
     *
     * @return the exponent step between each axis tick.
     */
    public double getExponentStep() {
        return ((LogarithmicAxisModel) this.getModel()).getMajorStep();
    }

    /**
     * Sets the exponent step between each axis tick.
     * By default the axis itself will calculate the exponent step, depending on the plotted data points.
     * You can reset this property by setting it to 0 to restore the default behavior.
     *
     * @param value the exponent step between each axis tick.
     */
    public void setExponentStep(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("ExponentStep cannot be less than 0.");
        }

        ((LogarithmicAxisModel) this.getModel()).setMajorStep(value);
    }

    @Override
    protected AxisModel createModel() {
        return new LogarithmicAxisModel();
    }
}
