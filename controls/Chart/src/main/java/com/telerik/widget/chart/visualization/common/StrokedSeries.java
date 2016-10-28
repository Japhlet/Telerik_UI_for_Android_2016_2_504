package com.telerik.widget.chart.visualization.common;

/**
 * Identifies Chart Series that support setting stroke color and stroke thickness. Such series
 * are {@link com.telerik.widget.chart.visualization.cartesianChart.series.categorical.SplineSeries},
 * {@link com.telerik.widget.chart.visualization.cartesianChart.series.categorical.LineSeries}, etc.
 */
public interface StrokedSeries {

    /**
     * Gets the color used to draw the series' stroke.
     *
     * @return an integer representing the color.
     */
    int getStrokeColor();

    /**
     * Gets the thickness of the stroke in pixels.
     *
     * @return the thickness.
     */
    float getStrokeThickness();
}
