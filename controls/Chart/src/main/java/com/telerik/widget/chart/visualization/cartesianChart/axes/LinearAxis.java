package com.telerik.widget.chart.visualization.cartesianChart.axes;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.continuous.LinearAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisModel;


/**
 * This class represents a Cartesian Chart axis for linear numerical values.
 */
public class LinearAxis extends NumericalAxis {
    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.cartesianChart.axes.LinearAxis}.
     */
    public LinearAxis() {
    }

    /**
     * Gets the major value step between two ticks. By default the axis itself will calculate the
     * major step, depending on the plotted data points. You can reset this property by setting it
     * to 0 to restore the default behavior.
     *
     * @return the major value step between two ticks.
     */
    public double getMajorStep() {
        return ((NumericalAxisModel) this.getModel()).getMajorStep();
    }

    /**
     * Sets the major value step between two ticks. By default the axis itself will calculate the
     * major step, depending on the plotted data points.
     *
     * @param value the desired value step between two ticks.
     */
    public void setMajorStep(double value) {
        ((NumericalAxisModel) this.getModel()).setMajorStep(value);
    }

    @Override
    protected AxisModel createModel() {
        return new LinearAxisModel();
    }
}
