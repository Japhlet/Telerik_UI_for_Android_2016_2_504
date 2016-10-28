package com.telerik.widget.chart.engine.series;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisOhlcPlotInfo;
import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;

/**
 * Represents the Open, High, Low, Close financial chart series.
 */
public class OhlcSeriesModel extends SeriesModelWithAxes<OhlcDataPoint> {
    @Override
    public AxisPlotMode getDefaultPlotMode() {
        return AxisPlotMode.ON_TICKS_PADDED;
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        AxisPlotDirection plotDirection = this.getTypedValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, AxisPlotDirection.VERTICAL);
        if (plotDirection == AxisPlotDirection.HORIZONTAL) {
            throw new UnsupportedOperationException("HORIZONTAL OHLC series are not supported.");
        }

        RadRect plotAreaRectZoomed = this.getZoomedRect(rect);
        double panOffsetX = chartArea().getView().getPanOffsetX();
        double panOffsetY = chartArea().getView().getPanOffsetY();
        for (OhlcDataPoint point : this.visibleDataPoints()) {
            if (point.categoricalPlot == null || point.getNumericalPlot() == null) {
                continue;
            }

            NumericalAxisOhlcPlotInfo plotInfo = point.getNumericalPlot();
            plotInfo.physicalOpen = (plotAreaRectZoomed.getY() + (1 - plotInfo.normalizedOpen) * plotAreaRectZoomed.getHeight()) + panOffsetY;
            plotInfo.physicalClose = (plotAreaRectZoomed.getY() + (1 - plotInfo.normalizedClose) * plotAreaRectZoomed.getHeight()) + panOffsetY;

            double y = plotAreaRectZoomed.getY() + (1 - Math.max(plotInfo.normalizedHigh, plotInfo.plotOriginOffset)) * plotAreaRectZoomed.getHeight();
            double x = plotAreaRectZoomed.getX() + point.categoricalPlot.position * plotAreaRectZoomed.getWidth();
            double width = point.categoricalPlot.length * plotAreaRectZoomed.getWidth();
            double height = Math.abs(plotInfo.normalizedHigh - plotInfo.normalizedLow) * plotAreaRectZoomed.getHeight();

            point.arrange(new RadRect(x + panOffsetX, y + panOffsetY, width, height));
        }
        return plotAreaRectZoomed;
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child instanceof OhlcDataPoint) {
            return ModifyChildrenResult.ACCEPT;
        }

        return super.canAddChild(child);
    }
}

