package com.telerik.widget.chart.engine.series;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfo;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesPlotStrategy;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesRoundLayoutStrategy;
import com.telerik.widget.chart.engine.series.combination.barSeries.CombinedBarSeriesPlotStrategy;
import com.telerik.widget.chart.engine.series.combination.barSeries.CombinedBarSeriesRoundLayoutStrategy;

/**
 * The BarSeriesModel class calculates the size and location of the bars.
 */
public class BarSeriesModel extends CategoricalSeriesModel {

    private double maxBarWidth;

    /**
     * Creates a new instance of the {@link BarSeriesModel} class.
     */
    public BarSeriesModel() {
    }

    @Override
    public CombinedSeriesPlotStrategy getCombinedPlotStrategy() {
        return new CombinedBarSeriesPlotStrategy();
    }

    @Override
    public CombinedSeriesRoundLayoutStrategy getCombinedRoundLayoutStrategy() {
        return new CombinedBarSeriesRoundLayoutStrategy();
    }

    @Override
    public void applyLayoutRounding() {
        CategoricalSeriesRoundLayoutContext info = new CategoricalSeriesRoundLayoutContext(this);
        for (CategoricalDataPointBase point : this.visibleDataPoints()) {
            info.snapPointToPlotLine(point);
            info.snapPointToGridLine(point);
        }
    }

    public void setMaxBarWidth(double value) {
        maxBarWidth = value;
    }

    public double getMaxBarWidth() {
        return maxBarWidth;
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        RadRect plotAreaRectZoomed = this.getZoomedRect(rect);

        double x, y;
        double width, height;
        AxisPlotDirection plotDirection = this.getTypedValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, AxisPlotDirection.VERTICAL);

        double panOffsetX = chartArea().getView().getPanOffsetX();
        double panOffsetY = chartArea().getView().getPanOffsetY();

        for (CategoricalDataPointBase point : this.visibleDataPoints()) {
            if (point.categoricalPlot == null || point.numericalPlot == null) {
                continue;
            }

            NumericalAxisPlotInfo numericInfo = (NumericalAxisPlotInfo) point.numericalPlot;

            if (plotDirection == AxisPlotDirection.VERTICAL) {
                // vertical bars
                x = plotAreaRectZoomed.getX() + (point.categoricalPlot.position + point.categoricalPlot.length / 2) * plotAreaRectZoomed.getWidth();
                width = point.categoricalPlot.length * plotAreaRectZoomed.getWidth();
                height = Math.abs(numericInfo.normalizedValue - point.numericalPlot.plotOriginOffset) * plotAreaRectZoomed.getHeight();
                if(maxBarWidth != 0) {
                    width = Math.min(width, maxBarWidth);
                }
                x -= width / 2;
                y = plotAreaRectZoomed.getY() + ((1 - Math.max(numericInfo.normalizedValue, point.numericalPlot.plotOriginOffset)) * plotAreaRectZoomed.getHeight());

                // Avoids having invisible bars when they are less than 1 pixel wide. When the width is less than 1 it gets rounded to 0.
                if(width < 1) {
                    width = 1;
                }
            } else {
                // horizontal bars
                x = plotAreaRectZoomed.getX() + (Math.min(numericInfo.normalizedValue, point.numericalPlot.plotOriginOffset) * plotAreaRectZoomed.getWidth());
                width = Math.abs(numericInfo.normalizedValue - point.numericalPlot.plotOriginOffset) * plotAreaRectZoomed.getWidth();
                height = point.categoricalPlot.length * plotAreaRectZoomed.getHeight();
                if(maxBarWidth != 0) {
                    height = Math.min(height, maxBarWidth);
                }
                y = plotAreaRectZoomed.getBottom() - ((point.categoricalPlot.position + point.categoricalPlot.length / 2) * plotAreaRectZoomed.getHeight() + height / 2);

                if(height < 1) {
                    height = 1;
                }
            }

            point.arrange(new RadRect(x + panOffsetX, y + panOffsetY, width, height));
        }

        return plotAreaRectZoomed;
    }
}

