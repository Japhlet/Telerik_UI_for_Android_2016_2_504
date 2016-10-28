package com.telerik.widget.chart.engine.dataPoints;

import com.telerik.widget.chart.engine.axes.categorical.CategoricalAxisPlotInfo;
import com.telerik.widget.chart.engine.axes.continuous.NumericalAxisPlotInfoBase;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;

/**
 * Represents a single-value data point, which may be plotted in a
 * {@link com.telerik.widget.chart.engine.chartAreas.CartesianChartAreaModel}.
 */
public abstract class CategoricalDataPointBase extends DataPoint {

    private static final int CATEGORY_PROPERTY_KEY =
            PropertyKeys.register(CategoricalDataPointBase.class, "Category",
                    ChartAreaInvalidateFlags.ALL);

    public NumericalAxisPlotInfoBase numericalPlot;
    public CategoricalAxisPlotInfo categoricalPlot;

    /**
     * Gets the Object instance that describes the category of the point.
     *
     * @return the category object instance.
     */
    public Object getCategory() {
        return this.getValue(CATEGORY_PROPERTY_KEY);
    }

    /**
     * Sets the Object instance that describes the category of the point.
     *
     * @param value the new category object instance.
     */
    public void setCategory(Object value) {
        this.setValue(CATEGORY_PROPERTY_KEY, value);
    }
}

