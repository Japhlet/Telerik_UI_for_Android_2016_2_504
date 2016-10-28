package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.telerik.android.common.Function;
import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.widget.chart.engine.axes.common.AxisPlotDirection;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.FilledSeries;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class AreaRendererBase extends LineRenderer {

    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            AreaRendererBase renderer = (AreaRendererBase)sender;

            int value = renderer.getFillColor();
            renderer.fillColor = value;
            renderer.fillPaint.setColor(value);
        }
    });

    private int fillColor;
    private Path fillPath;

    /**
     * The current fill paint.
     *
     * @see Paint
     */
    protected Paint fillPaint;

    /**
     * The current set of data point segments.
     */
    protected ListIterator dataPointSegmentsIterator;

    /**
     * The current data point segment.
     */
    protected DataPointSegment currentSegmentNode;

    /**
     * The current collection of top surface points.
     */
    public List<Point> topSurfacePoints;

    /**
     * Creates a new instance of the {@link AreaRendererBase} class.
     */
    public AreaRendererBase() {
        super();

        this.fillPath = new Path();
        this.fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.topSurfacePoints = new ArrayList<Point>();
    }

    /**
     * Gets the current fill color id.
     *
     * @return the current fill color id.
     */
    public int getFillColor() {
        return (int)this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the current fill color id.
     *
     * @param value the new color id.
     */
    public void setFillColor(int value) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, value);
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public void setFillPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("paint argument cannot be null.");
        }

        this.fillPaint = paint;
    }

    @Override
    protected Path getPath() {
        return this.fillPath;
    }

    @Override
    public void applyPalette(ChartPalette palette) {
        super.applyPalette(palette);
        FilledSeries filledSeries = (FilledSeries) this.model.getPresenter();
        if (filledSeries == null)
            return;

        ChartSeries series = (ChartSeries) this.model.getPresenter();
        if (series.getIsSelected() && series.getChart().getSelectionPalette() != null) {
            palette = series.getChart().getSelectionPalette();
        }

        PaletteEntry entry = null;
        if (palette != null)
            entry = palette.getEntry(series.getPaletteFamilyCore(), this.model.getPresenter().getCollectionIndex());

        if (entry != null)
            this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getFill());
    }

    /**
     * Gets a value stating whether top stroke should be drawn.
     *
     * @return <code>true</code> if top stroke should be drawn and <code>false</code> otherwise.
     */
    protected boolean shouldDrawTopStroke() {
        return true;
    }

    @Override
    protected void reset() {
        super.reset();
        this.topSurfacePoints.clear();
        this.linePath.reset();
        this.fillPath.reset();
    }

    @Override
    protected void preparePaths() {
        if (indicateDataPoints()) {
            prepareDataPointIndicators(this.model.visibleDataPoints());
        }

        if (this.model.visibleDataPoints().size() < 2) {
            return;
        }

        AreaRenderContext context = new AreaRenderContext(this);

        LinkedList<DataPointSegment> dataPointSegments = this.dataPointSegments();
        dataPointSegmentsIterator = dataPointSegments.listIterator();

        while (dataPointSegmentsIterator.hasNext()) {
            currentSegmentNode = (DataPointSegment) dataPointSegmentsIterator.next();
            context.currentSegmentNode = currentSegmentNode;
            DataPointSegment dataPointSegment = currentSegmentNode;

            context.areaFigure = new Path();
            context.strokeFigure = new Path();
            this.prepareTopDrawingForSegment(context);
            this.prepareBottomDrawingForSegment(context);

            this.fillPath.addPath(context.areaFigure);
            this.linePath.addPath(context.strokeFigure);

            context.lastSegmentEndIndex = dataPointSegment.startIndex + dataPointSegment.dataPoints.size() - 1;
            context.lastSegmentXEnd = context.segmentEnd;
        }

        // Fill in top points
        if (this.model.visibleDataPoints().size() > 0)
            this.fillEmptyPointsToTopSurface(context, this.model.visibleDataPoints().size() - 1,
                    ((DataPoint) this.model.visibleDataPoints().get(this.model.visibleDataPoints().size() - 1)).getCenterX());

    }

    @Override
    protected void renderCore(Canvas canvas) {
        // we need at least two points to draw a line
        if (this.model.visibleDataPoints().size() > 1) {
            canvas.drawPath(this.fillPath, this.fillPaint);
            canvas.drawPath(this.linePath, this.linePaint);
        }

        if (indicateDataPoints()) {
            indicatorRenderer.drawDataPointIndicators(canvas);
        }
    }

    /**
     * Gets the top points for the current renderer.
     *
     * @param context context of the renderer.
     * @return the top points for the current renderer.
     */
    protected List<Point> topPoints(AreaRenderContext context) {
        return this.points(context, new Function<DataPoint, Point>() {
            @Override
            public Point apply(DataPoint argument) {
                return argument.getCenter();
            }
        });
    }

    /**
     * Gets the data points of the current renderer.
     *
     * @param context     context to be updated.
     * @param getLocation function for getting the location of a given point.
     * @return the collection of data points for the current renderer.
     */
    protected List<Point> points(AreaRenderContext context, Function<DataPoint, Point> getLocation) {
        DataPointSegment dataPointSegment = this.currentSegmentNode;
        List<Point> points = new ArrayList<Point>();
        for (int i = 0; i < dataPointSegment.dataPoints.size(); i++) {
            points.add(getLocation.apply(dataPointSegment.dataPoints.get(i)));
        }

        this.updateContext(context, points, points.get(0));

        return points;
    }

    /**
     * Updates a given {@link AreaRenderContext} context using the given collection of data points
     * starting from the given start point.
     *
     * @param context    the context to be updated.
     * @param points     the collection of points.
     * @param startPoint the starting point from which the update should start.
     * @see AreaRenderContext
     */
    protected void updateContext(AreaRenderContext context, List<Point> points, Point startPoint) {
        if (points.size() > 0) {
            if (context.plotDirection == AxisPlotDirection.VERTICAL) {
                context.segmentStart = points.get(0).x;
                context.segmentEnd = points.get(points.size() - 1).x;
            } else {
                context.segmentStart = points.get(0).y;
                context.segmentEnd = points.get(points.size() - 1).y;
            }
        }
    }

    /**
     * Gets the current bottom points for the renderer.
     *
     * @param context context holding the info about the renderer.
     * @return the current bottom points for the renderer.
     * @see AreaRenderContext
     */
    protected List<Point> bottomPoints(AreaRenderContext context) {
        List<Point> points = new ArrayList<Point>();
        if (!context.isStacked) {
            if (context.plotDirection == AxisPlotDirection.VERTICAL) {
                //first point
                points.add(new Point((int) context.segmentStart, (int) context.plotLine));
                // last point
                points.add(new Point((int) context.segmentEnd, (int) context.plotLine));
            } else {
                //first point
                points.add(new Point((int) context.plotLine, (int) context.segmentStart));
                // last point
                points.add(new Point((int) context.plotLine, (int) context.segmentEnd));
            }
        } else {
            this.bottomPointsForStackedSeries(context, points);
        }

        return points;
    }

    /**
     * Adds the bottom points for stacked series into the given points collection.
     *
     * @param context context from where the points will be extracted.
     * @param points  collection into which the points will be stored.
     */
    protected void bottomPointsForStackedSeries(AreaRenderContext context, List<Point> points) {
        List<Point> stackedPoints = context.previousStackedPoints;
        Point currentPoint;
        do {
            currentPoint = stackedPoints.get(context.previousStackedPointsCurrentIndex);
            context.previousStackedPointsCurrentIndex++;
            if (currentPoint.x < context.segmentStart) {
                continue;
            }
            points.add(currentPoint);
        }
        while (currentPoint.x < context.segmentEnd && context.previousStackedPointsCurrentIndex < stackedPoints.size());

        context.previousStackedPointsCurrentIndex--;

        if (points.size() > 1 && points.get(0).x == points.get(1).x) {
            points.remove(0);
        }
    }

    /**
     * Updates the context using the current top points of the passed {@link AreaRenderContext} and
     * makes the necessary changes to the top points of the current renderer instance.
     *
     * @param context the context to be updated.
     */
    protected void prepareTopDrawingForSegment(AreaRenderContext context) {

        List<Point> topPoints = this.topPoints(context);
        boolean shouldDrawStroke = this.shouldDrawTopStroke();

        if (topPoints.size() == 0) {
            return;
        }

        context.currentSegmentFirstTopPoint = topPoints.get(0);
        context.areaFigure.moveTo(context.currentSegmentFirstTopPoint.x, context.currentSegmentFirstTopPoint.y);

        context.currentSegmentLastTopPoint = topPoints.get(topPoints.size() - 1);

        if (shouldDrawStroke) {
            context.strokeFigure.moveTo(context.currentSegmentFirstTopPoint.x, context.currentSegmentFirstTopPoint.y);
        }

        DataPointSegment dataPointSegment = this.currentSegmentNode;
        this.fillEmptyPointsToTopSurface(context, dataPointSegment.startIndex, context.segmentStart);

        this.topSurfacePoints.add(topPoints.get(0));

        for (int i = 1; i < topPoints.size(); i++) {
            Point point = topPoints.get(i);
            context.areaFigure.lineTo(point.x, point.y);
            this.topSurfacePoints.add(point);

            if (shouldDrawStroke) {
                context.strokeFigure.lineTo(point.x, point.y);
            }
        }
    }

    private void fillEmptyPointsToTopSurface(AreaRenderContext context, int lastDataPointIndex, double lastXPoint) {
        if (context.lastSegmentEndIndex == lastDataPointIndex || context.lastSegmentXEnd == lastXPoint) {
            return;
        }

        if (context.isStacked) {
            int lastTopSurfacePointsCount = this.topSurfacePoints.size();
            List<Point> stackedPoints = context.previousStackedPoints;
            Point currentPoint;
            do {
                currentPoint = stackedPoints.get(context.previousStackedPointsCurrentIndex);
                context.previousStackedPointsCurrentIndex++;
                if (currentPoint.x < context.lastSegmentXEnd) {
                    continue;
                }
                this.topSurfacePoints.add(currentPoint);
            }
            while (currentPoint.x < lastXPoint && context.previousStackedPointsCurrentIndex < stackedPoints.size());

            context.previousStackedPointsCurrentIndex--;

            if (lastTopSurfacePointsCount > 0 &&
                    this.topSurfacePoints.get(lastTopSurfacePointsCount - 1).x == this.topSurfacePoints.get(lastTopSurfacePointsCount).x &&
                    this.topSurfacePoints.get(lastTopSurfacePointsCount).x == this.topSurfacePoints.get(lastTopSurfacePointsCount + 1).x) {
                this.topSurfacePoints.remove(lastTopSurfacePointsCount);
            }
        } else {
            for (int i = context.lastSegmentEndIndex; i <= lastDataPointIndex; i++) {
                DataPoint dataPoint = (DataPoint) this.model.dataPoints().get(i);
                this.topSurfacePoints.add(new Point((int) dataPoint.getCenterX(), (int) context.plotLine));
            }
        }
    }

    private void prepareBottomDrawingForSegment(AreaRenderContext context) {

        List<Point> bottomPoints = this.bottomPoints(context);
        if (bottomPoints.size() == 0) {
            return;
        }

        context.currentSegmentFirstBottomPoint = bottomPoints.get(0);
        context.lastBottomPoint = bottomPoints.get(bottomPoints.size() - 1);

        for (int i = bottomPoints.size() - 1; i >= 0; i--) {
            Point point = bottomPoints.get(i);
            context.areaFigure.lineTo(point.x, point.y);
        }
    }
}

