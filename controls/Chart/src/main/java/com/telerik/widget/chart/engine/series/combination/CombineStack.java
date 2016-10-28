package com.telerik.widget.chart.engine.series.combination;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;

import java.util.ArrayList;

/**
 * Contains information for a collection of stacked series.
 */
public class CombineStack {

    private final ArrayList<DataPoint> points = new ArrayList<DataPoint>();

    public Object key;
    public double positiveSum;
    public double negativeSum;

    /**
     * Creates a new instance of the {@link CombineStack} class.
     */
    public CombineStack() {
    }

    public ArrayList<DataPoint> points() {
        return this.points;
    }
}

