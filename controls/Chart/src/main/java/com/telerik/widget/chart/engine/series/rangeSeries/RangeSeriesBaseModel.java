package com.telerik.widget.chart.engine.series.rangeSeries;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisRangePlotInfo;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.dataPoints.RangeDataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;
import com.telerik.widget.chart.engine.series.CategoricalSeriesModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all range series models.
 */
public class RangeSeriesBaseModel extends CategoricalSeriesModel {

    /**
     * Creates a new instance of the {@link RangeSeriesBaseModel} class.
     */
    public RangeSeriesBaseModel() {
    }

    /**
     * Gets the default {@link AxisPlotMode} for all range series.
     */
    public AxisPlotMode getDefaultPlotMode() {
        return AxisPlotMode.ON_TICKS;
    }

    /**
     * Gets a value that determines whether the range series layout should be rounded.
     * The layout is not rounded by default.
     */
    protected boolean getShouldRoundLayout() {
        return false;
    }

//    @Override
//    public void applyLayoutRounding() {
//        //RangeSeriesBaseRoundLayoutContext info = new RangeSeriesBaseRoundLayoutContext(this);
//
//        AxisPlotDirection plotDirection = this.getTypedValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, AxisPlotDirection.VERTICAL);
//
//        Map<Double, Double> normalizedValueToY = new HashMap<Double, Double>();
//        Map<Double, Double> normalizedValueToX = new HashMap<Double, Double>();
//
//        for (CategoricalDataPointBase categoricalPoint : this.visibleDataPoints()) {
//            RangeDataPoint point = (RangeDataPoint)categoricalPoint;
//
//            if (point.isEmpty) {
//                continue;
//            }
//
//            //info.snapPointToGridLine(point);
//
//            // Handles specific scenario where range bar items from non-combined series have the same high/low value (floating point number) i.e.
//            // the presenters should be rendered on the same horizontal/vertical pixel row/column.
//            if (plotDirection == AxisPlotDirection.VERTICAL) {
//                //info.snapNormalizedValueToPreviousY(point, normalizedValueToY);
//            } else {
//                //info.snapNormalizedValueToPreviousX(point, normalizedValueToX);
//            }
//        }
//    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        AxisPlotDirection plotDirection = this.getTypedValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, AxisPlotDirection.VERTICAL);
        double x, y;
        double width, height;
        double panOffsetX = chartArea().getView().getPanOffsetX();
        double panOffsetY = chartArea().getView().getPanOffsetY();
        if (plotDirection == AxisPlotDirection.HORIZONTAL) {
            RadRect plotAreaRectZoomed = this.getZoomedRect(rect);

            for (CategoricalDataPointBase point : this.visibleDataPoints()) {
                if (point.categoricalPlot == null || point.numericalPlot == null) {
                    continue;
                }

                NumericalAxisRangePlotInfo plotInfo = (NumericalAxisRangePlotInfo)point.numericalPlot;

                height = point.categoricalPlot.length * plotAreaRectZoomed.getHeight();
                y = plotAreaRectZoomed.getBottom() - ((point.categoricalPlot.position * plotAreaRectZoomed.getHeight()) + height);
                width = (plotInfo.normalizedHigh - plotInfo.normalizedLow) * plotAreaRectZoomed.getWidth();
                x = plotAreaRectZoomed.getX() + (plotInfo.normalizedLow * plotAreaRectZoomed.getWidth());

                point.arrange(new RadRect(x + panOffsetX, y + panOffsetY, width, height), this.getShouldRoundLayout());
            }
        } else {
            RadRect plotAreaRectZoomed = this.getZoomedRect(rect);

            for (CategoricalDataPointBase point : this.visibleDataPoints()) {
                if (point.categoricalPlot == null || point.numericalPlot == null) {
                    continue;
                }

                NumericalAxisRangePlotInfo plotInfo = (NumericalAxisRangePlotInfo)point.numericalPlot;

                x = plotAreaRectZoomed.getX() + (point.categoricalPlot.position * plotAreaRectZoomed.getWidth());
                width = point.categoricalPlot.length * plotAreaRectZoomed.getWidth();
                height = (plotInfo.normalizedHigh - plotInfo.normalizedLow) * plotAreaRectZoomed.getHeight();
                y = plotAreaRectZoomed.getY() + ((1 - plotInfo.normalizedHigh) * plotAreaRectZoomed.getHeight());

                point.arrange(new RadRect(x + panOffsetX, y + panOffsetY, width, height), this.getShouldRoundLayout());
            }
        }

        return rect;
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child instanceof RangeDataPoint) {
            return ModifyChildrenResult.ACCEPT;
        }

        return super.canAddChild(child);
    }
}

