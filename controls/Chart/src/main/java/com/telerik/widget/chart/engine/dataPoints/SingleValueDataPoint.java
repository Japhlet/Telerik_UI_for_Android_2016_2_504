package com.telerik.widget.chart.engine.dataPoints;

import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.continuous.ContinuousAxisModel;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.events.RadPropertyEventArgs;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

/**
 * Defines a {@link DataPoint} that has a single value property, used by numerical axes.
 */
public abstract class SingleValueDataPoint extends DataPoint {

    private static final int VALUE_PROPERTY_KEY = PropertyKeys.register(SingleValueDataPoint.class, "Value", ChartAreaInvalidateFlags.ALL);

    private double value;

    /**
     * Creates a new instance of the {@link SingleValueDataPoint} class.
     */
    public SingleValueDataPoint() {
    }

    /**
     * Gets the core value associated with the point.
     *
     * @return the current value for this data point.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Sets the core value associated with the point.
     *
     * @param value the new value.
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
    public Object getValueForAxis(AxisModel axis) {
        if (axis instanceof ContinuousAxisModel) {
            return this.value;
        }

        return null;
    }

    @Override
    public Object[] getTooltipTokens() {
        return new Object[]{this.value};
    }

    @Override
    Object getDefaultLabel() {
        return this.value;
    }
}

