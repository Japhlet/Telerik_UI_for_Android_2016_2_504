package com.telerik.widget.chart.engine.axes;

import com.telerik.widget.chart.engine.elementTree.ChartNode;

/**
 * This is a base class for all tick types available in a Chart. This class is abstract
 * and should not be used directly in your application.
 */
public abstract class AxisTickModel extends ChartNode {

    int virtualIndex; // the index of the tick on the axis that it would occupy if no zoom is applied
    protected double value;
    protected double normalizedValue;
    protected AxisTickModel next;
    protected AxisTickModel previous;
    protected AxisLabelModel associatedLabel;
    protected TickPosition position;

    public AxisTickModel(double value, double normalizedValue, int virtualIndex) {
        this.value = value;
        this.normalizedValue = normalizedValue;
        this.virtualIndex = virtualIndex;
    }

    /**
     * When overridden, gets a value from the {@link TickType}
     * enum determining the type of the axis.
     *
     * @return the axis type
     */
    public abstract TickType getType();

    public AxisLabelModel associatedLabel() {
        return this.associatedLabel;
    }

    /**
     * Gets the value associated with this {@link AxisTickModel}.
     *
     * @return the value
     */
    public double value() {
        return this.value;
    }

    /**
     * Gets a value from the {@link TickPosition} enum representing the position of the current tick.
     *
     * @return the tick position
     */
    public TickPosition position() {
        return this.position;
    }

    /**
     * Gets the normalized value associated with this {@link AxisTickModel}.
     *
     * @return the normalized value
     */
    public double normalizedValue() {
        return this.normalizedValue;
    }

    /**
     * Gets the virtual index of the current {@link AxisTickModel} instance.
     * The virtual index represents the index of the label as if no zoom is applied.
     *
     * @return the virtual index
     */
    public int virtualIndex() {
        return this.virtualIndex;
    }

    /**
     * Gets the next major tick on the axis on which this one resides.
     *
     * @return an instance of the {@link AxisTickModel} representing
     * the next major tick
     */
    public AxisTickModel getNextMajorTick() {
        AxisTickModel tick = this.next;
        while (tick != null) {
            if (tick.getType() == TickType.MAJOR) {
                return tick;
            }
            tick = tick.next;
        }

        return null;
    }

    /**
     * Calculates the normalized value between this and the next major tick.
     *
     * @return the normalized value
     */
    public double getNormalizedForwardLength() {
        if (this.next == null) {
            return 0.0;
        }

        return this.next.normalizedValue - this.normalizedValue;
    }

    /**
     * Calculates the normalized value between this and the previous major tick.
     *
     * @return a {@link java.math.BigDecimal} instance representing the normalized value
     */
    public double getNormalizedBackwardLength() {
        if (this.previous == null) {
            return 0.0;
        }

        return this.normalizedValue - this.previous.normalizedValue;
    }
}

