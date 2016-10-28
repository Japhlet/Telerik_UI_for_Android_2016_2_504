package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Path;
import android.graphics.Point;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.visualization.common.ChartSeries;

import java.util.List;

/**
 * Holds information needed for rendering an area of the chart.
 */
class AreaRenderContext {

    float strokeThickness;
    float strokeThicknessOffset;
    RadRect plotArea;
    double plotLine;
    boolean isStacked;
    List<Point> previousStackedPoints;
    ChartSeriesRenderer.DataPointSegment currentSegmentNode;
    double segmentStart;
    double segmentEnd;
    Path areaFigure;
    Path strokeFigure;
    Point currentSegmentFirstBottomPoint;
    Point lastBottomPoint;
    Point currentSegmentFirstTopPoint;
    Point currentSegmentLastTopPoint;
    int lastSegmentEndIndex;
    double lastSegmentXEnd;
    AxisPlotDirection plotDirection;
    int previousStackedPointsCurrentIndex;

    /**
     * Creates a new instance of the {@link AreaRenderContext} class with a specified
     * {@link AreaRendererBase} renderer instance.
     *
     * @param renderer the current renderer.
     */
    AreaRenderContext(AreaRendererBase renderer) {
        ChartSeries series = (ChartSeries) renderer.model.getPresenter();
        this.plotDirection = series.model().getTypedValue(AxisModel.PLOT_DIRECTION_PROPERTY_KEY, AxisPlotDirection.VERTICAL);

        this.strokeThicknessOffset = (int) (this.strokeThickness / 2);

        this.previousStackedPoints = series.getChart().stackedSeriesContext().getPreviousStackedArea();
        this.isStacked = this.previousStackedPoints != null && this.previousStackedPoints.size() > 0;

        this.plotArea = renderer.model.chartArea().getPlotArea().getLayoutSlot();
        double width = (this.plotArea.getWidth() * series.getChart().getZoom().getWidth());
        double height = (this.plotArea.getHeight() * series.getChart().getZoom().getHeight());
        this.plotArea = new RadRect(this.plotArea.getX(), this.plotArea.getY(), width, height);

        // calculate the plot line - consider plot origin
        double plotOrigin = renderer.model.getTypedValue(AxisModel.PLOT_ORIGIN_PROPERTY_KEY, 0.0);
        if (plotDirection == AxisPlotDirection.VERTICAL)
            this.plotLine = this.plotArea.getBottom() - (int) ((plotOrigin * this.plotArea.getHeight()) - renderer.layoutContext.panOffset().getY() + 0.5);
        else
            this.plotLine = this.plotArea.getX();
    }
}
