package com.telerik.widget.chart.engine.axes.common;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;

/**
 * Used in RadChartView to provide multiple axes support.
 * Series that can provide their own axis implement this interface.
 */
public interface SeriesModelWithAxes {

    /**
     * Gets the first axis as {@link AxisModel}.
     *
     * @return the first axis.
     * @see AxisModel
     */
    AxisModel getFirstAxis();

    /**
     * Gets the second axis as {@link AxisModel}.
     *
     * @return the second axis.
     * @see AxisModel
     */
    AxisModel getSecondAxis();

    /**
     * Used to attach an axis of a given type.
     *
     * @param axis the axis to be attached.
     * @param type the type of the axis.
     * @see AxisModel
     * @see AxisType
     */
    void attachAxis(AxisModel axis, AxisType type);

    /**
     * Used to detach a selected axis.
     *
     * @param axis the axis to be detached.
     * @see AxisModel
     */
    void detachAxis(AxisModel axis);
}
