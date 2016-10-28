package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Point;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents an {@link AreaRendererBase} that adds the functionality of rendering areas ending with
 * a spline like shape instead of the normal rectangular form.
 */
public class SplineAreaRenderer extends AreaRendererBase {

    /**
     * Creates a new instance of the {@link SplineAreaRenderer} class.
     */
    public SplineAreaRenderer() {
    }

    @Override
    protected List<Point> topPoints(AreaRenderContext context) {
        List<Point> points = new ArrayList<Point>();
        RadChartViewBase chart = ((ChartSeries) this.model.getPresenter()).getChart();
        DataPointSegment currentSegmentNode = context.currentSegmentNode;
        LinkedList<DataPointSegment> dataPointSegments = this.dataPointSegments();
        ListIterator<DataPointSegment> segmentListIterator = dataPointSegments.listIterator();
        DataPoint previousPoint = this.findPreviousNonEmptyPoint(currentSegmentNode, segmentListIterator);
        DataPoint nextPoint = this.findNextNonEmptyPoint(currentSegmentNode, segmentListIterator);

        List<DataPoint> meaningfulDataPoints = new ArrayList<DataPoint>();
        int pointsCount = currentSegmentNode.dataPoints.size();

        // Should add 2 points past the last visible to obtain correct spline visualization.
        for (int i = 0; i < pointsCount; i++) {
            DataPoint point = currentSegmentNode.dataPoints.get(i);

            if (point.isEmpty)
                continue;

            meaningfulDataPoints.add(point);
        }

        Point startPoint = null;
        SplineHelper<DataPoint> splineHelper = new SplineHelper<DataPoint>();
        if (meaningfulDataPoints.size() > 0) {
            startPoint = meaningfulDataPoints.get(0).getCenter();
            context.areaFigure.moveTo(startPoint.x, startPoint.y);
            for (Point item : splineHelper.getSplinePoints(
                    meaningfulDataPoints, chart.getZoom(), previousPoint, nextPoint)) {
                points.add(item);
            }
        }

        this.updateContext(context, points, startPoint);

        return points;
    }

    @Override
    protected void bottomPointsForStackedSeries(AreaRenderContext context, List<Point> points) {
        // ADD one point prior to the first visible point as SegmentXStart
        // might not match for different stacked series.
        List<Point> stackedPoints = context.previousStackedPoints;
        Point currentPoint;
        Point previousPoint = stackedPoints.get(context.previousStackedPointsCurrentIndex);
        boolean firstPointAdded = false;

        do {
            currentPoint = stackedPoints.get(context.previousStackedPointsCurrentIndex);
            context.previousStackedPointsCurrentIndex++;

            if (currentPoint.x < context.segmentStart) {
                previousPoint = currentPoint;
                continue;
            }

            if (!firstPointAdded) {
                points.add(previousPoint);
                firstPointAdded = true;
            }

            previousPoint = currentPoint;
            points.add(currentPoint);
        }
        while (currentPoint.x < context.segmentEnd &&
                context.previousStackedPointsCurrentIndex < stackedPoints.size());

        context.previousStackedPointsCurrentIndex--;

        if (points.size() > 1 && points.get(0).x == points.get(1).x) {
            points.remove(0);
        }
    }
}
