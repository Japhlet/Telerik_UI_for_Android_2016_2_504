package com.telerik.widget.chart.engine.axes.continuous;

import com.telerik.widget.chart.engine.axes.AxisModel;

/**
 * Axis range plot info for range series.
 *
 * @see com.telerik.widget.chart.engine.series.rangeSeries.RangeBarSeriesModel
 * @see com.telerik.widget.chart.engine.dataPoints.RangeDataPoint
 */
public class NumericalAxisRangePlotInfo extends NumericalAxisPlotInfoBase {
    public double normalizedHigh;
    public double normalizedLow;
    public int snapBaseTickIndex = -1;

    private NumericalAxisRangePlotInfo() {
    }

    static NumericalAxisRangePlotInfo create(AxisModel axis, double plotOriginOffset, double normalizedHigh, double normalizedLow, double normalizedOrigin) {
        NumericalAxisRangePlotInfo info = new NumericalAxisRangePlotInfo();
        info.axis = axis;
        info.plotOriginOffset = plotOriginOffset;
        info.normalizedHigh = normalizedHigh;
        info.normalizedLow = normalizedLow;
        info.normalizedOrigin = normalizedOrigin;

        return info;
    }
}
