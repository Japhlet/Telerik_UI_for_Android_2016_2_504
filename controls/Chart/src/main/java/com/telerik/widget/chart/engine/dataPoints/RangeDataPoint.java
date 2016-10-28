package com.telerik.widget.chart.engine.dataPoints;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo;
import com.telerik.widget.chart.engine.axes.continuous.DateTimeContinuousAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisRangePlotInfo;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.series.rangeSeries.Range;

/**
 * A High-Low data point.
 */
public class RangeDataPoint extends CategoricalDataPointBase {

    private static final int HIGH_PROPERTY_KEY = PropertyKeys.register(RangeDataPoint.class, "High", ChartAreaInvalidateFlags.ALL);
    private static final int LOW_PROPERTY_KEY = PropertyKeys.register(RangeDataPoint.class, "Low", ChartAreaInvalidateFlags.ALL);

    private double high;
    private double low;

    /**
     * Creates a new instance of the {@link RangeDataPoint} class.
     */
    public RangeDataPoint() {
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
     * Gets the current numerical range plot info.
     *
     * @return the current numerical range plot info.
     */
    public NumericalAxisRangePlotInfo numericalPlot() {
        return (NumericalAxisRangePlotInfo) this.numericalPlot;
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        if (e.getKey() == HIGH_PROPERTY_KEY) {
            this.high = ((Number) e.newValue()).doubleValue();
            this.isEmpty = false;
        } else if (e.getKey() == LOW_PROPERTY_KEY) {
            this.low = ((Number) e.newValue()).doubleValue();
            this.isEmpty = false;
        }

        super.onPropertyChanged(e);
    }

    @Override
    public Object getValueForAxis(AxisModel axis) {
        if (axis instanceof NumericalAxisModel) {
            return new Range(this.low, this.high);
        }

        return this.getCategory();
    }

    @Override
    public Object[] getTooltipTokens() {
        return new Object[]{this.low, this.high};
    }

    @Override
    public void setValueFromAxis(AxisModel axis, Object value) {
        // ChartSeries labels rely on isPositive to flip alignment, so isPositive is set to true by default
        this.isPositive = true;

        if (axis instanceof NumericalAxisModel) {
            this.numericalPlot = (NumericalAxisRangePlotInfo) value;
        } else if (axis instanceof CategoricalAxisModel ||
                axis instanceof DateTimeContinuousAxisModel) {
            this.categoricalPlot = (CategoricalAxisPlotInfo) value;
        }
    }
}

