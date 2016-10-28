package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Point;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;

import java.util.ArrayList;
import java.util.List;

class SplineHelper<T extends DataPoint> {
    private static final float DEFAULT_TENSION = 0.5F;
    //private static final float DEFAULT_TOLERANCE = 5F;
    private static final float MIN_TOLERANCE = 1F;
    private static final float POINT_DISTANCE_FACTOR = 0.03F;

    public List<Point> getSplinePoints(List<T> dataPoints, RadSize scale, T startPoint, T endPoint) {

        List<Point> list = new ArrayList<Point>();

        int count = dataPoints.size();
        if (count == 0) {
            return list;
        }

        if (count == 1) {
            list.add(dataPoints.get(0).getCenter());
            return list;
        }

        if (startPoint == null) {
            startPoint = dataPoints.get(0);
        }
        if (endPoint == null) {
            endPoint = dataPoints.get(dataPoints.size() - 1);
        }

        //float tolerance = DEFAULT_TOLERANCE;
        //float zoomFactor = Math.abs(scale.width - scale.height) / 2;

        /*if (zoomFactor > 2) {
            tolerance *= (int) zoomFactor / 2;
        }*/

        if (count == 2) {
            Point firstPoint = dataPoints.get(0).getCenter();
            Point secondPoint = dataPoints.get(1).getCenter();
            for (Point segmentedPoint : segment(firstPoint, firstPoint, secondPoint, secondPoint)) {
                list.add(segmentedPoint);
            }
            return list;
        }

        List<Point> segmentedPoints;
        for (int i = 0; i < count - 1; i++) {
            if (i == 0) {
                segmentedPoints = segment(
                        startPoint.getCenter(),
                        dataPoints.get(0).getCenter(),
                        dataPoints.get(1).getCenter(),
                        dataPoints.get(2).getCenter());
            } else if (i == count - 2) {
                segmentedPoints = segment(
                        dataPoints.get(i - 1).getCenter(),
                        dataPoints.get(i).getCenter(),
                        dataPoints.get(i + 1).getCenter(),
                        endPoint.getCenter());
            } else {
                segmentedPoints = segment(
                        dataPoints.get(i - 1).getCenter(),
                        dataPoints.get(i).getCenter(),
                        dataPoints.get(i + 1).getCenter(),
                        dataPoints.get(i + 2).getCenter());
            }

            for (Point point : segmentedPoints) {
                list.add(point);
            }
        }

        return list;
    }

    private static List<Point> segment(Point pt0, Point pt1, Point pt2, Point pt3) { // TODO: tolerance not used
        List<Point> list = new ArrayList<Point>();

        float sX1 = DEFAULT_TENSION * (pt2.x - pt0.x);
        float sY1 = DEFAULT_TENSION * (pt2.y - pt0.y);
        float sX2 = DEFAULT_TENSION * (pt3.x - pt1.x);
        float sY2 = DEFAULT_TENSION * (pt3.y - pt1.y);

        float ax = sX1 + sX2 + (2 * pt1.x) - (2 * pt2.x);
        float ay = sY1 + sY2 + (2 * pt1.y) - (2 * pt2.y);
        float bx = (-2 * sX1) - sX2 - (3 * pt1.x) + (3 * pt2.x);
        float by = (-2 * sY1) - sY2 - (3 * pt1.y) + (3 * pt2.y);

        float dx = pt1.x;
        float dy = pt1.y;

        float distance = (float) Math.sqrt(Math.pow(pt1.x - pt2.x, 2) + Math.pow(pt1.y - pt2.y, 2));
        float tolerance = (POINT_DISTANCE_FACTOR * distance) + MIN_TOLERANCE;
        int num = (int) (distance / tolerance);

        for (int i = 0; i < num; i++) {
            float t = (num == 1) ? 0 : (float) i / (num - 1);
            list.add(new Point((int) ((ax * t * t * t) + (bx * t * t) + (sX1 * t) + dx),
                    (int) ((ay * t * t * t) + (by * t * t) + (sY1 * t) + dy)));
        }
        return list;
    }
}

