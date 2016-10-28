package com.telerik.widget.chart.visualization.annotations;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;

/**
 * This class contains layout information related to a single {@link com.telerik.widget.chart.visualization.annotations.ChartAnnotation}.
 */
public class ChartAnnotationLabelUpdateContext {

    /**
     * An instance of the {@link RadPoint} class representing the location of the annotation.
     */
    public RadPoint location;

    /**
     * An instance of the {@link RadRect} class representing the bounding rectangle of the annotation.
     */
    public RadRect layoutSlot;

    /**
     * Creates an instance of the {@link ChartAnnotationLabelUpdateContext}
     * class with the specified location.
     *
     * @param location a {@link RadPoint} instance representing the location of the annotation.
     */
    public ChartAnnotationLabelUpdateContext(RadPoint location) {
        this.layoutSlot = new RadRect(location, location);
        this.location = location;
    }

    /**
     * Creates an instance of the {@link ChartAnnotationLabelUpdateContext}
     * class with the specified bounds.
     *
     * @param layoutSlot a {@link RadRect} instance representing the layout slot for the annotation.
     */
    public ChartAnnotationLabelUpdateContext(RadRect layoutSlot) {
        this.layoutSlot = layoutSlot;
        this.location = layoutSlot.getLocation();
    }
}
