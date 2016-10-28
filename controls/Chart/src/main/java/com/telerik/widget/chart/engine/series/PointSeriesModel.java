package com.telerik.widget.chart.engine.series;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;

/**
 * Calculates the layout information for all line and area series.
 */
public class PointSeriesModel extends CategoricalSeriesModel {

    /**
     * Creates a new instance of the {@link PointSeriesModel} class.
     */
    public PointSeriesModel() {
    }

    @Override
    public AxisPlotMode getDefaultPlotMode() {
        return AxisPlotMode.ON_TICKS;
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        RadRect rectZoomed = this.getZoomedRect(rect);

        double x = 0, y = 0;
        AxisPlotDirection plotDirection = this.getTypedValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, AxisPlotDirection.VERTICAL);

        double panOffsetX = chartArea().getView().getPanOffsetX();
        double panOffsetY = chartArea().getView().getPanOffsetY();
        for (CategoricalDataPointBase point : this.visibleDataPoints()) {
            if (point.categoricalPlot == null && point.numericalPlot == null) {
                continue;
            }

            if (point.categoricalPlot != null) {
                if (plotDirection == AxisPlotDirection.VERTICAL) {
                    x = point.categoricalPlot.centerX(rectZoomed);
                } else {
                    y = point.categoricalPlot.centerY(rectZoomed);
                }
            }
            if (point.numericalPlot != null) {
                if (plotDirection == AxisPlotDirection.VERTICAL) {
                    y = point.numericalPlot.centerY(rectZoomed);
                } else {
                    x = point.numericalPlot.centerX(rectZoomed);
                }
            }

            point.arrange(new RadRect(x + panOffsetX, y + panOffsetY, 0, 0), false);
        }

        return rectZoomed;
    }
}
