package com.telerik.widget.chart.visualization.behaviors;

import android.graphics.Point;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;

import java.util.List;

/**
 * An object of this type is used as a data context for chart's behaviors.
 * For example the tool tip behavior can use a chart data context to populate
 * its tool tip template with data.
 */
public class ChartDataContext {
    private DataPointInfo closestDataPoint;
    private List<DataPointInfo> dataPointInfos;
    private List<DataPoint> dataPoints;
    private RadChartViewBase chart;
    private Point touchLocation;

    /**
     * Creates a new instance of the {@link ChartDataContext} class.
     *
     * @param infos        set of data point infos.
     * @param closestPoint the closest data point.
     */
    public ChartDataContext(RadChartViewBase chart, List<DataPointInfo> infos, List<DataPoint> dataPoints, DataPointInfo closestPoint) {
        this.dataPointInfos = infos;
        this.closestDataPoint = closestPoint;
        this.dataPoints = dataPoints;
        this.chart = chart;
    }

    /**
     * Returns the chart that created this context.
     */
    public RadChartViewBase chart() {
        return this.chart;
    }

    /**
     * Gets the physical point (in coordinates, relative to the chart surface) this
     * context is associated with.
     *
     * @return the current touch lastLocation.
     * @see Point
     */
    public Point getTouchLocation() {
        return this.touchLocation;
    }

    /**
     * Sets the physical point (in coordinates, relative to the chart surface) this
     * context is associated with.
     *
     * @param value the new touch point.
     * @see Point
     */
    public void setTouchLocation(Point value) {
        this.touchLocation = value;
    }

    /**
     * Gets an object that contains the closest data point to the tap lastLocation
     * and the series object to which the data point belongs.
     *
     * @return the current closest data point.
     * @see DataPointInfo
     */
    public DataPointInfo getClosestDataPoint() {
        return this.closestDataPoint;
    }

    /**
     * Sets an object that contains the closest data point to the tap lastLocation
     * and the series object to which the data point belongs.
     *
     * @param value the new closest data point.
     * @see DataPointInfo
     */
    public void setClosestDataPoint(DataPointInfo value) {
        this.closestDataPoint = value;
    }

    /**
     * Gets a list of data point infos each of which contains the closest data
     * point to the tap lastLocation and the point's corresponding series.
     *
     * @return the current set of data point infos.
     */
    public List<DataPointInfo> getDataPointInfos() {
        return this.dataPointInfos;
    }

    /**
     * Sets a list of data point infos each of which contains the closest data
     * point to the tap lastLocation and the point's corresponding series.
     *
     * @param value the new set of data point infos.
     */
    public void setDataPointInfos(List<DataPointInfo> value) {
        this.dataPointInfos = value;
    }

    public List<DataPoint> getDataPoints() {
        return this.dataPoints;
    }
}
