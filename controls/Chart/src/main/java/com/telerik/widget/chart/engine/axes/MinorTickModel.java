package com.telerik.widget.chart.engine.axes;

// TODO: MINOR ticks are not implemented at the moment.
class MinorTickModel extends AxisTickModel {

    public MinorTickModel(double value, double normalizedValue, int virtualIndex) {
        super(value, normalizedValue, virtualIndex);
    }

    @Override
    public TickType getType() {
        return TickType.MINOR;
    }
}

