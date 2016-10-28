package com.telerik.widget.chart.visualization.behaviors;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.common.ChartSeries;

/**
 * This class contains the closest data point to a tap lastLocation as well as the point's
 * corresponding series object.
 */
public class DataPointInfo {

    private ChartSeriesModel series;
    private Object displayContent;
    private Object displayHeader;
    private DataPoint dataPoint;
    private double distanceToTouchLocation;
    private int priority;
    private boolean containsTouchLocation;

    /**
     * Creates a new instance of the {@link DataPointInfo} class.
     */
    public DataPointInfo() {
        this.setPriority(1);
    }

    /**
     * Gets the series object that contains the data point.
     *
     * @return the series that contain the data point
     */
    public ChartSeries series() {
        if (this.series == null) {
            return null;
        }

        return (ChartSeries) this.series.getPresenter();
    }

    /**
     * Gets a value indicating whether the layout slot of the data point contains the touch lastLocation.
     *
     * @return <code>true</code> if the context contains the touch lastLocation.
     */
    public boolean getContainsTouchLocation() {
        return this.containsTouchLocation;
    }

    /**
     * Sets a value indicating whether the layout slot of the data point contains the touch lastLocation.
     *
     * @param value the new value.
     */
    public void setContainsTouchLocation(boolean value) {
        this.containsTouchLocation = value;
    }

    /**
     * Gets the data point in the series object that is closest to the tap lastLocation.
     *
     * @return the current data point.
     */
    public DataPoint getDataPoint() {
        return this.dataPoint;
    }

    /**
     * Sets the data point in the series object that is closest to the tap lastLocation.
     *
     * @param value the new data point.
     */
    public void setDataPoint(DataPoint value) {
        this.dataPoint = value;
    }

    /**
     * Gets the name of the series which host the associated data point.
     *
     * @return the current display header.
     */
    public Object getDisplayHeader() {
        if (this.displayHeader != null) {
            return this.displayHeader;
        }

        ChartSeries series = this.series();
        if (series != null) {
            // TODO c# return String.IsNullOrEmpty(series.DisplayName) ? series.GetType().Name : series.DisplayName;
        }

        return "";
    }

    /**
     * Sets the name of the series which host the associated data point.
     *
     * @param value the new display header.
     */
    public void setDisplayHeader(Object value) {
        this.displayHeader = value;
    }

    /**
     * Gets the object that visually represents the value of the associated point.
     *
     * @return the current display content.
     */
    public Object getDisplayContent() {
        if (this.displayContent != null) {
            return this.displayContent;
        }

        return this.dataPoint.getTooltipTokens();

    }

    /**
     * Sets the object that visually represents the value of the associated point.
     *
     * @param value the new display content.
     */
    public void setDisplayContent(Object value) {
        this.displayContent = value;
    }

    /**
     * Gets the distance to the touch lastLocation.
     *
     * @return the current distance to the touch lastLocation.
     */
    public double getDistanceToTouchLocation() {
        return this.distanceToTouchLocation;
    }

    /**
     * Sets the distance to the touch lastLocation.
     *
     * @param value the new distance to the touch lastLocation.
     */
    public void setDistanceToTouchLocation(double value) {
        this.distanceToTouchLocation = value;
    }

    /**
     * Gets the current series.
     *
     * @return the current series.
     * @see ChartSeriesModel
     */
    public ChartSeriesModel getSeriesModel() {
        return this.series;
    }

    /**
     * Sets the current series.
     *
     * @param value the new series.
     * @see ChartSeriesModel
     */
    public void setSeriesModel(ChartSeriesModel value) {
        this.series = value;
        this.onSeriesModelChanged();
    }

    /**
     * Gets the current priority.
     *
     * @return the current priority.
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * Sets the current priority.
     *
     * @param value the new priority.
     */
    public void setPriority(int value) {
        this.priority = value;
    }

    private void onSeriesModelChanged() {
            /* TODO c#
            if (this.series() instanceof IndicatorBase)
            {
                this.setPriority(100);
            }*/
    }
}

