package com.telerik.widget.chart.engine.axes.continuous;

import com.telerik.widget.chart.engine.axes.AxisModel;

public class NumericalAxisOhlcPlotInfo extends NumericalAxisPlotInfoBase {
    public double normalizedHigh;
    public double normalizedLow;
    public double normalizedOpen;
    public double normalizedClose;

    public double physicalOpen = -1.0;
    public double physicalClose = -1.0;

    public int snapBaseTickIndex = -1;
    public int snapOpenTickIndex = -1;
    public int snapCloseTickIndex = -1;

    static NumericalAxisOhlcPlotInfo create(AxisModel axis, double plotOffset, double high, double low, double open, double close, double origin) {
        NumericalAxisOhlcPlotInfo info = new NumericalAxisOhlcPlotInfo();
        info.axis = axis;
        info.plotOriginOffset = plotOffset;
        info.normalizedHigh = high;
        info.normalizedLow = low;
        info.normalizedOpen = open;
        info.normalizedClose = close;
        info.normalizedOrigin = origin;

        return info;
    }
}

