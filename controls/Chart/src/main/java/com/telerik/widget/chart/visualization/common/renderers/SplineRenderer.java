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
 * Represents a {@link LineRenderer} that adds spline rendering functionality on top of the normal
 * line rendering.
 */
public class SplineRenderer extends LineRenderer {

    /**
     * Creates a new instance of the {@link SplineRenderer} class.
     */
    public SplineRenderer() {
        super();
    }

    @Override
    protected void preparePaths() {
        RadChartViewBase chart = ((ChartSeries) this.model.getPresenter()).getChart();
        LinkedList<DataPointSegment> dataPointSegments = this.dataPointSegments();
        ListIterator<DataPointSegment> segmentListIterator = dataPointSegments.listIterator();

        if (indicateDataPoints())
            prepareDataPointIndicators(model.visibleDataPoints());

        while (segmentListIterator.hasNext()) {
            DataPointSegment currentSegmentNode = segmentListIterator.next();

            DataPoint previousPoint =
                    this.findPreviousNonEmptyPoint(currentSegmentNode, segmentListIterator);

            DataPoint nextPoint = this.findNextNonEmptyPoint(currentSegmentNode, segmentListIterator);

            List<DataPoint> meaningfulDataPoints = new ArrayList<DataPoint>();

            int pointsCount = currentSegmentNode.dataPoints.size();

            for (int i = 0; i < pointsCount; i++) {
                DataPoint point = currentSegmentNode.dataPoints.get(i);

                if (point.isEmpty)
                    continue;

                meaningfulDataPoints.add(point);
            }

            SplineHelper<DataPoint> splineHelper = new SplineHelper<DataPoint>();
            if (meaningfulDataPoints.size() > 0) {
                Point startPoint = meaningfulDataPoints.get(0).getCenter();
                this.linePath.moveTo(startPoint.x, startPoint.y);
                for (Point item : splineHelper.getSplinePoints(
                        meaningfulDataPoints, chart.getZoom(), previousPoint, nextPoint)) {
                    this.linePath.lineTo(item.x, item.y);
                }
            }
        }
    }
}


