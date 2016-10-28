package com.telerik.widget.chart.engine.axes.categorical;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;

import java.util.ArrayList;

/**
 * Defines an axis category used in the {@link CategoricalAxisModel}.
 */
public class AxisCategory {
    /**
     * The key used to determine distinct categories
     */
    public Object key;

    /**
     * The source Object, containing the key.
     * This for example is used by the {@link DateTimeCategoricalAxisModel} where key is the day while
     * the key source is the associated date.
     */
    public Object keySource;

    /**
     * The {@link DataPoint} instances that fall into this category.
     */
    public final ArrayList<DataPoint> points = new ArrayList<DataPoint>();

    /**
     * Creates a new instance of the {@link AxisCategory} class.
     */
    public AxisCategory() {
    }
}