package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

/**
 * Defines how an {@link RangeSeriesStrokeMode} shape is outlined.
 */
public enum RangeSeriesStrokeMode {

    /**
     * No outlining.
     */
    NONE,

    /**
     * The path segment will have a stroke along the low values.
     */
    LOW_POINTS,

    /**
     * The path segment will have a stroke along the High values.
     */
    HIGH_POINTS,

    /**
     * The path segment will have a stroke along the low and high values.
     */
    LOW_AND_HIGH_POINTS
}
