package com.telerik.widget.chart.engine.dataPoints;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisModel;
import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo;
import com.telerik.widget.chart.engine.axes.continuous.DateTimeContinuousAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisModel;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfo;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

/**
 * Defines a {@link DataPoint} that has a single value property, used by numerical axes.
 */
public class CategoricalDataPoint extends CategoricalDataPointBase {

    public static final int VALUE_PROPERTY_KEY = PropertyKeys.register(CategoricalDataPoint.class, "Value", ChartAreaInvalidateFlags.ALL);

    private double value;

    /**
     * Initializes a new instance of the {@link CategoricalDataPoint} class.
     */
    public CategoricalDataPoint() {
    }

    /**
     * Gets the core value associated with the data point.
     *
     * @return the core value of the data point.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Sets the core value associated with the data point.
     *
     * @param value the new value for the data point.
     */
    public void setValue(double value) {
        this.setValue(VALUE_PROPERTY_KEY, value);
    }

    @Override
    protected void onPropertyChanged(RadPropertyEventArgs e) {
        // update local value first and then call super to raise the PropertyChanged event (if needed)
        if (e.getKey() == VALUE_PROPERTY_KEY) {
            this.value = ((Number) e.newValue()).doubleValue();
            this.isEmpty = checkIsEmpty(this.value);

            // check whether the default label is used
            if (this.label == null) {
                this.raisePropertyChanged(null, LABEL_PROPERTY_KEY);
            }
        }

        super.onPropertyChanged(e);
    }

    @Override
    public void setValueFromAxis(AxisModel axis, Object value) {
        if (axis instanceof NumericalAxisModel) {
            NumericalAxisPlotInfo plotInfo = (NumericalAxisPlotInfo) value;
            this.numericalPlot = plotInfo;
            this.isPositive = plotInfo.normalizedValue >= this.numericalPlot.normalizedOrigin;
        } else if (axis instanceof CategoricalAxisModel || axis instanceof DateTimeContinuousAxisModel) {
            this.categoricalPlot = (CategoricalAxisPlotInfo) value;
        }
    }

    @Override
    public Object getValueForAxis(AxisModel axis) {
        if (axis instanceof NumericalAxisModel) {
            return this.value;
        }

        return this.getCategory();
    }

    @Override
    public Object[] getTooltipTokens() {
        if (this.categoricalPlot == null || this.numericalPlot == null) {
            return null;
        }

        Object categoryKey = this.categoricalPlot.categoryKey;
        return new Object[]{this.getValue(), (String) categoryKey};
    }

    @Override
    Object getDefaultLabel() {
        return this.value;
    }

    @Override
    public double getCenterX() {
        if (this.isEmpty) {
            return this.categoricalPlot.centerX(this.parent.getLayoutSlot());
        }

        return super.getCenterX();
    }

    @Override
    public double getCenterY() {
        if (this.isEmpty) {
            return this.categoricalPlot.centerY(this.parent.getLayoutSlot());
        }

        return super.getCenterY();
    }
}

