package com.telerik.widget.chart.engine.axes.common.layout;

import com.telerik.android.common.RadThickness;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisLastLabelVisibility;
import com.telerik.widget.chart.engine.axes.continuous.ValueRange;

/**
 * Encapsulates the layout algorithm for the {@link AxisModel}.
 */
public abstract class AxisModelLayoutStrategy {

    protected AxisModel owner;
    protected double maxLabelHeight;
    protected double maxLabelWidth;

    /**
     * This is a ratio that determines whether the labels on the horizontal axis are overlapping.
     * Ratios greater than one means that the labels are overlapping and that a non-overlapping layout
     * strategy needs to be chosen. The strategy is determined based on te value of the LabelLayoutMode property.
     */
    protected int totalLabelWidthToAvailableWidth = 0;

    /**
     * Creates a new instance of the {@link AxisModelLayoutStrategy} class.
     *
     * @param owner The owner of the layout strategy.
     */
    public AxisModelLayoutStrategy(AxisModel owner) {
        if (owner == null) {
            throw new IllegalArgumentException("owner cannot be null.");
        }

        this.owner = owner;
    }

    /**
     * Gets the owner.
     *
     * @return The layout strategy owner.
     */
    public AxisModel owner() {
        return this.owner;
    }

    /**
     * Gets the default {@link com.telerik.widget.chart.engine.axes.common.AxisLastLabelVisibility}.
     *
     * @return The default last label visibility.
     */
    public abstract AxisLastLabelVisibility getDefaultLastLabelVisibility();

    /**
     * Gets the zoom factor.
     *
     * @return The zoom factor.
     */
    public abstract double getZoom();

    /**
     * Applies layout rounding.
     */
    public abstract void applyLayoutRounding();

    /**
     * Updates the ticks visibility taking into account the provided clip rectangle.
     *
     * @param clipRect The clip rectangle.
     */
    public abstract void updateTicksVisibility(final RadRect clipRect);

    /**
     * Arranges the the axis components in the specified layout slot.
     *
     * @param rect The axis layout slot.
     */
    public abstract void arrange(final RadRect rect);

    /**
     * Gets the currently visible axis range within the [0, 1] order.
     *
     * @param availableSize The available axis size.
     * @return The visible axis value range.
     */
    public abstract ValueRange<Double> getVisibleRange(final RadSize availableSize);

    /**
     * Gets the axis desired size based on the available size.
     *
     * @param availableSize The available axis size.
     * @return The axis desired size.
     */
    public abstract RadSize getDesiredSize(final RadSize availableSize);

    /**
     * Gets the desired margin base on the provided available size.
     *
     * @param availableSize The available axis size.
     * @return The desired margin.
     */
    public abstract RadThickness getDesiredMargin(final RadSize availableSize);

    /**
     * Arranges the axis labels on multiple lines.
     *
     * @param label The label to arrange.
     * @param rect  The axis layout slot.
     */
    public abstract void arrangeLabelMultiline(AxisLabelModel label, final RadRect rect);

    /**
     * Arranges the axis labels as if there is room for each one even if they are too big.
     *
     * @param label The label to arrange.
     * @param rect  The axis layout slot.
     */
    public abstract void arrangeLabelNone(AxisLabelModel label, final RadRect rect);
}

