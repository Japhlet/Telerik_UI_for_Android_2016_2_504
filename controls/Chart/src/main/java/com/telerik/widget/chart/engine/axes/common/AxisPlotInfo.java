package com.telerik.widget.chart.engine.axes.common;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;

/**
 * Base class for all classes that deal with axis plots.
 */
public abstract class AxisPlotInfo {

    /**
     * Instance of the {@link AxisModel} class that is responsible for calculating the positions of
     * the chart elements.
     *
     * @see AxisModel
     */
    protected AxisModel axis;

    private int snapTickIndex = -1;

    /**
     * Used to calculate the center on the x axis based on the passed {@link RadRect} instance.
     *
     * @param relativeRect RadRect instance used for measuring the center on the x axis.
     * @return the calculated center on the x axis.
     */
    public double centerX(final RadRect relativeRect) {
        return 0;
    }

    /**
     * Used to calculate the center on the y axis based on the passed {@link RadRect} instance.
     *
     * @param relativeRect RadRect instance used for measuring the center on the y axis.
     * @return the calculated center on the y axis.
     */
    public double centerY(final RadRect relativeRect) {
        return 0;
    }

    /**
     * Gets the current instance of the {@link AxisModel}.
     *
     * @return the current instance of the axis model.
     */
    public AxisModel getAxis() {
        return axis;
    }

    /**
     * Gets the current snap tick index.
     *
     * @return the current snap tick index.
     */
    public int getSnapTickIndex() {
        return snapTickIndex;
    }

    /**
     * Sets the current snap tick index.
     *
     * @param snapTickIndex the new snap tick index.
     */
    public void setSnapTickIndex(int snapTickIndex) {
        this.snapTickIndex = snapTickIndex;
    }
}
