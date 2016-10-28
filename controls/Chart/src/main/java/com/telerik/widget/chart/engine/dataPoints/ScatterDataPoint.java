package com.telerik.widget.chart.engine.dataPoints;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfo;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

/**
 * Represents a data point that may be visualized by a Scatter series in a CartesianChart.&nbsp;
 * Generally this is a point that provides values for both x and y axes and may be plotted by
 * two numerical axes - Linear or Logarithmic.
 */
public class ScatterDataPoint extends DataPoint {

    private static final int X_VALUE_PROPERTY_KEY = PropertyKeys.register(ScatterDataPoint.class, "XValue", ChartAreaInvalidateFlags.ALL);
    private static final int Y_VALUE_PROPERTY_KEY = PropertyKeys.register(ScatterDataPoint.class, "YValue", ChartAreaInvalidateFlags.ALL);

    private NumericalAxisPlotInfo xPlot;
    private NumericalAxisPlotInfo yPlot;

    private static final String DEFAULT_LABEL_FORMAT_STRING = "(%s,%s)";

    private double xValue;
    private double yValue;

    /**
     * Gets the value that is provided for the x-axis of the cartesian widget.
     *
     * @return the current x value.
     */
    public double getXValue() {
        return this.xValue;
    }

    /**
     * Sets the value that is provided for the x-axis of the cartesian widget.
     *
     * @param value the new x value.
     */
    public void setXValue(double value) {
        this.setValue(X_VALUE_PROPERTY_KEY, value);
    }

    /**
     * Gets the value that is provided for the y-axis of the cartesian widget.
     *
     * @return the current y value.
     */
    public double getYValue() {
        return this.yValue;
    }

    /**
     * Sets the value that is provided for the y-axis of the cartesian widget.
     *
     * @param value the new y value.
     */
    public void setYValue(double value) {
        this.setValue(Y_VALUE_PROPERTY_KEY, value);
    }

    /**
     * Gets the current numerical x plot instance.
     *
     * @return the current x plot.
     */
    public NumericalAxisPlotInfo getXPlot() {
        return xPlot;
    }

    /**
     * Sets the current numerical x plot instance.
     *
     * @param xPlot the new x plot.
     */
    public void setxPlot(NumericalAxisPlotInfo xPlot) {
        this.xPlot = xPlot;
    }

    /**
     * Gets the current numerical y plot instance.
     *
     * @return the current y plot.
     */
    public NumericalAxisPlotInfo getYPlot() {
        return yPlot;
    }

    /**
     * Sets the current numerical y plot instance.
     *
     * @param yPlot the new y plot.
     */
    public void setyPlot(NumericalAxisPlotInfo yPlot) {
        this.yPlot = yPlot;
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local values first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == X_VALUE_PROPERTY_KEY) {
            this.xValue = ((Number) e.newValue()).doubleValue();
        } else if (e.getKey() == Y_VALUE_PROPERTY_KEY) {
            this.yValue = ((Number) e.newValue()).doubleValue();
            this.isEmpty = checkIsEmpty(this.yValue);
        }

        super.onPropertyChanged(e);
    }

    @Override
    public Object getValueForAxis(AxisModel axis) {
        if (axis.getType() == AxisType.FIRST) {
            return this.xValue;
        }

        return this.yValue;
    }

    @Override
    public void setValueFromAxis(AxisModel axis, Object value) {
        NumericalAxisPlotInfo plot = (NumericalAxisPlotInfo) value;
        if (axis.getType() == AxisType.FIRST) {
            this.setxPlot(plot);
        } else {
            this.setyPlot(plot);
        }
    }

    @Override
    public Object[] getTooltipTokens() {
        if (this.getXPlot() == null || this.getYPlot() == null) {
            return null;
        }

        return new Object[]{this.xValue, this.yValue};
    }

    @Override
    Object getDefaultLabel() {
        if (this.getXPlot() == null || this.getYPlot() == null) {
            return null;
        }

        return String.format(DEFAULT_LABEL_FORMAT_STRING, this.xValue, this.yValue);
    }
}

