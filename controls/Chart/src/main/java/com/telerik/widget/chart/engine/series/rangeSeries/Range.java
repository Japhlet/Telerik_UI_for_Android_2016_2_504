package com.telerik.widget.chart.engine.series.rangeSeries;

/**
 * Represents a class, which defines a set of two values - High, Low.
 */
public class Range {

    private double low;
    private double high;

    /**
     * Creates a new instance of the {@link Range} class.
     */
    public Range() {
    }

    /**
     * Creates a new instance of the {@link Range} class with the specified values.
     *
     * @param low  The low value.
     * @param high The high value.
     */
    public Range(double low, double high) {
        this.low = low;
        this.high = high;
    }

    /**
     * Gets the low value.
     */
    public double low() {
        return this.low;
    }

    /**
     * Gets the high value.
     */
    public double high() {
        return this.high;
    }
}

