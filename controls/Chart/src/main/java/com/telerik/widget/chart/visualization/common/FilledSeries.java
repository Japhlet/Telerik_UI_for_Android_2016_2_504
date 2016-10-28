package com.telerik.widget.chart.visualization.common;

/**
 * Identifies chart series that support filled areas.
 */
public interface FilledSeries {

    /**
     * Gets the color used to fill the corresponding are in the implementing {@link com.telerik.widget.chart.visualization.common.ChartSeries}
     * instance.
     *
     * @return an integer representing the fill color.
     */
    int getFillColor();
}
