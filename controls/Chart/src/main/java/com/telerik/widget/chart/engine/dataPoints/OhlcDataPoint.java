package com.telerik.widget.chart.engine.dataPoints;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo;
import com.telerik.widget.chart.engine.axes.continuous.DateTimeContinuousAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisOhlcPlotInfo;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.Ohlc;

/**
 * A High-Low-Open-Close data point.
 */
public class OhlcDataPoint extends CategoricalDataPointBase {

    private static final int HIGH_PROPERTY_KEY =
            PropertyKeys.register(OhlcDataPoint.class, "High", ChartAreaInvalidateFlags.ALL);

    private static final int LOW_PROPERTY_KEY =
            PropertyKeys.register(OhlcDataPoint.class, "Low", ChartAreaInvalidateFlags.ALL);

    private static final int OPEN_PROPERTY_KEY =
            PropertyKeys.register(OhlcDataPoint.class, "Open", ChartAreaInvalidateFlags.ALL);

    private static final int CLOSE_PROPERTY_KEY =
            PropertyKeys.register(OhlcDataPoint.class, "Close", ChartAreaInvalidateFlags.ALL);

    private double high;
    private double low;
    private double open;
    private double close;

    private NumericalAxisOhlcPlotInfo numericalPlot;

    /**
     * Initializes a new instance of the {@link OhlcDataPoint} class.
     */
    public OhlcDataPoint() {
        this.isEmpty = false;
    }

    /**
     * Gets a value indicating whether this instance is rising (Bullish).
     *
     * @return a value indicating whether this instance is rising.
     */
    public boolean isRising() {
        return this.open < this.close;
    }

    /**
     * Gets a value indicating whether this instance is falling (Bearish).
     *
     * @return a value indicating whether this instance is falling (Bearish).
     */
    public boolean isFalling() {
        return this.open > this.close;
    }

    /**
     * Gets the high associated with the point.
     *
     * @return the current high.
     */
    public double getHigh() {
        return this.high;
    }

    /**
     * Sets the high associated with the point.
     *
     * @param value the new high.
     */
    public void setHigh(double value) {
        this.setValue(HIGH_PROPERTY_KEY, value);
    }

    /**
     * Gets the low associated with the point.
     *
     * @return the current low.
     */
    public double getLow() {
        return this.low;
    }

    /**
     * Sets the low associated with the point.
     *
     * @param value the new low.
     */
    public void setLow(double value) {
        this.setValue(LOW_PROPERTY_KEY, value);
    }

    /**
     * Gets the open associated with the point.
     *
     * @return the current open.
     */
    public double getOpen() {
        return this.open;
    }

    /**
     * Sets the open associated with the point.
     *
     * @param value the new open.
     */
    public void setOpen(double value) {
        this.setValue(OPEN_PROPERTY_KEY, value);
    }

    /**
     * Gets the close associated with the point.
     *
     * @return the current close.
     */
    public double getClose() {
        return this.close;
    }

    /**
     * Sets the close associated with the point.
     *
     * @param value the new close.
     */
    public void setClose(double value) {
        this.setValue(CLOSE_PROPERTY_KEY, value);
    }

    public NumericalAxisOhlcPlotInfo getNumericalPlot() {
        return this.numericalPlot;
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)

        int changedKey = e.getKey();

        if (changedKey == HIGH_PROPERTY_KEY) {
            this.high = ((Number) e.newValue()).doubleValue();
        } else if (changedKey == LOW_PROPERTY_KEY) {
            this.low = ((Number) e.newValue()).doubleValue();
        } else if (changedKey == OPEN_PROPERTY_KEY) {
            this.open = ((Number) e.newValue()).doubleValue();
        } else if (changedKey == CLOSE_PROPERTY_KEY) {
            this.close = ((Number) e.newValue()).doubleValue();
        }

        super.onPropertyChanged(e);
    }

    @Override
    public Object getValueForAxis(AxisModel axis) {
        if (axis instanceof NumericalAxisModel) {
            return new Ohlc(this.high, this.low, this.open, this.close);
        }

        return this.getCategory();
    }

    @Override
    public Object[] getTooltipTokens() {
        String format = "High: %s\nLow: %s\nOpen: %s\nClose: %s";
        return new String[]{String.format(format, this.high, this.low, this.open, this.close)};
    }

    @Override
    public void setValueFromAxis(AxisModel axis, Object value) {
        // ChartSeries labels rely on isPositive to flip alignment, so isPositive is set to true by default
        this.isPositive = true;

        if (axis instanceof NumericalAxisModel) {
            this.numericalPlot = (NumericalAxisOhlcPlotInfo) value;
        } else if (axis instanceof CategoricalAxisModel ||
                axis instanceof DateTimeContinuousAxisModel) {
            this.categoricalPlot = (CategoricalAxisPlotInfo) value;
        }
    }
}
