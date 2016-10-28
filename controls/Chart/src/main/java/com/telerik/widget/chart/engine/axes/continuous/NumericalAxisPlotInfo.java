package com.telerik.widget.chart.engine.axes.continuous;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;

/**
 * Plot info for the numeric axes.
 */
public class NumericalAxisPlotInfo extends NumericalAxisPlotInfoBase {

    public double normalizedValue;

    private NumericalAxisPlotInfo() {
    }

    /**
     * Creates a numeric plot info with the provided arguments.
     *
     * @param axis       The axis of the plot information.
     * @param plotOffset The plot offset.
     * @param value      The plot value.
     * @param origin     The plot origin.
     * @return A new instance of the {@link NumericalAxisPlotInfo} class.
     */
    public static NumericalAxisPlotInfo create(AxisModel axis, double plotOffset, double value, double origin) {
        NumericalAxisPlotInfo info = new NumericalAxisPlotInfo();
        info.axis = axis;
        info.plotOriginOffset = plotOffset;
        info.normalizedValue = value;
        info.normalizedOrigin = origin;

        return info;
    }

    @Override
    public double centerX(final RadRect relativeRect) {
        return relativeRect.getX() + (this.normalizedValue * relativeRect.getWidth());
    }

    @Override
    public double centerY(final RadRect relativeRect) {
        return relativeRect.getY() + (relativeRect.getHeight() * (1 - this.normalizedValue));
    }

    /**
     * Converts the normalized value to an angle in degrees.
     *
     * @return An angle in degrees.
     */
    public double convertToAngle() {
        return this.normalizedValue * 360;
    }
}
