package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Canvas;

import com.telerik.android.common.PropertyManager;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.palettes.ChartPalette;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Base class for all renders of the chart series.
 */
public abstract class ChartSeriesRenderer extends PropertyManager {

    /**
     * The current chart series model.
     */
    protected ChartSeriesModel model;

    /**
     * Creates a new instance of the {@link ChartSeriesRenderer} class.
     */
    public ChartSeriesRenderer() {
    }

    /**
     * Used to reset the renderer and to prepare the current paths.
     */
    public void prepare() {
        this.reset();

        if (this.model.visibleDataPoints().size() == 0) {
            return;
        }

        this.preparePaths();
    }

    /**
     * Used to invoke the {@link #renderCore(android.graphics.Canvas)} method of the corresponding
     * renderer implementation.
     *
     * @param canvas the canvas on which the data will be rendered.
     * @see Canvas
     */
    public void render(Canvas canvas) {

        if (this.model.visibleDataPoints().size() == 0) {
            return;
        }

        this.renderCore(canvas);
    }

    /**
     * Applies the color of the current palette entry to the current renderer implementation.
     */
    public void applyPalette(ChartPalette palette) {
    }

    /**
     * Gets the current data point segments generated from the current model's data points.
     *
     * @return the current data point segments.
     */
    public LinkedList<DataPointSegment> dataPointSegments() { // TODO: Check this for possible optimization
        List<DataPoint> allDataPoints = this.model.visibleDataPoints();
        LinkedList<DataPointSegment> segments = new LinkedList();
        DataPointSegment currentSegment = null;

        for (int i = 0; i < allDataPoints.size(); i++) {
            DataPoint current = allDataPoints.get(i);
            if (current.isEmpty) {
                if (currentSegment != null && currentSegment.dataPoints.size() > 1) {
                    segments.addLast(currentSegment);
                }

                currentSegment = null;
                continue;
            }

            if (currentSegment == null) {
                currentSegment = new DataPointSegment();
                currentSegment.startIndex = i;
            }

            currentSegment.dataPoints.add(current);
        }

        if (currentSegment != null && currentSegment.dataPoints.size() > 1) {
            segments.addLast(currentSegment);
        }

        return segments;
    }

    /**
     * Reset the current renderer implementation.
     */
    protected abstract void reset();

    /**
     * Draws the information stored in the paths of the current renderer implementation.
     *
     * @param canvas the canvas on which the data will be rendered.
     * @see Canvas
     */
    protected abstract void renderCore(Canvas canvas);

    /**
     * Prepares the paths of the current renderer implementation with the appropriate paths by
     * using the data points stored in the model.
     */
    protected abstract void preparePaths();

    /**
     * Finds the previous non empty point in the data points of the model.
     *
     * @param currentSegment      TODO:
     * @param segmentListIterator TODO:
     * @return the previous non empty data point.
     * @see DataPoint
     */
    protected DataPoint findPreviousNonEmptyPoint(DataPointSegment currentSegment, ListIterator<DataPointSegment> segmentListIterator) {
        List<DataPoint> points = this.model.visibleDataPoints();
        DataPoint previousPoint = null;
        if (segmentListIterator.hasPrevious()) {
            DataPointSegment previousSegment = segmentListIterator.previous();
            for (int i = currentSegment.startIndex - 1; i >= previousSegment.startIndex; i--) {
                DataPoint point = points.get(i);
                if (!point.isEmpty) {
                    previousPoint = point;
                    break;
                }
            }
        } else if (currentSegment.startIndex > 0) {
            for (int i = currentSegment.startIndex - 1; i >= 0; i--) {
                DataPoint point = points.get(i);
                if (!point.isEmpty) {
                    previousPoint = point;
                    break;
                }
            }
        }

        return previousPoint;
    }

    /**
     * Finds the next non empty point in the data points of the model.
     *
     * @param currentSegment      TODO
     * @param segmentListIterator TODO
     * @return the next non empty data point
     * @see DataPoint
     */
    protected DataPoint findNextNonEmptyPoint(DataPointSegment currentSegment, ListIterator<DataPointSegment> segmentListIterator) {
        DataPoint nextPoint = null;
        List<DataPoint> points = this.model.visibleDataPoints();
        if (segmentListIterator.hasNext()) {
            DataPointSegment nextSegment = segmentListIterator.next();
            for (int i = currentSegment.startIndex + currentSegment.dataPoints.size(); i <= nextSegment.startIndex; i++) {
                DataPoint point = points.get(i);
                if (!point.isEmpty) {
                    nextPoint = point;
                    break;
                }
            }
        } else if (currentSegment.startIndex + currentSegment.dataPoints.size() != points.size()) {
            for (int i = currentSegment.startIndex + currentSegment.dataPoints.size(); i < points.size(); i++) {
                DataPoint point = points.get(i);
                if (!point.isEmpty) {
                    nextPoint = point;
                    break;
                }
            }
        }

        return nextPoint;
    }

    /**
     * Used to store a list of data points and present them as a segment with a starting index.
     */
    protected class DataPointSegment {

        /**
         * The data points collection.
         */
        public List<DataPoint> dataPoints;

        /**
         * The starting index for the segment.
         */
        public int startIndex;

        /**
         * Creates a new instance of the {@link DataPointSegment} class.
         */
        public DataPointSegment() {
            this.dataPoints = new ArrayList<DataPoint>();
        }
    }
}

