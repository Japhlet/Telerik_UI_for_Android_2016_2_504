package com.telerik.widget.chart.engine.series;

/**
 * Represents a class, which defines a set of four values - High, Low, Open, Close.
 */
@SuppressWarnings("SpellCheckingInspection")
public class Ohlc {

    private double close;
    private double open;
    private double low;
    private double high;

    /**
     * Creates a new instance of the {@link Ohlc} class.
     */
    public Ohlc() {
    }

    /**
     * Creates a new instance of the {@link Ohlc} class.
     *
     * @param high  The high value.
     * @param low   The low value.
     * @param open  The open value.
     * @param close The close value.
     */
    public Ohlc(double high, double low, double open, double close) {
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
    }

    /**
     * Gets the high value.
     */
    public double high() {
        return this.high;
    }

    /**
     * Gets the low value.
     */
    public double low() {
        return this.low;
    }

    /**
     * Gets the open value.
     */
    public double open() {
        return this.open;
    }

    /**
     * Gets the close value.
     */
    public double close() {
        return this.close;
    }
}

