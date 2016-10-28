package com.telerik.widget.chart.engine.axes;

/**
 * Represents a major tick on an axis.
 * This class is used by the Chart engine and is not intended to be used directly in your application.
 */
public class MajorTickModel extends AxisTickModel {

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.engine.axes.MajorTickModel} class.
     *
     * @param value           the value to which the tick corresponds.
     * @param normalizedValue the value in normalized form.
     * @param virtualIndex    the virtual index of the tick.
     */
    public MajorTickModel(double value, double normalizedValue, int virtualIndex) {
        super(value, normalizedValue, virtualIndex);
    }

    @Override
    public TickType getType() {
        return TickType.MAJOR;
    }
}

