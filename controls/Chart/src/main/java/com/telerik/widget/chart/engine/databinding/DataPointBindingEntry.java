package com.telerik.widget.chart.engine.databinding;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;

/**
 * A class holding a data item and the data point this item is bound to.
 */
public class DataPointBindingEntry {

    private Object dataItem;
    private DataPoint dataPoint;

    /**
     * Creates an instance of the {@link DataPointBindingEntry} class that holds data item
     * and its associated {@link DataPoint}.
     *
     * @param dataItem  The data item.
     * @param dataPoint The data point associated with the data item.
     */
    public DataPointBindingEntry(Object dataItem, DataPoint dataPoint) {
        this.dataItem = dataItem;
        this.dataPoint = dataPoint;
    }

    /**
     * Gets the current data item.
     *
     * @return the current data item as an Object.
     */
    public Object getDataItem() {
        return dataItem;
    }

    /**
     * Gets the current data point.
     *
     * @return the current data point.
     */
    public DataPoint getDataPoint() {
        return dataPoint;
    }
}