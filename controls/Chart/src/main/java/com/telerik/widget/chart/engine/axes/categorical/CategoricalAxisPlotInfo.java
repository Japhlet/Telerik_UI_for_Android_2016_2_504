package com.telerik.widget.chart.engine.axes.categorical;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotInfo;

/**
 * Contains plot information for the {@link com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel}.
 */
public class CategoricalAxisPlotInfo extends AxisPlotInfo {

    public double position;
    public double length;
    public double rangeLength; // total length of the axis range, containing the slot
    public double rangePosition; // position of the axis range, containing the slot
    public Object categoryKey;

    private CategoricalAxisPlotInfo() {
    }

    /**
     * Creates a new instance of the {@link com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo} class with the provided arguments.
     *
     * @param axis  The axis for this plot info.
     * @param value The value with which this plot info will be associated.
     * @param step  The axis value range that this plot info is associated with.
     * @return A new {@link com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo} instance.
     */
    public static CategoricalAxisPlotInfo create(AxisModel axis, double value, double step) {
        CategoricalAxisPlotInfo info = new CategoricalAxisPlotInfo();
        info.axis = axis;
        info.rangeLength = step;
        info.rangePosition = value;

        return info;
    }

    @Override
    public double centerX(final RadRect relativeRect) {
        return relativeRect.getX() + ((this.position + (this.length / 2)) * relativeRect.getWidth());
    }

    @Override
    public double centerY(final RadRect relativeRect) {
        return relativeRect.getBottom() - ((this.position + (this.length / 2)) * relativeRect.getHeight());
    }

    /**
     * Converts the current range position to an angle.
     *
     * @param chartArea The chart that will be used to normalize the final angle.
     * @return The range position converted to an angle in degrees.
     */
//    public double convertToAngle(PolarChartAreaModel chartArea) {
//        double angle = this.rangePosition * 360;
//        angle = chartArea.normalizeAngle(angle);
//        return angle;
//    }
}
